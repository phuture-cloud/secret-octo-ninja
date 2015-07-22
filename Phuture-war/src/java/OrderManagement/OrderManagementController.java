package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
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

public class OrderManagementController extends HttpServlet {

    @EJB
    private InvoiceManagementBeanLocal invoiceManagementBean;

    @EJB
    private PurchaseOrderManagementBeanLocal purchaseOrderManagementBean;

    @EJB
    private DeliveryOrderManagementBeanLocal deliveryOrderManagementBean;

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String id = request.getParameter("id");
        String scoNumber = request.getParameter("scoNumber");

        String customerID = request.getParameter("customerID");
        String contactID = request.getParameter("contactID");
        String lineItemID = request.getParameter("lineItemID");
        String scoDate = request.getParameter("scoDate");
        if (scoDate == null) {
            scoDate = "";
        }
        String estimatedDeliveryDate = request.getParameter("estimatedDeliveryDate");
        String poNumber = request.getParameter("poNumber");
        String terms = request.getParameter("terms");
        if (terms == null) {
            terms = "";
        }
        String status = request.getParameter("status");
        if (status == null) {
            status = "";
        }

        String remarks = request.getParameter("remarks");
        String notes = request.getParameter("notes");
        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");
        String itemUnitPrice = request.getParameter("itemUnitPrice");
        String source = request.getParameter("source");

        String company = request.getParameter("company");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String officeNo = request.getParameter("officeNo");
        String mobileNo = request.getParameter("mobileNo");
        String faxNo = request.getParameter("faxNo");
        String address = request.getParameter("address");

        String doNumber = request.getParameter("doNumber");
        String doDate = request.getParameter("doDate");
        if (doDate == null) {
            doDate = "";
        }

        session = request.getSession();
        ReturnHelper returnHelper = null;
        Boolean isAdmin = false;

        try {
            //Following functions are only accessible if logged in
            if (checkLogin()) {
                //Set the admin flag so he can overwrite some functionalities
                Staff staff = (Staff) (session.getAttribute("staff"));
                if (staff.getIsAdmin()) {
                    isAdmin = true;
                }
                Long loggedInStaffID = staff.getId();

                switch (target) {
                    case "ListAllSCO":
                        if (true) {
                            List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                            if (salesConfirmationOrders == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                nextPage = "OrderManagement/scoManagement.jsp";
                            }
                            session.removeAttribute("contacts");
                            session.removeAttribute("sco");
                            session.removeAttribute("do");
                            session.removeAttribute("po");
                            session.removeAttribute("invoice");
                        }
                        break;

                    case "ListAllCustomer":
                        if (true) {
                            List<Customer> customers = customerManagementBean.listCustomers();
                            if (customers == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("customers", customers);
                                if (source != null && source.equals("addressBook")) {
                                    nextPage = "OrderManagement/updateContact.jsp?previousPage=sco&id=" + id;
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp";
                                }
                            }
                        }
                        break;

                    case "ListCustomerContacts":
                        if (true) {
                            List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(customerID));
                            if (contacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("contacts", contacts);
                                if (source != null && source.equals("addressBook")) {
                                    nextPage = "OrderManagement/updateContact.jsp?previousPage=sco&id=" + id + "&selectedCustomerID=" + customerID;
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&terms=" + terms + "&scoDate=" + scoDate + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&poNumber=" + poNumber;
                                }
                            }
                        }
                        break;

                    case "SaveSCO":
                        if (true) {
                            if (source.equals("AddLineItemToNewSCO")) {
                                if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedContactID=" + contactID + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                    break;
                                }
                            }
                            if (scoNumber == null || scoNumber.isEmpty() || scoDate == null || scoDate.isEmpty() || terms == null || terms.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedContactID=" + contactID + "&terms=" + terms + "&scoDate=" + scoDate + "&id=" + id + "&errMsg=Please fill in all the fields for the SCO.";
                            } else {

                                DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date scoDateDate = sourceFormat.parse(scoDate);

                                //Create SCO
                                returnHelper = orderManagementBean.createSalesConfirmationOrder(scoNumber, scoDateDate, estimatedDeliveryDate, poNumber, Long.parseLong(customerID), Long.parseLong(contactID), loggedInStaffID, Integer.parseInt(terms));
                                if (returnHelper.getResult()) {
                                    Long scoID = returnHelper.getID();
                                    SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(scoID);

                                    session.setAttribute("sco", sco);
                                    //Add line item if already filled in
                                    if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                        returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                        if (returnHelper.getResult()) {
                                            session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(scoID));
                                            nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                        } else {
                                            nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&selectedContactID=" + contactID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                        }
                                    } else {
                                        nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                        break;
                                    }
                                } else { // Error in creating SCO
                                    nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&scoNumber=" + scoNumber + "&scoDate=" + scoDate + "&terms=" + terms;
                                }
                            }
                        }
                        break;

                    case "UpdateSCO":
                        if (true) {
                            if (source.equals("AddLineItemToExistingSCO")) {
                                if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                    break;
                                }
                            }
                            if (scoNumber == null || scoNumber.isEmpty() || scoDate == null || scoDate.isEmpty() || terms == null || terms.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedContactID=" + contactID + "&terms=" + terms + "&scoDate=" + scoDate + "&id=" + id + "&errMsg=Please fill in all the fields for the SCO.";
                            } else {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date scoDateDate = formatter.parse(scoDate);

                                //Update SCO
                                returnHelper = orderManagementBean.updateSalesConfirmationOrder(Long.parseLong(id), scoNumber, scoDateDate, estimatedDeliveryDate, poNumber, Long.parseLong(customerID), status, Integer.parseInt(terms), isAdmin);
                                if (returnHelper.getResult()) {

                                    SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(returnHelper.getID());
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&scoNumber=" + scoNumber + "&id=" + returnHelper.getID();

                                    Long scoID = returnHelper.getID();
                                    //Update line item if there is any
                                    if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                        returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                        sco = orderManagementBean.getSalesConfirmationOrder(scoID);
                                        if (returnHelper.getResult() && sco != null) {
                                            session.setAttribute("sco", sco);
                                            nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                        } else {
                                            nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                        }
                                    }
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=" + returnHelper.getDescription();
                                    break;
                                }
                            }
                        }
                        break;

                    case "UpdateSCOContact":
                        if (true) {
                            //if address book
                            if (source != null && source.equals("UpdateContact")) {
                                returnHelper = orderManagementBean.updateSalesConfirmationOrderCustomerContactDetails(Long.parseLong(id), Long.parseLong(customerID), Long.parseLong(contactID), isAdmin);
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                if (returnHelper.getResult() && sco != null) {
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                                }
                                //manual key in
                            } else if (company != null && !company.isEmpty() && name != null && !name.isEmpty() && address != null && !address.isEmpty() && officeNo != null && !officeNo.isEmpty() && email != null && !email.isEmpty()) {
                                returnHelper = orderManagementBean.updateSalesConfirmationOrderCustomerContactDetails(Long.parseLong(id), company, name, email, officeNo, mobileNo, faxNo, address, isAdmin);
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                if (returnHelper.getResult() && sco != null) {
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=Please fill in all the required fields for edit contact";
                                }
                            }
                        }
                        break;

                    case "UpdateSCONotes":
                        if (true) {
                            returnHelper = orderManagementBean.updateSalesConfirmationOrderNotes(Long.parseLong(id), notes, isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "UpdateSCORemarks":
                        if (true) {
                            returnHelper = orderManagementBean.updateSalesConfirmationOrderRemarks(Long.parseLong(id), remarks, isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "RetrieveSCO":
                        if (true) {
                            List<Customer> customers = customerManagementBean.listCustomers();
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (customers == null || sco == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("customers", customers);
                                session.setAttribute("sco", sco);
                                if (source != null && source.equals("listAllInvoice")) {
                                    nextPage = "OrderManagement/scoManagement_invoice.jsp";
                                } else if (source != null && source.equals("listAllDO")) {
                                    nextPage = "OrderManagement/scoManagement_DO.jsp";
                                } else if (source != null && source.equals("listAllPO")) {
                                    //remove
                                    nextPage = "OrderManagement/scoManagement_PO.jsp";
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id;
                                }
                            }
                        }
                        break;

                    case "PrintPDF":
                        if (true) {
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (sco == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/sco-print.jsp?id=" + id;
                            }
                        }
                        break;

                    case "EditLineItem":
                        if (true) {
                            //Check for empty fields
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&terms=" + terms + "&editingLineItem=" + lineItemID + "&scoDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }

                            //Edit line item
                            returnHelper = orderManagementBean.updateSCOlineItem(Long.parseLong(id), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&terms=" + terms + "&scoDate=" + scoDate + "&goodMsg=" + returnHelper.getDescription();

                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&status=" + status + "&editingLineItem=" + lineItemID + "&errMsg=" + returnHelper.getDescription();
                            }

                        }
                        break;

                    case "RemoveLineItem":
                        if (true) {
                            returnHelper = orderManagementBean.deleteSCOlineItem(Long.parseLong(id), Long.parseLong(lineItemID), isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&terms=" + terms + "&scoDate=" + scoDate + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "DeleteSCO":
                        if (true) {
                            returnHelper = orderManagementBean.deleteSalesConfirmationOrder(Long.parseLong(id), isAdmin);
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                session.removeAttribute("sco");
                                nextPage = "OrderManagement/scoManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "GenerateDO":
                        if (true) {
                            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date doDateDate = sourceFormat.parse(doDate);

                            returnHelper = deliveryOrderManagementBean.createDeliveryOrder(Long.parseLong(id), doNumber, doDateDate);
                            if (returnHelper.getResult()) {
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                session.setAttribute("sco", sco);
                                //session.setAttribute("do", deliveryOrderManagementBean.getDeliveryOrder(returnHelper.getID()));
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "GeneratePO":
                        if (true) {
                            String poDate = request.getParameter("poDate");
                            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date poDateDate = sourceFormat.parse(poDate);

                            returnHelper = purchaseOrderManagementBean.createPurchaseOrder(Long.parseLong(id), poNumber, poDateDate);
                            if (returnHelper.getResult()) {
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                session.setAttribute("sco", sco);
                                //session.setAttribute("po", purchaseOrderManagementBean.getPurchaseOrder(returnHelper.getID()));
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "GenerateInvoice":
                        if (true) {
                            String invoiceNumber = request.getParameter("invoiceNumber");
                            returnHelper = invoiceManagementBean.createInvoice(Long.parseLong(id), invoiceNumber);
                            if (returnHelper.getResult()) {
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    default:
                        System.out.println("OrderManagementController: Unknown target value.");
                        break;
                }
            }

            if (nextPage.equals("")) {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }
        } catch (Exception ex) {
            response.sendRedirect("OrderManagement/scoManagement.jsp?errMsg=An error has occured");
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
