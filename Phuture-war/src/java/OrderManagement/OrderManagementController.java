package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
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

public class OrderManagementController extends HttpServlet {

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Welcome to OrderManagementController");
        String target = request.getParameter("target");
        String id = request.getParameter("id");
        String scoNumber = request.getParameter("scoNumber");
        String customerID = request.getParameter("customerID");
        String contactID = request.getParameter("contactID");
        String salesStaffID = request.getParameter("salesStaffID");
        String date = request.getParameter("date");
        String terms = request.getParameter("terms");
        String remarks = request.getParameter("remarks");
        String notes = request.getParameter("notes");
        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");
        String itemUnitPrice = request.getParameter("itemUnitPrice");

        session = request.getSession();
        ReturnHelper returnHelper;

        try {
            switch (target) {
                case "ListAllSCO":
                    if (checkLogin(response)) {
                        List<SalesConfirmationOrder> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder();
                        if (salesConfirmationOrders == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                            nextPage = "OrderManagement/scoManagement.jsp";
                        }
                        session.removeAttribute("contacts");
                    }
                    break;

                case "ListAllCustomer":
                    if (checkLogin(response)) {
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("customers", customers);
                            nextPage = "OrderManagement/scoManagement_add.jsp";
                        }
                    }
                    break;

                case "ListCustomerContacts":
                    if (checkLogin(response)) {
                        List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                        if (contacts == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("contacts", contacts);
                            nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + id + "&scoNumber=" + scoNumber;
                        }
                    }
                    break;

                case "AddLineItemToNewSCO":
                    if (checkLogin(response)) {
                        if (scoNumber == null || scoNumber.isEmpty() || date == null || date.isEmpty() || customerID == null || customerID.isEmpty() || salesStaffID == null || salesStaffID.isEmpty() || terms == null || terms.isEmpty() || itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty() || itemUnitPrice.isEmpty()) {
                            nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedContactID=" + contactID + "&selectedTerms=" + terms + "&selectedDate=" + date + "&errMsg=Please ensure all the fields are populated.";
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date scoDate = formatter.parse(date);

                            returnHelper = orderManagementBean.createSalesConfirmationOrder(scoNumber, scoDate, Long.parseLong(customerID), Long.parseLong(contactID), Long.parseLong(salesStaffID), Integer.parseInt(terms), remarks, notes);

                            if (returnHelper.getResult()) {
                                Long scoID = returnHelper.getID();

                                returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), false);
                                if (returnHelper.getResult()) {
                                    session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(scoID));
                                    nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "=&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                }
                            }
                        }
                    }
                    break;

                case "RetrieveSCO":
                    if (checkLogin(response)) {
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("customers", customers);
                            session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id)));
                            nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id;
                        }
                    }
                    break;

                case "DeleteSCO":
                    if (checkLogin(response)) {
                        returnHelper = orderManagementBean.deleteSalesConfirmationOrder(Long.parseLong(id));
                        if (returnHelper.getResult()) {
                            session.setAttribute("salesConfirmationOrders", orderManagementBean.listAllSalesConfirmationOrder());
                            nextPage = "OrderManagement/scoManagement.jsp?goodMsg=" + returnHelper.getDescription();
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
            response.sendRedirect("OrderManagement/scoManagement.jsp?errMsg=An error has occured");
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
