package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.CreditNote;
import EntityManager.Invoice;
import EntityManager.PaymentRecord;
import EntityManager.ReturnHelper;
import EntityManager.Staff;
import PaymentManagement.PaymentManagementBeanLocal;
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

public class PaymentManagementController extends HttpServlet {

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    @EJB
    private PaymentManagementBeanLocal pmbl;

    @EJB
    private InvoiceManagementBeanLocal invoiceManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;
    Invoice invoice;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String id = request.getParameter("id");

        String amount = request.getParameter("amount");
        String paymentMethod = request.getParameter("paymentMethod");
        String paymentDate = request.getParameter("paymentDate");
        String paymentReferenceNumber = request.getParameter("paymentReferenceNumber");
        String notes = request.getParameter("notes");
        if (paymentDate == null) {
            paymentDate = "";
        }

        String invoiceID = request.getParameter("invoiceID");
        String creditNoteID = request.getParameter("creditNoteID");
        String contactID = request.getParameter("contactID");
        String name = request.getParameter("name");

        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date paymentDateDate = null;

        session = request.getSession();
        ReturnHelper returnHelper = null;
        List<PaymentRecord> paymentRecords;
        Invoice invoice = (Invoice) (session.getAttribute("invoice"));

        String previousManagementPage = request.getParameter("previousManagementPage");
        if (previousManagementPage != null && !previousManagementPage.isEmpty()) {
            session.setAttribute("previousManagementPage", previousManagementPage);
        }

        try {
            if (checkLogin()) {
                switch (target) {
                    case "AddPayment":
                        if (invoice != null) {
                            paymentDateDate = sourceFormat.parse(paymentDate);

                            returnHelper = pmbl.addPayment(invoice.getId(), Double.parseDouble(amount), paymentDateDate, paymentMethod, paymentReferenceNumber, notes);
                            invoice = invoiceManagementBean.getInvoice(invoice.getId());

                            if (returnHelper.getResult() && invoice != null) {
                                session.setAttribute("invoice", invoice);
                                session.setAttribute("paymentRecord", pmbl.getPayment(invoice.getId()));
                                session.setAttribute("invoicePayments", pmbl.listPaymentByInvoice(invoice.getId()));
                                String previousPage = request.getParameter("previousPage");
                                if (previousPage.equals("invoice")) {
                                    nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                                } else if (previousPage.equals("payments")) {
                                    nextPage = "PaymentManagement/paymentManagement.jsp?goodMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                String previousPage = request.getParameter("previousPage");
                                if (previousPage.equals("invoice")) {
                                    nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                                } else if (previousPage.equals("payments")) {
                                    nextPage = "PaymentManagement/paymentManagement.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            }
                        } else {
                            response.sendRedirect("InvoiceManagement/paymentManagement.jsp?errMsg=An Error has occured");
                            return;
                        }
                        break;

                    case "ListPaymentTiedToInvoice":
                        if (invoice != null) {
                            paymentRecords = pmbl.listPaymentByInvoice(invoice.getId());
                            if (paymentRecords == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("paymentRecords", paymentRecords);
                            }
                            nextPage = "PaymentManagement/paymentManagement.jsp";
                        } else {
                            response.sendRedirect("InvoiceManagement/paymentManagement.jsp?errMsg=An Error has occured");
                            return;
                        }
                        break;

                    case "DeletePaymentRecord":
                        if (invoice != null) {
                            returnHelper = pmbl.deletePayment(Long.parseLong(id));
                            if (returnHelper.getResult()) {
                                paymentRecords = pmbl.listPaymentByInvoice(invoice.getId());
                                if (paymentRecords == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                } else {
                                    session.setAttribute("paymentRecords", paymentRecords);
                                }
                                nextPage = "PaymentManagement/paymentManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "PaymentManagement/paymentManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            response.sendRedirect("InvoiceManagement/paymentManagement.jsp?errMsg=An Error has occured");
                            return;
                        }
                        break;

                    case "UpdatePaymentRecord":
                        if (invoice != null) {
                            paymentDateDate = sourceFormat.parse(paymentDate);

                            returnHelper = pmbl.updatePayment(Long.parseLong(id), Double.parseDouble(amount), paymentDateDate, paymentMethod, paymentReferenceNumber, notes);
                            if (returnHelper.getResult()) {
                                paymentRecords = pmbl.listPaymentByInvoice(invoice.getId());
                                if (paymentRecords == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                } else {
                                    session.setAttribute("paymentRecords", paymentRecords);
                                }
                                nextPage = "PaymentManagement/paymentManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "PaymentManagement/paymentManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            response.sendRedirect("InvoiceManagement/paymentManagement.jsp?errMsg=An Error has occured");
                            return;
                        }
                        break;

                    case "ListCustomerCreditNotes":
                        if (id != null && !id.isEmpty()) {
                            List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                            if (contacts == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("contacts", contacts);
                                session.setAttribute("listOfCreditNotes", pmbl.listAllCreditNote(Long.parseLong(id)));
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name;
                            }
                        }
                        break;

                    case "GenerateCreditNote":
                        if (id != null && !id.isEmpty() && contactID != null && !contactID.isEmpty()) {
                            String creditNoteDate = request.getParameter("creditNoteDate");
                            Date creditNoteDateDate = sourceFormat.parse(creditNoteDate);

                            returnHelper = pmbl.addCreditNote(Long.parseLong(contactID), Double.parseDouble(amount), creditNoteDateDate);

                            if (returnHelper.getResult()) {
                                session.setAttribute("listOfCreditNotes", pmbl.listAllCreditNote(Long.parseLong(id)));
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "UpdateCreditNote":
                        //id here refers to custoemr ID
                        if (creditNoteID != null && !creditNoteID.isEmpty() && id != null && !id.isEmpty()) {
                            String creditNoteDate = request.getParameter("creditNoteDate");
                            Date creditNoteDateDate = sourceFormat.parse(creditNoteDate);

                            Long longContactID = null;
                            if (contactID != null && !contactID.isEmpty()) {
                                longContactID = Long.parseLong(contactID);
                            }

                            returnHelper = pmbl.updateCreditNote(Long.parseLong(creditNoteID), longContactID, creditNoteDateDate, Double.parseDouble(amount));

                            if (returnHelper.getResult()) {
                                session.setAttribute("listOfCreditNotes", pmbl.listAllCreditNote(Long.parseLong(id)));
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "VoidCreditNote":
                        //id here refers to custoemr ID
                        if (creditNoteID != null && !creditNoteID.isEmpty() && id != null && !id.isEmpty()) {
                            returnHelper = pmbl.voidCreditNote(Long.parseLong(creditNoteID));

                            if (returnHelper.getResult()) {
                                session.setAttribute("listOfCreditNotes", pmbl.listAllCreditNote(Long.parseLong(id)));
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "CreditNoteManagement/creditNotes.jsp?id=" + id + "&name=" + name + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "AttachCreditNote":
                        //id here refers to custoemr ID
                        if (creditNoteID != null && !creditNoteID.isEmpty() && invoiceID != null && !invoiceID.isEmpty()) {
                            returnHelper = pmbl.attachCreditNote(Long.parseLong(creditNoteID), Long.parseLong(invoiceID));

                            if (returnHelper.getResult()) {
                                session.setAttribute("customerAvailableCreditNotes", pmbl.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                invoice = invoiceManagementBean.getInvoice(Long.parseLong(invoiceID));
                                session.setAttribute("invoice", invoice);
                                nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "DetachCreditNote":
                        if (invoiceID != null && !invoiceID.isEmpty()) {
                            returnHelper = pmbl.detachAllCreditNote(Long.parseLong(invoiceID));

                            if (returnHelper.getResult()) {
                                session.setAttribute("customerAvailableCreditNotes", pmbl.listAvailableCreditNote(invoice.getSalesConfirmationOrder().getCustomer().getId()));
                                invoice = invoiceManagementBean.getInvoice(Long.parseLong(invoiceID));
                                session.setAttribute("invoice", invoice);
                                nextPage = "InvoiceManagement/invoice.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "InvoiceManagement/invoice.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "PrintPDF":
                        CreditNote creditNote = pmbl.getCreditNote(Long.parseLong(creditNoteID));
                        if (creditNote == null) {
                            response.sendRedirect("error500.html");
                            return;
                        } else {
                            session.setAttribute("creditNote", creditNote);
                            nextPage = "CreditNoteManagement/creditNote-print.jsp";
                        }
                        break;

                }//end switch
            } else {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("OrderManagement/paymentManagement.jsp?errMsg=An error has occured");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }
        } catch (Exception ex) {
            response.sendRedirect("OrderManagement/paymentManagement.jsp?errMsg=An error has occured");
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
