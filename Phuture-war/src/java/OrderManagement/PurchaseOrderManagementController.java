package OrderManagement;

import EntityManager.PurchaseOrder;
import EntityManager.ReturnHelper;
import EntityManager.Staff;
import EntityManager.Supplier;
import EntityManager.SupplierContact;
import SupplierManagement.SupplierManagementBeanLocal;
import java.io.IOException;
import java.text.DateFormat;
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
    private SupplierManagementBeanLocal supplierManagementBean;

    @EJB
    private PurchaseOrderManagementBeanLocal purchaseOrderManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        PurchaseOrder purchaseOrder = (PurchaseOrder) (session.getAttribute("po"));

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
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                session.setAttribute("listOfPO", purchaseOrderManagementBean.listPurchaseOrdersTiedToSCO(Long.parseLong(id)));
                                session.setAttribute("previousManagementPage", "sco");
                                nextPage = "POManagement/purchaseOrders.jsp";
                            }
                        }
                        break;

                    case "RetrievePO":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                                if (suppliers == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                } else {
                                    session.setAttribute("suppliers", suppliers);
                                }
                                session.removeAttribute("supplierContacts");
                                session.setAttribute("po", purchaseOrderManagementBean.getPurchaseOrder(Long.parseLong(id)));
                                nextPage = "POManagement/purchaseOrder.jsp";
                            }
                        }
                        break;

                    case "ListAllSupplier":
                        if (true) {
                            List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                            if (suppliers == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("suppliers", suppliers);
                                String source = request.getParameter("source");
                                if (source != null && source.equals("addressBook")) {
                                    nextPage = "SupplierManagement/updateSupplierContact.jsp?previousPage=po";
                                } else {
                                    nextPage = "SupplierManagement/purchaseOrder.jsp";
                                }
                            }
                        }
                        break;

                    case "ListSupplierContacts":
                        if (true) {
                            String supplierID = request.getParameter("supplierID");
                            List<SupplierContact> supplierContacts = supplierManagementBean.listSupplierContacts(Long.parseLong(supplierID));
                            if (supplierContacts == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("supplierContacts", supplierContacts);
                                String source = request.getParameter("source");
                                if (source != null && source.equals("addressBook")) {
                                    nextPage = "SupplierManagement/updateSupplierContact.jsp?previousPage=po&selectedSupplierID=" + supplierID;
                                } else {
                                    String terms = request.getParameter("terms");
                                    String poDate = request.getParameter("poDate");
                                    if (poDate == null) {
                                        poDate = "";
                                    }
                                    String deliveryDate = request.getParameter("deliveryDate");
                                    if (deliveryDate == null) {
                                        deliveryDate = "";
                                    }
                                    String currency = request.getParameter("currency");
                                    nextPage = "POManagement/purchaseOrder.jsp?selectedSupplierID=" + supplierID + "&terms=" + terms + "&poDate=" + poDate + "&deliveryDate=" + deliveryDate + "&currency=" + currency;
                                }
                            }
                        }
                        break;

                    case "DeletePO":
                        if (purchaseOrder != null) {
                            returnHelper = purchaseOrderManagementBean.deletePurchaseOrder(purchaseOrder.getId());
                            if (returnHelper.getResult()) {
                                session.removeAttribute("po");
                                String previousMgtPage = (String) session.getAttribute("previousManagementPage");
                                if (previousMgtPage.equals("sco")) {
                                    session.setAttribute("listOfPO", purchaseOrderManagementBean.listPurchaseOrdersTiedToSCO(purchaseOrder.getSalesConfirmationOrder().getId()));
                                } else { //coming from po
                                    session.setAttribute("listOfPO", purchaseOrderManagementBean.listAllPurchaseOrder(loggedInStaffID));
                                }

                                nextPage = "POManagement/purchaseOrders.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "POManagement/purchaseOrders.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "POManagement/purchaseOrders.jsp?errMsg=Delete Purchase Order failed. An error has occured.";
                        }
                        break;

                    case "UpdatePO":
                        if (true) {
                            String poDate = request.getParameter("poDate");
                            if (poDate == null) {
                                poDate = "";
                            }
                            String status = request.getParameter("status");
                            if (status == null) {
                                status = "";
                            }
                            String supplierContactID = request.getParameter("supplierContactID");
                            String terms = request.getParameter("terms");
                            String currency = request.getParameter("currency");
                            String deliveryDateString = request.getParameter("deliveryDate");

                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");

                            String source = request.getParameter("source");

                            if (source.equals("AddLineItemToExistingPO")) {
                                if (supplierContactID == null || supplierContactID.isEmpty() || itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                    nextPage = "POManagement/purchaseOrder.jsp?poDate=" + poDate + "&status=" + status + "&terms=" + terms + "&deliveryDate=" + deliveryDateString + "&currency=" + currency + "&errMsg=Please fill in all the fields for the item.";
                                    break;
                                }
                            }

                            if (purchaseOrder.getSupplierLink() != null) {
                                supplierContactID = purchaseOrder.getSupplierLink().getId().toString();
                                if (currency == null || currency.isEmpty() || poDate.isEmpty()) {
                                    nextPage = "POManagement/purchaseOrder.jsp?poDate=" + poDate + "&status=" + status + "&terms=" + terms + "&deliveryDate=" + deliveryDateString + "&currency=" + currency + "&errMsg=Please fill in all the fields for the PO.";
                                    break;
                                }
                            } else {
                                if (supplierContactID == null || supplierContactID.isEmpty() || currency == null || currency.isEmpty() || poDate.isEmpty()) {
                                    nextPage = "POManagement/purchaseOrder.jsp?poDate=" + poDate + "&status=" + status + "&terms=" + terms + "&deliveryDate=" + deliveryDateString + "&currency=" + currency + "&errMsg=Please fill in all the fields for the PO.";
                                    break;
                                }
                            }

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date poDateDate = formatter.parse(poDate);

                            String remarks = request.getParameter("remarks");

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date deliveryDate = dateFormat.parse(deliveryDateString);

                            //Update PO
                            returnHelper = purchaseOrderManagementBean.updatePurchaseOrder(purchaseOrder.getId(), Long.parseLong(supplierContactID), poDateDate, status, terms, deliveryDate, remarks, currency);
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
                        String notes = request.getParameter("notes");
                        returnHelper = purchaseOrderManagementBean.updatePurchaseOrderNotes(purchaseOrder.getId(), notes);
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "UpdatePORemarks":
                        String remarks = request.getParameter("remarks");
                        returnHelper = purchaseOrderManagementBean.updatePurchaseOrderRemarks(purchaseOrder.getId(), remarks);
                        purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                        if (returnHelper.getResult() && purchaseOrder != null) {
                            session.setAttribute("po", purchaseOrder);
                            nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        if (true) {
                            String lineItemID = request.getParameter("lineItemID");

                            returnHelper = purchaseOrderManagementBean.deletePOlineItem(purchaseOrder.getId(), Long.parseLong(lineItemID));
                            purchaseOrder = purchaseOrderManagementBean.getPurchaseOrder(purchaseOrder.getId());
                            if (returnHelper.getResult() && purchaseOrder != null) {
                                session.setAttribute("po", purchaseOrder);
                                nextPage = "POManagement/purchaseOrder.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "POManagement/purchaseOrder.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "EditLineItem":
                        //Check for empty fields
                        if (true) {
                            String lineItemID = request.getParameter("lineItemID");
                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");

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
                        }
                        break;

                }//end switch
            } else {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("POManagement/purchaseOrders.jsp?errMsg=An error has occured");
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
            if (staff == null) {
                return false;
            } else {
                if (staff.getIsAdmin()) {
                    loggedInStaffID = staff.getId();
                }
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
