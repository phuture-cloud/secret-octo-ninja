package CustomerManagement;

import EntityManager.Customer;
import EntityManager.Staff;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CustomerManagementController extends HttpServlet {

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        session = request.getSession();

        System.out.println("target>>>>" + target);
        try {
            switch (target) {
                case "ListAllCustomer":
                    if (checkLogin(response)) {
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("customers", customers);
                            nextPage = "CustomerManagement/customerManagement.jsp";
                        }
                    }
                    break;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
            } else {
                response.sendRedirect(nextPage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkLogin(HttpServletResponse response) {
        try {
            Staff staff = (Staff) (session.getAttribute("staff"));
            if (staff == null) {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return false;
            } else {
                System.out.println("Good to go");
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
