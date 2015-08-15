package OrderManagement;

import CustomerManagement.CustomerManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.DeliveryOrder;
import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeliveryOrderManagementController extends HttpServlet {

    @EJB
    private DeliveryOrderManagementBeanLocal deliveryOrderManagementBean;

    @EJB
    private CustomerManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;
    Boolean isAdmin = false;
    Long loggedInStaffID = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String source = request.getParameter("source");

        String itemName = request.getParameter("itemName");
        String itemDescription = request.getParameter("itemDescription");
        String itemQty = request.getParameter("itemQty");

        String poNumber = request.getParameter("poNumber");

        String status = request.getParameter("status");
        if (status == null) {
            status = "";
        }

        session = request.getSession();
        ReturnHelper returnHelper = null;
        DeliveryOrder deliveryOrder = (DeliveryOrder) (session.getAttribute("do"));

        try {
            if (checkLogin()) {
                switch (target) {
                    case "ListAllDO":
                        List<DeliveryOrder> deliveryOrders = deliveryOrderManagementBean.listAllDeliveryOrder(loggedInStaffID);
                        session.setAttribute("listOfDO", deliveryOrders);
                        session.setAttribute("previousManagementPage", "deliveryOrders");
                        nextPage = "DOManagement/deliveryOrders.jsp";
                        break;

                    case "ListDoTiedToSCO":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                session.setAttribute("listOfDO", deliveryOrderManagementBean.listDeliveryOrdersTiedToSCO(Long.parseLong(id)));
                                session.setAttribute("previousManagementPage", "sco");
                                nextPage = "DOManagement/deliveryOrders.jsp";
                            }
                        }
                        break;

                    case "RetrieveDO":
                        if (true) {
                            String id = request.getParameter("id");
                            if (id != null) {
                                session.setAttribute("do", deliveryOrderManagementBean.getDeliveryOrder(Long.parseLong(id)));
                                nextPage = "DOManagement/deliveryOrder.jsp";
                            }
                        }
                        break;

                    case "VoidDO":
                        if (deliveryOrder != null) {
                            returnHelper = deliveryOrderManagementBean.voidDeliveryOrder(deliveryOrder.getId(), isAdmin);
                            if (returnHelper.getResult()) {
                                session.removeAttribute("do");
                                nextPage = "DOManagement/deliveryOrders.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "DOManagement/deliveryOrders.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "DOManagement/deliveryOrders.jsp?errMsg=Delete Delivery Order failed. An error has occured.";
                        }
                        break;

                    case "UpdateDO":
                        String doDate = request.getParameter("doDate");
                        if (source.equals("AddLineItemToExistingDO")) {
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty()) {
                                nextPage = "DOManagement/deliveryOrder.jsp?doDate=" + doDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                        }
                        if (doDate == null || doDate.isEmpty()) {
                            nextPage = "DOManagement/deliveryOrder.jsp?doDate=" + doDate + "&errMsg=Please fill in all the fields for the DO.";
                        } else {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            //Update DO
                            returnHelper = deliveryOrderManagementBean.updateDeliveryOrder(deliveryOrder.getId(), formatter.parse(doDate), poNumber, status, isAdmin);
                            if (returnHelper.getResult()) {
                                Long doID = returnHelper.getID();
                                deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(doID);
                                session.setAttribute("do", deliveryOrder);
                                nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();

                                //Update line item if there is any
                                if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty()) {
                                    returnHelper = deliveryOrderManagementBean.addDOlineItem(doID, itemName, itemDescription, Integer.parseInt(itemQty), isAdmin);
                                    deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(doID);
                                    if (returnHelper.getResult() && deliveryOrder != null) {
                                        session.setAttribute("do", deliveryOrder);
                                        nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                                    } else {
                                        nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                                    }
                                }
                            } else {
                                nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                                break;
                            }
                        }
                        break;

                    case "UpdateDONotes":
                        String notes = request.getParameter("notes");
                        returnHelper = deliveryOrderManagementBean.updateDeliveryOrderNotes(deliveryOrder.getId(), notes, isAdmin);
                        deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                        if (returnHelper.getResult() && deliveryOrder != null) {
                            session.setAttribute("do", deliveryOrder);
                            nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "UpdateDORemarks":
                        String remarks = request.getParameter("remarks");
                        returnHelper = deliveryOrderManagementBean.updateDeliveryOrderRemarks(deliveryOrder.getId(), remarks, isAdmin);
                        deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                        if (returnHelper.getResult() && deliveryOrder != null) {
                            session.setAttribute("do", deliveryOrder);
                            nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                        } else {
                            nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                        }
                        break;

                    case "RemoveLineItem":
                        if (true) {
                            String lineItemID = request.getParameter("lineItemID");
                            returnHelper = deliveryOrderManagementBean.deleteDOlineItem(deliveryOrder.getId(), Long.parseLong(lineItemID), isAdmin);
                            deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                            if (returnHelper.getResult() && deliveryOrder != null) {
                                session.setAttribute("do", deliveryOrder);
                                nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "EditLineItem":
                        if (true) {
                            //Check for empty fields
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty()) {
                                nextPage = "DOManagement/deliveryOrder.jsp?errMsg=Please fill in all the fields for the item.";
                                break;
                            }

                            //Edit line item
                            String lineItemID = request.getParameter("lineItemID");
                            returnHelper = deliveryOrderManagementBean.updateDOlineItem(deliveryOrder.getId(), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), isAdmin);
                            deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                            if (returnHelper.getResult() && deliveryOrder != null) {
                                session.setAttribute("do", deliveryOrder);
                                nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "UpdateDOContact":
                        if (source != null && source.equals("UpdateContact")) {
                            String customerID = request.getParameter("customerID");
                            String contactID = request.getParameter("contactID");
                            returnHelper = deliveryOrderManagementBean.updateDeliveryOrderCustomerContactDetails(deliveryOrder.getId(), Long.parseLong(customerID), Long.parseLong(contactID), isAdmin);
                            deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                            if (returnHelper.getResult() && deliveryOrder != null) {
                                session.setAttribute("do", deliveryOrder);
                                nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                            }
                            //manual key in
                        } else if (source == null) {
                            String company = request.getParameter("company");
                            String name = request.getParameter("name");
                            String email = request.getParameter("email");
                            String officeNo = request.getParameter("officeNo");
                            String mobileNo = request.getParameter("mobileNo");
                            String faxNo = request.getParameter("faxNo");
                            String address = request.getParameter("address");

                            if (company != null && !company.isEmpty() && name != null && !name.isEmpty() && address != null && !address.isEmpty() && officeNo != null && !officeNo.isEmpty() && email != null && !email.isEmpty()) {
                                returnHelper = deliveryOrderManagementBean.updateDeliveryOrderCustomerContactDetails(deliveryOrder.getId(), company, name, email, officeNo, mobileNo, faxNo, address, isAdmin);
                                deliveryOrder = deliveryOrderManagementBean.getDeliveryOrder(deliveryOrder.getId());
                                if (returnHelper.getResult() && deliveryOrder != null) {
                                    session.setAttribute("do", deliveryOrder);
                                    nextPage = "DOManagement/deliveryOrder.jsp?goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "DOManagement/deliveryOrder.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            }
                        }
                        break;

                    case "ListAllCustomer":
                        List<Customer> customers = customerManagementBean.listCustomers();
                        if (customers == null) {
                            response.sendRedirect("error500.html");
                            return;
                        } else {
                            session.setAttribute("customers", customers);
                            nextPage = "DOManagement/updateContact.jsp?previousPage=delivery";
                        }
                        break;

                    case "ListCustomerContacts":
                        String customerID = request.getParameter("customerID");
                        List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(customerID));
                        if (contacts == null) {
                            response.sendRedirect("error500.html");
                            return;
                        } else {
                            session.setAttribute("contacts", contacts);
                            if (source != null && source.equals("addressBook")) {
                                nextPage = "DOManagement/updateContact.jsp?previousPage=delivery&selectedCustomerID=" + customerID;
                            }
                        }
                        break;
                }//end switch
            } else {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("DOManagement/deliveryOrder.jsp?errMsg=An error has occured");
                return;
            } else {
                response.sendRedirect(nextPage);
                return;
            }
        } catch (Exception ex) {
            response.sendRedirect("DOManagement/deliveryOrder.jsp?errMsg=An error has occured");
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
                    isAdmin = true;
                    loggedInStaffID = staff.getId();
                }
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
