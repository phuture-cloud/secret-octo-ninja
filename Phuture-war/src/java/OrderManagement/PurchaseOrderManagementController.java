package OrderManagement;

import EntityManager.PurchaseOrder;
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

public class PurchaseOrderManagementController extends HttpServlet {

    @EJB
    private PurchaseOrderManagementBeanLocal purchaseOrderManagementBean;

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
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

//        String doNumber = request.getParameter("doNumber");
//        String poNumber = request.getParameter("poNumber");
//        String doDate = request.getParameter("doDate");
//        if (doDate == null) {
//            doDate = "";
//        }
//        String status = request.getParameter("status");
//        if (status == null) {
//            status = "";
//        }
        String id;
        String lineItemID = request.getParameter("lineItemID");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        PurchaseOrder purchaseOrder = (PurchaseOrder) (session.getAttribute("po"));

        try {
            if (checkLogin()) {
                switch (target) {
                    case "RetrievePO":
                        id = request.getParameter("id");
                        if (id != null) {
                            session.setAttribute("po", purchaseOrderManagementBean.getPurchaseOrder(Long.parseLong(id)));
                            nextPage = "OrderManagement/poManagement.jsp";
                        }
                        break;

                    case "DeletePO":
                        if (purchaseOrder != null) {
                            returnHelper = purchaseOrderManagementBean.deletePurchaseOrder(purchaseOrder.getId());
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));
                                session.removeAttribute("po");

                                nextPage = "OrderManagement/scoManagement_PO.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_PO.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "OrderManagement/scoManagement_PO.jsp?errMsg=Delete Delivery Order failed. An error has occured.";
                        }
                        break;

                    case "UpdatePONotes":
                        returnHelper = purchaseOrderManagementBean.updatePurchaseOrderNotes(purchaseOrder.getId(), notes);
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "OrderManagement/poManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/poManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        returnHelper = purchaseOrderManagementBean.deletePOlineItem(purchaseOrder.getId(), Long.parseLong(lineItemID));
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "OrderManagement/poManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/poManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "EditLineItem":
                        //Check for empty fields
                        if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                            nextPage = "OrderManagement/poManagement.jsp?errMsg=Please fill in all the fields for the item.";
                            break;
                        }

                        //Edit line item
                        returnHelper = purchaseOrderManagementBean.updatePOlineItem(purchaseOrder.getId(), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice));
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "OrderManagement/poManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/poManagement.jsp?errMsg=" + returnHelper.getDescription();
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