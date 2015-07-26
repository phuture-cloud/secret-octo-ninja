package OrderManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import EntityManager.StatementOfAccount;
import PaymentManagement.StatementOfAccountBeanLocal;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StatementOfAccountManagementController extends HttpServlet {

    @EJB
    private StatementOfAccountBeanLocal statementOfAccountBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to StatementOfAccountManagementController");
        String target = request.getParameter("target");
        String source = request.getParameter("source");
        String id = request.getParameter("id");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        List<StatementOfAccount> statementOfAccounts;

        try {
            if (checkLogin()) {
                switch (target) {
                    case "ListAllSOA":
                        statementOfAccounts = statementOfAccountBean.listAllStatementOfAccounts();
                        if (statementOfAccounts == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("statementOfAccounts", statementOfAccounts);
                            nextPage = "PaymentManagement/statementOfAccounts.jsp";
                        }
                        break;

                    case "RefreshSOA":
                        returnHelper = statementOfAccountBean.refreshAllSOA();
                        if (returnHelper.getResult()) {
                            statementOfAccounts = statementOfAccountBean.listAllStatementOfAccounts();
                            if (statementOfAccounts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("statementOfAccounts", statementOfAccounts);
                                nextPage = "PaymentManagement/statementOfAccounts.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "PaymentManagement/statementOfAccounts.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RetrieveSOA":
                        if (id != null) {
                            session.setAttribute("statementOfAccount", statementOfAccountBean.getCustomerSOA(Long.parseLong(id)));
                            nextPage = "PaymentManagement/statementOfAccount.jsp";
                        }
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
            response.sendRedirect("AccountManagement/workspace.jsp?errMsg=An error has occured");
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
