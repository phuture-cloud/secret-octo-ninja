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

        String notes = request.getParameter("notes");
        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");
        String itemUnitPrice = request.getParameter("itemUnitPrice");

        String poNumber = request.getParameter("poNumber");
        String poDate = request.getParameter("poDate");
        if (poDate == null) {
            poDate = "";
        }
        String status = request.getParameter("status");
        if (status == null) {
            status = "";
        }

        String id;
        String lineItemID = request.getParameter("lineItemID");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        PurchaseOrder purchaseOrder = (PurchaseOrder) (session.getAttribute("po"));
        
//        String previousManagementPage = request.getParameter("previousManagementPage");
//        if (previousManagementPage != null && !previousManagementPage.isEmpty()) {
//            session.setAttribute("previousManagementPage", previousManagementPage);
//        }
        
        try {
            if (checkLogin()) {
                switch (target) {
                    case "ListAllPO":
                        List<PurchaseOrder> purchaseOrders = purchaseOrderManagementBean.listAllPurchaseOrder(loggedInStaffID);
                        session.setAttribute("listOfPO", purchaseOrders);
                        session.setAttribute("previousManagementPage", "purchaseOrders");
                        nextPage = "POManagement/purchaseOrders.jsp";
                        break;

                    case "ListPoTiedToSCO":
                        id = request.getParameter("id");
                        if (id != null) {
                            session.setAttribute("listOfPO", purchaseOrderManagementBean.listPurchaseOrdersTiedToSCO(Long.parseLong(id)));
                            session.setAttribute("previousManagementPage", "sco");
                            nextPage = "POManagement/purchaseOrders.jsp";
                        }
                        break;

                    case "RetrievePO":
                        id = request.getParameter("id");
                        if (id != null) {
                            session.setAttribute("po", purchaseOrderManagementBean.getPurchaseOrder(Long.parseLong(id)));
                            nextPage = "POManagement/purchaseOrder.jsp";
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

                                nextPage = "POManagement/purchaseOrders.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "POManagement/purchaseOrders.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "POManagement/purchaseOrders.jsp?errMsg=Delete Purchase Order failed. An error has occured.";
                        }
                        break;

                    case "UpdatePO":
                        if (source.equals("AddLineItemToExistingPO")) {
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "POManagement/purchaseOrder.jsp?poNumber=" + poNumber + "&poDate=" + poDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                        }
                        if (poNumber == null || poNumber.isEmpty() || poDate == null || poDate.isEmpty()) {
                            nextPage = "POManagement/purchaseOrder.jsp?poNumber=" + poNumber + "&poDate=" + poDate + "&errMsg=Please fill in all the fields for the PO.";
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date poDateDate = formatter.parse(poDate);
                            String supplierName = request.getParameter("supplierName");
                            String supplierEmail = request.getParameter("supplierEmail");
                            String supplierOfficeNo = request.getParameter("supplierOfficeNo");
                            String supplierMobileNo = request.getParameter("supplierMobileNo");
                            String supplierFaxNo = request.getParameter("supplierFaxNo");
                            String supplierAddress = request.getParameter("supplierAddress");

                            //Update PO
                            returnHelper = purchaseOrderManagementBean.updatePurchaseOrder(purchaseOrder.getId(), poNumber, poDateDate, status, supplierName, supplierEmail, supplierOfficeNo, supplierMobileNo, supplierFaxNo, supplierAddress);
                            if (returnHelper.getResult()) {
                                Long poID = returnHelper.getID();
                                purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(poID);
                                session.setAttribute("po", purchaseOrder);
                                nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();

                                //Update line item if there is any
                                if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                    returnHelper = purchaseOrderManagementBean.addPOlineItem(poID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice));
                                    purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(poID);
                                    if (returnHelper.getResult() && purchaseOrder != null) {
                                        session.setAttribute("po", purchaseOrder);
                                        nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                                    } else {
                                        nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                                    }
                                }
                            } else {
                                nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                                break;
                            }
                        }
                        break;

                    case "UpdatePONotes":
                        returnHelper = purchaseOrderManagementBean.updatePurchaseOrderNotes(purchaseOrder.getId(), notes);
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        returnHelper = purchaseOrderManagementBean.deletePOlineItem(purchaseOrder.getId(), Long.parseLong(lineItemID));
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "EditLineItem":
                        //Check for empty fields
                        if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                            nextPage = "POManagement/purchaseOrder.jsp?errMsg=Please fill in all the fields for the item.";
                            break;
                        }

                        //Edit line item
                        returnHelper = purchaseOrderManagementBean.updatePOlineItem(purchaseOrder.getId(), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice));
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
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
            response.sendRedirect("POManagement/purchaseOrders.jsp?errMsg=An error has occured");
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
