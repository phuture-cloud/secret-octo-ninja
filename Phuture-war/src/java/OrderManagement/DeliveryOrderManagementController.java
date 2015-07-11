package OrderManagement;

import EntityManager.DeliveryOrder;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeliveryOrderManagementController extends HttpServlet {

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private DeliveryOrderManagementBeanLocal deliveryOrderManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to DeliveryOrderManagementController");
        String target = request.getParameter("target");
        String source = request.getParameter("source");

        String remarks = request.getParameter("remarks");
        String notes = request.getParameter("notes");
        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");
        String itemUnitPrice = request.getParameter("itemUnitPrice");

        String doNumber = request.getParameter("doNumber");
        String poNumber = request.getParameter("poNumber");
        String doDate = request.getParameter("doDate");
        if (doDate == null) {
            doDate = "";
        }
        String status = request.getParameter("status");
        if (status == null) {
            status = "";
        }

        String id;

        session = request.getSession();
        ReturnHelper returnHelper = null;
        DeliveryOrder deliveryOrder = (DeliveryOrder) (session.getAttribute("do"));

        try {
            if (checkLogin()) {
                switch (target) {
                    case "RetrieveDO":
                        id = request.getParameter("id");
                        if (id != null) {
                            session.setAttribute("do", deliveryOrderManagementBean.getDeliveryOrder(Long.parseLong(id)));
                            nextPage = "OrderManagement/doManagement.jsp";
                        }
                        break;

                    case "DeleteDO":
                        if (deliveryOrder != null) {
                            returnHelper = deliveryOrderManagementBean.deleteDeliveryOrder(deliveryOrder.getId(), isAdmin);
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));
                                session.removeAttribute("do");

                                nextPage = "OrderManagement/scoManagement_DO.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_DO.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "OrderManagement/scoManagement_DO.jsp?errMsg=Delete Delivery Order failed. An error has occured.";
                        }
                        break;

                    case "UpdateDO":
                        if (source.equals("AddLineItemToExistingDO")) {
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "OrderManagement/doManagement.jsp?doNumber=" + doNumber + "&doDate=" + doDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                        }
                        if (doNumber == null || doNumber.isEmpty() || doDate == null || doDate.isEmpty()) {
                            nextPage = "OrderManagement/doManagement.jsp?doNumber=" + doNumber + "&doDate=" + doDate + "&errMsg=Please fill in all the fields for the DO.";
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date doDateDate = formatter.parse(doDate);

                            //Update DO
                            returnHelper = deliveryOrderManagementBean.updateDeliveryOrder(deliveryOrder.getId(), doNumber, doDateDate, poNumber, status, isAdmin);
                            if (returnHelper.getResult()) {
                                Long doID = returnHelper.getID();
                                deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(doID);
                                session.setAttribute("do", deliveryOrder);
                                nextPage = "OrderManagement/doManagement.jsp?goodMsg=" + returnHelper.getDescription() + "&doNumber=" + doNumber;

                                //Update line item if there is any
                                if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                    returnHelper = deliveryOrderManagementBean.addDOlineItem(doID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                    deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(doID);
                                    if (returnHelper.getResult() && deliveryOrder != null) {
                                        session.setAttribute("do", deliveryOrder);
                                        nextPage = "OrderManagement/doManagement.jsp?goodMsg=" + returnHelper.getDescription() + "&doNumber=" + doNumber;
                                    } else {
                                        nextPage = "OrderManagement/doManagement.jsp?errMsg=" + returnHelper.getDescription() + "&doNumber=" + doNumber;
                                    }
                                }
                            } else {
                                nextPage = "OrderManagement/doManagement.jsp?doNumber=" + doNumber + "&doDate=" + doDate + "&errMsg=" + returnHelper.getDescription();
                                break;
                            }
                        }
                        break;

                    case "UpdateDONotes":
                        returnHelper = deliveryOrderManagementBean.updateDeliveryOrderNotes(deliveryOrder.getId(), notes, isAdmin);
                        deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                        if (returnHelper.getResult() && deliveryOrder != null) {
                            session.setAttribute("do", deliveryOrder);
                            nextPage = "OrderManagement/doManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/doManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "UpdateDORemarks":
                        returnHelper = deliveryOrderManagementBean.updateDeliveryOrderRemarks(deliveryOrder.getId(), remarks, isAdmin);
                        deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                        if (returnHelper.getResult() && deliveryOrder != null) {
                            session.setAttribute("do", deliveryOrder);
                            nextPage = "OrderManagement/doManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/doManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        String lineItemID = request.getParameter("lineItemID");
                        returnHelper = deliveryOrderManagementBean.deleteDOlineItem(deliveryOrder.getId(), Long.parseLong(lineItemID), isAdmin);
                        deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                        if (returnHelper.getResult() && deliveryOrder != null) {
                            session.setAttribute("do", deliveryOrder);
                            nextPage = "OrderManagement/doManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/doManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                }//end switch
            }//end checkLogin

            if (nextPage.equals("")) {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }
        } catch (Exception ex) {
            response.sendRedirect("OrderManagement/scoManagement_DO.jsp?errMsg=An error has occured");
            ex.printStackTrace();
            return;
        }

    }

    public boolean checkLogin() {
        try {
            Staff staff = (Staff) (session.getAttribute("staff"));
            if (staff.getIsAdmin()) {
                isAdmin = true;
                loggedInStaffID = staff.getId();
            }
            if (staff == null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
