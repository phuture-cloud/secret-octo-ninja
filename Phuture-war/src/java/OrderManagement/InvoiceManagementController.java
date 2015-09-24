package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.Invoice;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import PaymentManagement.PaymentManagementBeanLocal;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @EJB
    private PaymentManagementBeanLocal paymentManagementBeanLocal;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String source = request.getParameter("source");

        String poNumber = request.getParameter("poNumber");
        String status = request.getParameter("status");
        if (status == null) {
            status = "";
        }

        session = request.getSession();
        ReturnHelper returnHelper = null;
        Invoice invoice = (Invoice) (session.getAttribute("invoice"));

        String previousManagementPage = request.getParameter("previousManagementPage");
        if (previousManagementPage != null && !previousManagementPage.isEmpty()) {
            session.setAttribute("previousManagementPage", previousManagementPage);
        }

        try {
            if (checkLogin()) {
                switch (target) {
                    case "ListAllInvoice":
                        if (true) {
                            List<Invoice> invoices = invoiceManagementBean.listAllInvoice(loggedInStaffID);
                            if (invoices == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("listOfInvoice", invoices);
                                session.setAttribute("previousManagementPage", "invoices");
                                nextPage = "InvoiceManagement/invoices.jsp";
                            }
                        }
                        break;
                    case "ListInvoiceTiedToSCO":
                        if (true) {
                            SalesConfirmationOrder sco = (SalesConfirmationOrder) session.getAttribute("sco");
                            if (sco != null) {
                                session.setAttribute("listOfInvoice", invoiceManagementBean.listInvoicesTiedToSCO(sco.getId()));
                                session.setAttribute("previousManagementPage", "sco");
                                nextPage = "InvoiceManagement/invoices.jsp";
                            }
                            break;
                        }
                    case "RetrieveInvoice":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                invoice = invoiceManagementBean.getInvoice(Long.parseLong(id));
                                if (invoice == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                }
                                session.setAttribute("invoice", invoice);
                                session.setAttribute("invoicePayments", paymentManagementBeanLocal.listPaymentByInvoice(Long.parseLong(id)));
                                session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                nextPage = "InvoiceManagement/invoice.jsp";
                            }
                            break;
                        }
                    case "DeleteInvoice":
                        if (invoice != null) {
                            returnHelper = invoiceManagementBean.deleteInvoice(invoice.getId(), isAdmin);
                            if (returnHelper.getResult()) {
                                session.removeAttribute("invoice");
                                nextPage = "InvoiceManagement/invoices.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoices.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "InvoiceManagement/invoices.jsp?errMsg=Voiding of invoice failed. An error has occured.";
                        }
                        break;
                    case "VoidInvoice":
                        if (invoice != null) {
                            returnHelper = invoiceManagementBean.voidInvoice(invoice.getId(), isAdmin);
                            if (returnHelper.getResult()) {
                                List<Invoice> invoices = new ArrayList();
                                String previousMgtPage = (String) session.getAttribute("previousManagementPage");
                                if (previousMgtPage.equals("invoices")) {
                                    invoices = invoiceManagementBean.listAllInvoice(loggedInStaffID);
                                } else if (previousMgtPage.equals("sco")) {
                                    SalesConfirmationOrder sco = (SalesConfirmationOrder) session.getAttribute("sco");
                                    invoices = invoiceManagementBean.listInvoicesTiedToSCO(sco.getId());
                                }
                                if (invoices == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                }
                                session.setAttribute("listOfInvoice", invoices);
                                SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));
                                session.removeAttribute("invoice");

                                nextPage = "InvoiceManagement/invoices.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoices.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "InvoiceManagement/invoices.jsp?errMsg=Voiding of invoice failed. An error has occured.";
                        }
                        break;

                    case "UpdateInvoice":
                        if (true) {
                            String invoiceSent = request.getParameter("invoiceSent");
                            String invoicePaid = request.getParameter("invoicePaid");
                            String estimatedDeliveryDateString = request.getParameter("estimatedDeliveryDate");
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date estimatedDeliveryDate = dateFormat.parse(estimatedDeliveryDateString);

                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");

                            if (source.equals("AddLineItemToExistingInvoice")) {
                                if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                    nextPage = "InvoiceManagement/invoice.jsp?invoiceSent=" + invoiceSent + "&invoicePaid=" + invoicePaid + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&errMsg=Please fill in all the fields for the item.";
                                    break;
                                }
                            }

                            if (invoiceSent == null && invoiceSent.isEmpty()) {
                                nextPage = "InvoiceManagement/invoice.jsp?invoiceSent=" + invoiceSent + "&invoicePaid=" + invoicePaid + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&errMsg=Invoice date cannot be empty.";
                            } else {
                                String terms = request.getParameter("terms");
                                Integer intTerms = null;
                                if (terms != null) {
                                    intTerms = Integer.parseInt(terms);
                                }

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                Date invoiceSentDateDate = null;
                                if (!invoiceSent.isEmpty()) {
                                    invoiceSentDateDate = formatter.parse(invoiceSent);
                                } else {
                                    invoiceSentDateDate = null;
                                }

                                Date invoicePaidDateDate = null;
                                if (invoicePaid != null && !invoicePaid.isEmpty()) {
                                    invoicePaidDateDate = formatter.parse(invoicePaid);
                                } else {
                                    invoicePaidDateDate = null;
                                }

                                //Update Invoice
                                returnHelper = invoiceManagementBean.updateInvoice(invoice.getId(), invoiceSentDateDate, invoicePaidDateDate, estimatedDeliveryDate, intTerms, poNumber, isAdmin);
                                if (returnHelper.getResult()) {
                                    Long invoiceID = returnHelper.getID();
                                    invoice = invoiceManagementBean.getInvoice(invoiceID);
                                    session.setAttribute("invoice", invoice);
                                    session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));

                                    List<Invoice> invoices = invoiceManagementBean.listAllInvoice(loggedInStaffID);
                                    session.setAttribute("listOfInvoice", invoices);
                                    nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();

                                    //Update line item if there is any
                                    if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                        returnHelper = invoiceManagementBean.addInvoiceLineItem(invoiceID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                        invoice = invoiceManagementBean.getInvoice(invoiceID);
                                        if (returnHelper.getResult() && invoice != null) {
                                            session.setAttribute("invoice", invoice);
                                            nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                                        } else {
                                            nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                                        }
                                    }
                                } else {
                                    nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                                    break;
                                }
                            }
                        }
                        break;

                    case "UpdateInvoiceNotes":
                        String notes = request.getParameter("notes");
                        returnHelper = invoiceManagementBean.updateInvoiceNotes(invoice.getId(), notes, isAdmin);
                        invoice = invoiceManagementBean.getInvoice(invoice.getId());
                        if (returnHelper.getResult() && invoice != null) {
                            session.setAttribute("invoice", invoice);
                            session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                            nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "UpdateInvoiceRemarks":
                        String remarks = request.getParameter("remarks");
                        returnHelper = invoiceManagementBean.updateInvoiceRemarks(invoice.getId(), remarks, isAdmin);
                        invoice = invoiceManagementBean.getInvoice(invoice.getId());
                        if (returnHelper.getResult() && invoice != null) {
                            session.setAttribute("invoice", invoice);
                            session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                            nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        if (true) {
                            String lineItemID = request.getParameter("lineItemID");
                            returnHelper = invoiceManagementBean.deleteInvoiceLineItem(invoice.getId(), Long.parseLong(lineItemID), isAdmin);
                            invoice = invoiceManagementBean.getInvoice(invoice.getId());
                            if (returnHelper.getResult() && invoice != null) {
                                session.setAttribute("invoice", invoice);
                                session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "EditLineItem":
                        if (true) {
                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");
                            //Check for empty fields
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "InvoiceManagement/invoice.jsp?errMsg=Please fill in all the fields for the item.";
                                break;
                            }

                            //Edit line item
                            String lineItemID = request.getParameter("lineItemID");
                            returnHelper = invoiceManagementBean.updateInvoiceLineItem(invoice.getId(), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                            invoice = invoiceManagementBean.getInvoice(invoice.getId());
                            if (returnHelper.getResult() && invoice != null) {
                                session.setAttribute("invoice", invoice);
                                session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "UpdateInvoiceContact":
                        if (source != null && source.equals("UpdateContact")) {
                            String customerID = request.getParameter("customerID");
                            String contactID = request.getParameter("contactID");
                            returnHelper = invoiceManagementBean.updateInvoiceCustomerContactDetails(invoice.getId(), Long.parseLong(customerID), Long.parseLong(contactID), isAdmin);
                            invoice = invoiceManagementBean.getInvoice(invoice.getId());
                            if (returnHelper.getResult() && invoice != null) {
                                session.setAttribute("invoice", invoice);
                                session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
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
                                    session.setAttribute("customerAvailableCreditNotes", paymentManagementBeanLocal.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                    nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            }
                        }
                        break;

                    case "ListAllCustomer":
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            response.sendRedirect("error500.html");
                            return;
                        } else {
                            session.setAttribute("customers", customers);
                            nextPage = "OrderManagement/updateContact.jsp?previousPage=invoice";
                        }
                        break;

                    case "ListCustomerContacts":
                        String customerID = request.getParameter("customerID");
                        List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(customerID));
                        if (contacts == null) {
                            response.sendRedirect("error500.html");
                            return;
                        } else {
                            session.setAttribute("contacts", contacts);
                            if (source != null && source.equals("addressBook")) {
                                nextPage = "OrderManagement/updateContact.jsp?previousPage=invoice&selectedCustomerID=" + customerID;
                            }
                        }
                        break;

                    case "RefreshInvoices":
                        if (true) {
                            returnHelper = invoiceManagementBean.refreshInvoices(loggedInStaffID);
                            List<Invoice> invoices = invoiceManagementBean.listAllInvoice(loggedInStaffID);
                            if (invoices == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("listOfInvoice", invoices);
                                if (returnHelper.getResult()) {
                                    nextPage = "InvoiceManagement/invoices.jsp?goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "InvoiceManagement/invoices.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            }
                        }
                        break;

                    case "RefreshSCOInvoices":
                        if (true) {
                            returnHelper = invoiceManagementBean.refreshInvoices(loggedInStaffID);
                            SalesConfirmationOrder sco = (SalesConfirmationOrder) session.getAttribute("sco");
                            if (sco != null) {
                                List<Invoice> invoices = invoiceManagementBean.listInvoicesTiedToSCO(sco.getId());
                                if (invoices == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                } else {
                                    session.setAttribute("listOfInvoice", invoices);
                                    if (returnHelper.getResult()) {
                                        nextPage = "InvoiceManagement/invoices.jsp?goodMsg=" + returnHelper.getDescription();
                                    } else {
                                        nextPage = "InvoiceManagement/invoices.jsp?errMsg=" + returnHelper.getDescription();
                                    }
                                }
                            }
                        }
                        break;
                }//end switch
            } else {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("InvoiceManagement/invoices.jsp?errMsg=An error has occured");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }
        } catch (Exception ex) {
            response.sendRedirect("InvoiceManagement/invoices.jsp?errMsg=An error has occured");
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
                    isAdmin = true;
                }
                loggedInStaffID = staff.getId();
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
