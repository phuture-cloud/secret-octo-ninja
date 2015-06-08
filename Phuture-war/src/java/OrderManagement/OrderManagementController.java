package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import static com.sun.xml.bind.util.CalendarConv.formatter;
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

public class OrderManagementController extends HttpServlet {

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String id = request.getParameter("id");
        String scoNumber = request.getParameter("scoNumber");
        String customerID = request.getParameter("customerID");
        String contactID = request.getParameter("contactID");
        String salesStaffID = request.getParameter("salesStaffID");
        String lineItemID = request.getParameter("lineItemID");
        String scoDate = request.getParameter("scoDate");
        if (scoDate == null) {
            scoDate = "";
        }
        String terms = request.getParameter("terms");
        if (terms == null) {
            terms = "";
        }
        String remarks = request.getParameter("remarks");
        String notes = request.getParameter("notes");
        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");
        String itemUnitPrice = request.getParameter("itemUnitPrice");
        String source = request.getParameter("source");

        String company = request.getParameter("company");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String officeNo = request.getParameter("officeNo");
        String mobileNo = request.getParameter("mobileNo");
        String faxNo = request.getParameter("faxNo");
        String address = request.getParameter("address");

        session = request.getSession();
        ReturnHelper returnHelper = null;

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
                        session.removeAttribute("sco");
                    }
                    break;

                case "ListAllCustomer":
                    if (checkLogin(response)) {
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("customers", customers);
                            if (source != null && source.equals("addressBook")) {
                                nextPage = "OrderManagement/updateContact.jsp?id=" + id;
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp";
                            }
                        }
                    }
                    break;

                case "ListCustomerContacts":
                    if (checkLogin(response)) {
                        List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(customerID));
                        if (contacts == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("contacts", contacts);
                            if (source != null && source.equals("addressBook")) {
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id)));
                                nextPage = "OrderManagement/updateContact.jsp?id=" + id + "&selectedCustomerID=" + customerID;
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedTerms=" + terms + "&selectedDate=" + scoDate;
                            }
                        }
                    }
                    break;

                case "SaveSCO":
                    if (checkLogin(response)) {
                        if (source.equals("AddLineItemToNewSCO")) {
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedContactID=" + contactID + "&selectedTerms=" + terms + "&selectedDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                        }
                        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date scoDateDate = sourceFormat.parse(scoDate);

                        //Create SCO
                        returnHelper = orderManagementBean.createSalesConfirmationOrder(scoNumber, scoDateDate, Long.parseLong(customerID), Long.parseLong(contactID), Long.parseLong(salesStaffID), Integer.parseInt(terms), remarks, notes);
                        if (returnHelper.getResult()) {
                            Long scoID = returnHelper.getID();
                            session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(scoID));
                            //Add line item if already filled in
                            if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), false);
                                if (returnHelper.getResult()) {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                }
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                break;
                            }
                        } else { // Error in creating SCO
                            nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedDate=" + scoDate + "&selectedTerms=" + terms;
                        }
                    }

                    break;

                case "UpdateSCO":
                    if (checkLogin(response)) {
                        if (source.equals("AddLineItemToExistingSCO")) {
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&selectedTerms=" + terms + "&selectedDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                        }
                        if (scoNumber == null || scoNumber.isEmpty() || scoDate == null || scoDate.isEmpty() || terms == null || terms.isEmpty()) {
                            SalesConfirmationOrder sco = (SalesConfirmationOrder) session.getAttribute("sco");
                            Long scoID = sco.getId();
                            nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&selectedContactID=" + contactID + "&selectedTerms=" + terms + "&selectedDate=" + scoDate + "&id=" + scoID + "&errMsg=Please fill in all the fields for the SCO.";
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            Date scoDateDate = formatter.parse(scoDate);
                            //Update SCO
                            returnHelper = orderManagementBean.updateSalesConfirmationOrder(Long.parseLong(id), scoNumber, scoDateDate, Long.parseLong(customerID), Long.parseLong(salesStaffID), Integer.parseInt(terms), remarks, notes, false);
                            if (returnHelper.getResult()) {
                                Long scoID = returnHelper.getID();
                                //Update line item if there is any
                                if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                    returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), false);
                                    if (returnHelper.getResult()) {
                                        session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(scoID));
                                        nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                    } else {
                                        nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&scoNumber=" + scoNumber + "&id=" + scoID;
                                    }
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&selectedTerms=" + terms + "&selectedDate=" + scoDate + "&goodMsg=" + returnHelper.getDescription();
                                    break;
                                }
                            }
                        }
                    }
                    break;

                case "UpdateSCOContact":
                    if (checkLogin(response)) {
                        if (source != null && source.equals("UpdateContact")) {
                            returnHelper = orderManagementBean.updateSalesConfirmationOrderCustomerContactDetails(Long.parseLong(id), Long.parseLong(customerID), Long.parseLong(contactID), false);
                            if (returnHelper.getResult()) {
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id)));
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }

                        } else if (company != null && !company.isEmpty() && name != null && !name.isEmpty() && address != null && !address.isEmpty() && officeNo != null && !officeNo.isEmpty() && email != null && !email.isEmpty()) {
                            returnHelper = orderManagementBean.updateSalesConfirmationOrderCustomerContactDetails(Long.parseLong(id), company, name, email, officeNo, mobileNo, faxNo, address, false);
                            if (returnHelper.getResult()) {
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id)));
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=Please fill in all the required fields for edit contact";
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

                case "RemoveLineItem":
                    if (checkLogin(response)) {
                        returnHelper = orderManagementBean.deleteSCOlineItem(Long.parseLong(id), Long.parseLong(lineItemID), false);
                        if (returnHelper.getResult()) {
                            session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id)));
                            nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&scoNumber=" + scoNumber + "&selectedTerms=" + terms + "&selectedDate=" + scoDate + "&goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&selectedTerms=" + terms + "&selectedDate=" + scoDate + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "DeleteSCO":
                    if (checkLogin(response)) {
                        returnHelper = orderManagementBean.deleteSalesConfirmationOrder(Long.parseLong(id));
                        if (returnHelper.getResult()) {
                            session.setAttribute("salesConfirmationOrders", orderManagementBean.listAllSalesConfirmationOrder());
                            nextPage = "OrderManagement/scoManagement.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "OrderManagement/scoManagement.jsp?errMsg=" + returnHelper.getDescription();
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
            response.sendRedirect("OrderManagement/scoManagement.jsp?errMsg=An error has occured");
            ex.printStackTrace();
            return;
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
