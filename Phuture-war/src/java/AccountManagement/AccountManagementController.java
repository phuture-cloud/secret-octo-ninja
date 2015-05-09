package AccountManagement;

import EntityManager.ReturnHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountManagementController extends HttpServlet {

    AccountManagementBeanLocal accountManagementBean = lookupAccountManagementBeanLocal();
    String nextPage = "", goodMsg = "", errMsg = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");

        HttpSession session = request.getSession();
        ReturnHelper returnHelper;

        try {
            System.out.println("??");
            accountManagementBean.getCurrentUser();
            System.out.println(">>");
            switch (target) {
                case "StaffLogin":
                    returnHelper = accountManagementBean.loginStaff(username, password);
                    if (returnHelper.getResult()) {
                        //session.setAttribute("staff", accountManagementBean.checkCurrentUser());
                        nextPage = "AccountManagement/workspace.jsp?goodMsg=" + returnHelper.getResultDescription();
                    } else {
                        nextPage = "AccountManagement/workspace.jsp?errMsg=" + returnHelper.getResultDescription();
                    }
                    break;

                case "StaffLogout":
                    session.invalidate();
                    accountManagementBean.logoutStaff();
                    nextPage = "index.jsp?goodMsg=Logout Successful";
                    break;

                case "RemoveStaff":
                    nextPage = "AccountManagement/staffManagement.jsp";
                    break;

                case "AddStaff":
                    nextPage = "AccountManagement/staffManagement.jsp";
                    break;

            }

            response.sendRedirect(nextPage);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private AccountManagementBeanLocal lookupAccountManagementBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AccountManagementBeanLocal) c.lookup("java:global/Phuture/Phuture-ejb/AccountManagementBean!AccountManagement.AccountManagementBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
