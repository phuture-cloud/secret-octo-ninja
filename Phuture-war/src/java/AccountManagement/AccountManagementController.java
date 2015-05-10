package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccountManagementController extends HttpServlet {
    @EJB
    private AccountManagementBeanLocal accountManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");

        HttpSession session = request.getSession();
        ReturnHelper returnHelper;

        try {
            switch (target) {
                case "StaffLogin":
                    returnHelper = accountManagementBean.loginStaff(username, password);
                    if (returnHelper.getResult()) {
                        
                        //session.setAttribute("staff", accountManagementBean.checkCurrentUser());
                        nextPage = "AccountManagement/workspace.jsp?goodMsg=" + returnHelper.getDescription();
                    } else {
                        nextPage = "AccountManagement/workspace.jsp?errMsg=" + returnHelper.getDescription();
                    }
                    break;

                case "StaffLogout":
                    session.invalidate();
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


}
