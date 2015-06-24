package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.io.IOException;
import java.util.List;
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
    HttpSession session;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String prefix = request.getParameter("prefix");
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");

        session = request.getSession();
        ReturnHelper returnHelper;

        try {
            switch (target) {
                case "StaffLogin":
                    returnHelper = accountManagementBean.loginStaff(username, password);
                    if (returnHelper.getResult()) {
                        Staff staff = accountManagementBean.getStaff(username);
                        if (staff == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("staff", staff);
                            nextPage = "AccountManagement/workspace.jsp";
                        }
                    } else {
                        nextPage = "index.jsp?errMsg=" + returnHelper.getDescription();
                    }
                    break;

                case "StaffLogout":
                    session.invalidate();
                    nextPage = "index.jsp?goodMsg=Logout Successful";
                    break;

                case "AddStaff":
                    if (checkLogin(response)) {
                        returnHelper = accountManagementBean.registerStaffAccount(name, prefix, username, password, false);
                        if (returnHelper.getResult()) {
                            List<Staff> staffs = accountManagementBean.listAllStaffAccount();
                            if (staffs == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("staffs", staffs);
                                nextPage = "AccountManagement/staffManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "AccountManagement/staffManagement_add.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "UpdateStaff":
                    if (checkLogin(response)) {
                        if (password != null && !password.equals("")) {
                            returnHelper = accountManagementBean.updateStaffPassword(Long.parseLong(id), password);
                            if (!returnHelper.getResult()) {
                                nextPage = "AccountManagement/staffManagement_update.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        returnHelper = accountManagementBean.updateStaff(Long.parseLong(id), name, prefix);

                        if (returnHelper.getResult()) {
                            List<Staff> staffs = accountManagementBean.listAllStaffAccount();
                            if (staffs == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("staffs", staffs);
                                nextPage = "AccountManagement/staffManagement_update.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "AccountManagement/staffManagement_update.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "DisableStaff":
                    if (checkLogin(response)) {
                        returnHelper = accountManagementBean.disableAccount(Long.parseLong(id));
                        if (returnHelper.getResult()) {
                            List<Staff> staffs = accountManagementBean.listAllStaffAccount();
                            if (staffs == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("staffs", staffs);
                                nextPage = "AccountManagement/staffManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "AccountManagement/staffManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "ListAllStaff":
                    if (checkLogin(response)) {
                        List<Staff> staffs = accountManagementBean.listAllStaffAccount();
                        if (staffs == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("staffs", staffs);
                            nextPage = "AccountManagement/staffManagement.jsp";
                        }
                    }
                    break;

                case "UpdateProfile":
                    if (checkLogin(response)) {
                        if (password != null && !password.equals("")) {
                            returnHelper = accountManagementBean.updateStaffPassword(Long.parseLong(id), password);
                            if (!returnHelper.getResult()) {
                                nextPage = "profile.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        returnHelper = accountManagementBean.updateStaff(Long.parseLong(id), name, prefix);

                        if (returnHelper.getResult()) {
                            Staff staff = accountManagementBean.getStaff(username);

                            if (staff == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("staff", staff);
                                nextPage = "profile.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "profile.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkLogin(HttpServletResponse response) {
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
