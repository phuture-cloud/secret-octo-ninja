package OrderManagement;

import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeliveryOrderManagementController extends HttpServlet {

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private DeliveryOrderManagementBeanLocal deliveryOrderManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to DeliveryOrderManagementController");
        String target = request.getParameter("target");
        String id = request.getParameter("id");
        System.out.println("target " + target);
        System.out.println("id " + id);

        session = request.getSession();
        ReturnHelper returnHelper = null;

        try {
            if (checkLogin()) {
                switch (target) {
                    case "RetrieveDO":
                        if (id != null) {
                            session.setAttribute("do", deliveryOrderManagementBean.getDeliveryOrder(Long.parseLong(id)));
                            nextPage = "OrderManagement/doManagement.jsp";
                        }
                        break;

                    case "DeleteDO":
                        if (id != null) {
                            returnHelper = deliveryOrderManagementBean.deleteDeliveryOrder(Long.parseLong(id), isAdmin);
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));

                                nextPage = "OrderManagement/scoManagement_DO.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_DO.jsp?errMsg=" + returnHelper.getDescription();
                            }
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
            response.sendRedirect("OrderManagement/scoManagement.jsp?errMsg=An error has occured");
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
