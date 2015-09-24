package OrderManagement;

import EntityManager.Invoice;
import EntityManager.ReturnHelper;
import EntityManager.Staff;
import EntityManager.StatementOfAccount;
import PaymentManagement.PaymentManagementBeanLocal;
import PaymentManagement.StatementOfAccountBeanLocal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StatementOfAccountManagementController extends HttpServlet {

    @EJB
    private StatementOfAccountBeanLocal soabl;
    @EJB
    private PaymentManagementBeanLocal pmbl;
    @EJB
    private InvoiceManagementBeanLocal imbl;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        List<StatementOfAccount> statementOfAccounts;

        try {
            if (checkLogin()) {
                switch (target) {
                    case "ListAllSOA":
                        statementOfAccounts = soabl.listAllStatementOfAccounts();
                        if (statementOfAccounts == null) {
                            response.sendRedirect("error500.html");
                            return;
                        } else {
                            session.setAttribute("statementOfAccounts", statementOfAccounts);
                            nextPage = "PaymentManagement/statementOfAccounts.jsp";
                        }
                        break;

                    case "RefreshSOA":
                        returnHelper = soabl.refreshAllSOA();
                        if (returnHelper.getResult()) {
                            statementOfAccounts = soabl.listAllStatementOfAccounts();
                            if (statementOfAccounts == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("statementOfAccounts", statementOfAccounts);
                                nextPage = "PaymentManagement/statementOfAccounts.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "PaymentManagement/statementOfAccounts.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RetrieveSOA":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                session.setAttribute("statementOfAccount", soabl.getCustomerSOA(Long.parseLong(id)));
                                session.setAttribute("statementOfAccountPayments", pmbl.listPaymentByCustomer(Long.parseLong(id)));
                                nextPage = "PaymentManagement/statementOfAccount.jsp";
                            }
                            break;
                        }
                    case "ViewInvoiceTiedToCustomer":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                session.setAttribute("statementOfAccount", soabl.getCustomerSOA(Long.parseLong(id)));
                                session.setAttribute("listOfInvoice", imbl.listInvoicesTiedToCustomer(Long.parseLong(id)));
                                session.setAttribute("previousManagementPage", "soa");
                                nextPage = "InvoiceManagement/invoices.jsp";
                            }
                            break;
                        }
                    case "ViewOverDueInvoiceTiedToCustomer":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                StatementOfAccount soa = soabl.getCustomerSOA(Long.parseLong(id));
                                List<Invoice> invoices = new ArrayList();
                                if (soa == null) {
                                    nextPage = "PaymentManagement/statementOfAccounts.jsp?errMsg=Customer's record could not be found, try refreshing the accounts.";
                                } else {
                                    invoices = soa.getOverDueInvoices();
                                }
                                session.setAttribute("statementOfAccount", soa);
                                session.setAttribute("listOfInvoice", invoices);
                                session.setAttribute("previousManagementPage", "soa");
                                nextPage = "InvoiceManagement/invoices.jsp?show=overdue";
                            }
                            break;
                        }
                }
            } else {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("AccountManagement/workspace.jsp?errMsg=An error has occured");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }
        } catch (Exception ex) {
            response.sendRedirect("AccountManagement/workspace.jsp?errMsg=An error has occured");
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
                    isAdmin=true;
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
