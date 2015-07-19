package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.Invoice;
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

public class InvoiceManagementController extends HttpServlet {

    @EJB
    private InvoiceManagementBeanLocal invoiceManagementBean;

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to InvoiceManagementController");
        String target = request.getParameter("target");
        String source = request.getParameter("source");

        String remarks = request.getParameter("remarks");
        String notes = request.getParameter("notes");
        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");
        String itemUnitPrice = request.getParameter("itemUnitPrice");

        String invoiceNumber = request.getParameter("invoiceNumber");
        String poNumber = request.getParameter("poNumber");
        String status = request.getParameter("status");
        if (status == null) {
            status = "";
        }

        String customerID = "";
        String lineItemID = request.getParameter("lineItemID");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        Invoice invoice = (Invoice) (session.getAttribute("invoice"));

        try {
            if (checkLogin()) {
                switch (target) {
                    case "ListInvoiceTiedToSCO":
                        String id = request.getParameter("id");
                        if (id != null) {
                            session.setAttribute("listOfInvoice", invoiceManagementBean.listInvoicesTiedToSCO(Long.parseLong(id)));
                            nextPage = "OrderManagement/scoManagement_invoice.jsp";
                        }
                        break;
                    case "RetrieveInvoice":
                        id = request.getParameter("id");
                        if (id != null) {
                            session.setAttribute("invoice", invoiceManagementBean.getInvoice(Long.parseLong(id)));
                            nextPage = "OrderManagement/invoiceManagement.jsp";
                        }
                        break;

                    case "DeleteInvoice":
                        if (invoice != null) {
                            returnHelper = invoiceManagementBean.deleteInvoice(invoice.getId(), isAdmin);
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));
                                session.removeAttribute("invoice");

                                nextPage = "OrderManagement/scoManagement_invoice.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_invoice.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "OrderManagement/scoManagement_invoice.jsp?errMsg=Delete Delivery Invoice failed. An error has occured.";
                        }
                        break;

                    case "UpdateInvoice":
                        String invoiceCreated = request.getParameter("invoiceCreated");
                        String invoiceSent = request.getParameter("invoiceSent");
                        String invoicePaid = request.getParameter("invoicePaid");
                        String estimatedDeliveryDate = request.getParameter("estimatedDeliveryDate");

                        if (source.equals("AddLineItemToExistingInvoice")) {
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "OrderManagement/invoiceManagement.jsp?invoiceNumber=" + invoiceNumber + "&invoiceCreated=" + invoiceCreated + "&invoiceSent=" + invoiceSent + "&invoicePaid=" + invoicePaid + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                        }
                        if (invoiceNumber == null || invoiceNumber.isEmpty() || invoiceCreated == null || invoiceCreated.isEmpty()) {
                            nextPage = "OrderManagement/invoiceManagement.jsp?invoiceNumber=" + invoiceNumber + "&invoiceCreated=" + invoiceCreated + "&invoiceSent=" + invoiceSent + "&invoicePaid=" + invoicePaid + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&errMsg=Please fill in all the fields for the Invoice.";
                        } else {
                            String terms = request.getParameter("terms");
                            Integer intTerms = null;
                            if (terms != null) {
                                intTerms = Integer.parseInt(terms);
                            }

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date invoiceCreatedDateDate = formatter.parse(invoiceCreated);
                            Date invoiceSentDateDate = null;
                            if (invoiceSent != null && !invoiceSent.isEmpty()) {
                                invoiceSentDateDate = formatter.parse(invoiceSent);
                            }
                            Date invoicePaidDateDate = null;
                            if (invoicePaid != null && !invoicePaid.isEmpty()) {
                                invoicePaidDateDate = formatter.parse(invoicePaid);
                            }

                            //Update Invoice
                            returnHelper = invoiceManagementBean.updateInvoice(invoice.getId(), invoiceNumber, invoiceCreatedDateDate, invoiceSentDateDate, invoicePaidDateDate, estimatedDeliveryDate, intTerms, poNumber, isAdmin);
                            if (returnHelper.getResult()) {
                                Long invoiceID = returnHelper.getID();
                                invoice = invoiceManagementBean.getInvoice(invoiceID);
                                session.setAttribute("invoice", invoice);
                                nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();

                                //Update line item if there is any
                                if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                    returnHelper = invoiceManagementBean.addInvoiceLineItem(invoiceID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                    invoice = invoiceManagementBean.getInvoice(invoiceID);
                                    if (returnHelper.getResult() && invoice != null) {
                                        session.setAttribute("invoice", invoice);
                                        nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                                    } else {
                                        nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                                    }
                                }
                            } else {
                                nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                                break;
                            }
                        }
                        break;

                    case "UpdateInvoiceNotes":
                        returnHelper = invoiceManagementBean.updateInvoiceNotes(invoice.getId(), notes, isAdmin);
                        invoice = invoiceManagementBean.getInvoice(invoice.getId());
                        if (returnHelper.getResult() && invoice != null) {
                            session.setAttribute("invoice", invoice);
                            nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "UpdateInvoiceRemarks":
                        returnHelper = invoiceManagementBean.updateInvoiceRemarks(invoice.getId(), remarks, isAdmin);
                        invoice = invoiceManagementBean.getInvoice(invoice.getId());
                        if (returnHelper.getResult() && invoice != null) {
                            session.setAttribute("invoice", invoice);
                            nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        returnHelper = invoiceManagementBean.deleteInvoiceLineItem(invoice.getId(), Long.parseLong(lineItemID), isAdmin);
                        invoice = invoiceManagementBean.getInvoice(invoice.getId());
                        if (returnHelper.getResult() && invoice != null) {
                            session.setAttribute("invoice", invoice);
                            nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "EditLineItem":
                        //Check for empty fields
                        if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                            nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=Please fill in all the fields for the item.";
                            break;
                        }

                        //Edit line item
                        returnHelper = invoiceManagementBean.updateInvoiceLineItem(invoice.getId(), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                        invoice = invoiceManagementBean.getInvoice(invoice.getId());
                        if (returnHelper.getResult() && invoice != null) {
                            session.setAttribute("invoice", invoice);
                            nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "UpdateInvoiceContact":
                        if (source != null && source.equals("UpdateContact")) {
                            customerID = request.getParameter("customerID");
                            String contactID = request.getParameter("contactID");
                            returnHelper = invoiceManagementBean.updateInvoiceCustomerContactDetails(invoice.getId(), Long.parseLong(customerID), Long.parseLong(contactID), isAdmin);
                            invoice = invoiceManagementBean.getInvoice(invoice.getId());
                            if (returnHelper.getResult() && invoice != null) {
                                session.setAttribute("invoice", invoice);
                                nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                            //manual key in
                        } else if (source == null) {
                            String company = request.getParameter("company");
                            String name = request.getParameter("name");
                            String email = request.getParameter("email");
                            String officeNo = request.getParameter("officeNo");
                            String mobileNo = request.getParameter("mobileNo");
                            String faxNo = request.getParameter("faxNo");
                            String address = request.getParameter("address");

                            if (company != null && !company.isEmpty() && name != null && !name.isEmpty() && address != null && !address.isEmpty() && officeNo != null && !officeNo.isEmpty() && email != null && !email.isEmpty()) {
                                returnHelper = invoiceManagementBean.updateInvoiceCustomerContactDetails(invoice.getId(), company, name, email, officeNo, mobileNo, faxNo, address, isAdmin);
                                invoice = invoiceManagementBean.getInvoice(invoice.getId());
                                if (returnHelper.getResult() && invoice != null) {
                                    session.setAttribute("invoice", invoice);
                                    nextPage = "OrderManagement/invoiceManagement.jsp?goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/invoiceManagement.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            }
                        }
                        break;

                    case "ListAllCustomer":
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("customers", customers);
                            nextPage = "OrderManagement/updateContact.jsp";
                        }
                        break;

                    case "ListCustomerContacts":
                        customerID = request.getParameter("customerID");
                        List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(customerID));
                        if (contacts == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("contacts", contacts);
                            if (source != null && source.equals("addressBook")) {
                                nextPage = "OrderManagement/updateContact.jsp?selectedCustomerID=" + customerID;
                            }
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
            response.sendRedirect("OrderManagement/scoManagement_invoice.jsp?errMsg=An error has occured");
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
