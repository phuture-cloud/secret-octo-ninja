package CustomerManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.ReturnHelper;
import EntityManager.Staff;
import PaymentManagement.PaymentManagementBeanLocal;
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
    private PaymentManagementBeanLocal paymentManagementBean;

    @EJB
    private SupplierManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    ReturnHelper returnHelper;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String id = request.getParameter("id");
        String id2 = request.getParameter("id2");
        String companyName = request.getParameter("companyName");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String officeNo = request.getParameter("officeNo");
        String mobileNo = request.getParameter("mobileNo");
        String faxNo = request.getParameter("faxNo");
        String address = request.getParameter("address");
        String notes = request.getParameter("notes");
        String[] deleteArr = request.getParameterValues("delete");
        session = request.getSession();

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

                case "AddCustomer":
                    if (checkLogin(response)) {
                        returnHelper = customerManagementBean.addCustomer(companyName);
                        if (returnHelper.getResult()) {
                            Long custID = returnHelper.getID();
                            returnHelper = customerManagementBean.addContact(custID, name, email, officeNo, mobileNo, faxNo, address, notes);
                            if (returnHelper.getResult()) {
                                Long contactID = returnHelper.getID();
                                returnHelper = customerManagementBean.setPrimaryContact(custID, contactID);
                                if (returnHelper.getResult()) {
                                    List<Customer> customers = customerManagementBean.listCustomers();
                                    if (customers == null) {
                                        nextPage = "error500.html";
                                    } else {
                                        session.setAttribute("customers", customers);
                                        nextPage = "CustomerManagement/customerManagement.jsp?goodMsg=" + returnHelper.getDescription();
                                    }
                                } else {
                                    nextPage = "CustomerManagement/customerManagement.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                nextPage = "CustomerManagement/customerManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/customerManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "UpdateCustomer":
                    if (checkLogin(response)) {
                        returnHelper = customerManagementBean.updateCustomer(Long.parseLong(id), name);
                        if (returnHelper.getResult()) {
                            List<Customer> customers = customerManagementBean.listCustomers();
                            if (customers == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("customers", customers);
                                nextPage = "CustomerManagement/customerManagement_update.jsp?id=" + id + "&name=" + name + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/customerManagement_update.jsp?id=" + id + "&name=" + name + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "RemoveCustomer":
                    if (checkLogin(response)) {
                        returnHelper = customerManagementBean.deleteCustomer(Long.parseLong(id));
                        if (returnHelper.getResult()) {
                            List<Customer> customers = customerManagementBean.listCustomers();
                            if (customers == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("customers", customers);
                                nextPage = "CustomerManagement/customerManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/customerManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "ListCustomerContacts":
                    if (checkLogin(response)) {
                        List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (contacts == null || customers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("contacts", contacts);
                            session.setAttribute("customers", customers);
                            nextPage = "CustomerManagement/contactManagement.jsp?id=" + id;
                        }
                    }
                    break;

                case "SetPrimaryContact":
                    if (checkLogin(response)) {
                        returnHelper = customerManagementBean.setPrimaryContact(Long.parseLong(id), Long.parseLong(id2));
                        if (returnHelper.getResult()) {
                            List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                            if (contacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("contacts", contacts);
                                nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "AddContact":
                    if (checkLogin(response)) {
                        returnHelper = customerManagementBean.addContact(Long.parseLong(id), name, email, officeNo, mobileNo, faxNo, address, notes);
                        if (returnHelper.getResult()) {
                            List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                            if (contacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("contacts", contacts);
                                nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "UpdateContact":
                    if (checkLogin(response)) {
                        returnHelper = customerManagementBean.updateContact(Long.parseLong(id2), name, email, officeNo, mobileNo, faxNo, address, notes);

                        if (returnHelper.getResult()) {
                            List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                            if (contacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("contacts", contacts);
                                nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "RemoveContact":
                    if (checkLogin(response)) {
                        if (deleteArr != null) {
                            for (int i = 0; i < deleteArr.length; i++) {
                                if (!deleteArr[i].equals("")) {
                                    returnHelper = customerManagementBean.deleteContact(Long.parseLong(deleteArr[i]));
                                }
                            }

                            if (returnHelper.getResult()) {
                                List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(id));
                                if (contacts == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("contacts", contacts);
                                    nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "CustomerManagement/contactManagement.jsp?id=" + id + "&errMsg=Nothing is selected";
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
