package OrderManagement;

import CustomerManagement.SupplierManagementBeanLocal;
import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.SalesConfirmationOrderHelper;
import EntityManager.Staff;
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
    private InvoiceManagementBeanLocal invoiceManagementBean;

    @EJB
    private PurchaseOrderManagementBeanLocal purchaseOrderManagementBean;

    @EJB
    private DeliveryOrderManagementBeanLocal deliveryOrderManagementBean;

    @EJB
    private OrderManagementBeanLocal orderManagementBean;

    @EJB
    private SupplierManagementBeanLocal customerManagementBean;

    String nextPage = "", goodMsg = "", errMsg = "";
    HttpSession session;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String target = request.getParameter("target");
        String id = request.getParameter("id");

        String customerID = request.getParameter("customerID");
        String contactID = request.getParameter("contactID");
        String lineItemID = request.getParameter("lineItemID");
        String scoDate = request.getParameter("scoDate");
        if (scoDate == null) {
            scoDate = "";
        }

        String poNumber = request.getParameter("poNumber");
        String terms = request.getParameter("terms");
        if (terms == null) {
            terms = "";
        }

        String source = request.getParameter("source");

        session = request.getSession();
        ReturnHelper returnHelper = null;
        Boolean isAdmin = false;

        String previousManagementPage = request.getParameter("previousManagementPage");
        if (previousManagementPage != null && !previousManagementPage.isEmpty()) {
            session.setAttribute("previousManagementPage", previousManagementPage);
        }

        try {
            //Following functions are only accessible if logged in
            if (checkLogin()) {
                //Set the admin flag so he can overwrite some functionalities
                Staff staff = (Staff) (session.getAttribute("staff"));
                if (staff.getIsAdmin()) {
                    isAdmin = true;
                }
                Long loggedInStaffID = staff.getId();

                String estimatedDeliveryDateString = request.getParameter("estimatedDeliveryDate");
                Date estimatedDeliveryDate = null;
                if (estimatedDeliveryDateString != null && !estimatedDeliveryDateString.isEmpty()) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    estimatedDeliveryDate = dateFormat.parse(estimatedDeliveryDateString);
                }

                switch (target) {
                    case "ListAllSCO":
                        if (true) {
                            List<SalesConfirmationOrderHelper> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                            if (salesConfirmationOrders == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                session.setAttribute("previousManagementPage", "sco");
                                nextPage = "OrderManagement/scoManagement.jsp";
                            }
                            session.removeAttribute("contacts");
                            session.removeAttribute("sco");
                            session.removeAttribute("do");
                            session.removeAttribute("po");
                            session.removeAttribute("invoice");
                        }
                        break;

                    case "ListAllCustomer":
                        if (true) {
                            List<Customer> customers = customerManagementBean.listCustomers();
                            if (customers == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("customers", customers);
                                if (source != null && source.equals("addressBook")) {
                                    nextPage = "OrderManagement/updateContact.jsp?previousPage=sco&id=" + id;
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp";
                                }
                            }
                        }
                        break;

                    case "ListCustomerContacts":
                        if (true) {
                            List<Contact> contacts = customerManagementBean.listCustomerContacts(Long.parseLong(customerID));
                            if (contacts == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("contacts", contacts);
                                if (source != null && source.equals("addressBook")) {
                                    nextPage = "OrderManagement/updateContact.jsp?previousPage=sco&id=" + id + "&selectedCustomerID=" + customerID;
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&terms=" + terms + "&scoDate=" + scoDate + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&poNumber=" + poNumber;
                                }
                            }
                        }
                        break;

                    case "SaveSCO":
                        if (true) {
                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");
                            if (source.equals("AddLineItemToNewSCO")) {
                                if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                    break;
                                }
                            }
                            if (scoDate == null || scoDate.isEmpty() || terms == null || terms.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&terms=" + terms + "&scoDate=" + scoDate + "&id=" + id + "&errMsg=Please fill in all the fields for the SCO.";
                            } else {

                                DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date scoDateDate = sourceFormat.parse(scoDate);

                                //Create SCO
                                returnHelper = orderManagementBean.createSalesConfirmationOrder(scoDateDate, estimatedDeliveryDate, poNumber, Long.parseLong(customerID), Long.parseLong(contactID), loggedInStaffID, Integer.parseInt(terms));
                                if (returnHelper.getResult()) {
                                    Long scoID = returnHelper.getID();
                                    SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(scoID);

                                    session.setAttribute("sco", sco);
                                    //Add line item if already filled in
                                    if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                        returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                        if (returnHelper.getResult()) {
                                            session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(scoID));
                                            nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&id=" + scoID;
                                        } else {
                                            nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&selectedContactID=" + contactID + "&id=" + scoID;
                                        }
                                    } else {
                                        nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&id=" + scoID;
                                        break;
                                    }
                                } else { // Error in creating SCO
                                    nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&scoDate=" + scoDate + "&terms=" + terms;
                                }
                            }
                        }
                        break;

                    case "UpdateSCO":
                        if (true) {
                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");

                            if (source.equals("AddLineItemToExistingSCO")) {
                                if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                    break;
                                }
                            }
                            if (scoDate == null || scoDate.isEmpty() || terms == null || terms.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&terms=" + terms + "&scoDate=" + scoDate + "&id=" + id + "&errMsg=Please fill in all the fields for the SCO.";
                            } else {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date scoDateDate = formatter.parse(scoDate);

                                //Update SCO
                                String status = request.getParameter("status");

                                returnHelper = orderManagementBean.updateSalesConfirmationOrder(Long.parseLong(id), scoDateDate, estimatedDeliveryDate, poNumber, Long.parseLong(customerID), status, Integer.parseInt(terms), isAdmin);
                                if (returnHelper.getResult()) {

                                    SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(returnHelper.getID());
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&id=" + returnHelper.getID();

                                    Long scoID = returnHelper.getID();
                                    //Update line item if there is any
                                    if (itemName != null && !itemName.isEmpty() && itemDescription != null && !itemDescription.isEmpty() && itemQty != null && !itemQty.isEmpty() && itemUnitPrice != null && !itemUnitPrice.isEmpty()) {
                                        returnHelper = orderManagementBean.addSCOlineItem(scoID, itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                                        sco = orderManagementBean.getSalesConfirmationOrder(scoID);
                                        if (returnHelper.getResult() && sco != null) {
                                            session.setAttribute("sco", sco);
                                            nextPage = "OrderManagement/scoManagement_add.jsp?goodMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&id=" + scoID;
                                        } else {
                                            nextPage = "OrderManagement/scoManagement_add.jsp?errMsg=" + returnHelper.getDescription() + "&selectedCustomerID=" + customerID + "&id=" + scoID;
                                        }
                                    }
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=" + returnHelper.getDescription();
                                    break;
                                }
                            }
                        }
                        break;

                    case "UpdateSCOContact":
                        if (true) {
                            String company = request.getParameter("company");
                            String name = request.getParameter("name");
                            String email = request.getParameter("email");
                            String officeNo = request.getParameter("officeNo");
                            String mobileNo = request.getParameter("mobileNo");
                            String faxNo = request.getParameter("faxNo");
                            String address = request.getParameter("address");
                            //if address book
                            if (source != null && source.equals("UpdateContact")) {
                                returnHelper = orderManagementBean.updateSalesConfirmationOrderCustomerContactDetails(Long.parseLong(id), Long.parseLong(customerID), Long.parseLong(contactID), isAdmin);
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                if (returnHelper.getResult() && sco != null) {
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                                }
                                //manual key in
                            } else if (company != null && !company.isEmpty() && name != null && !name.isEmpty() && address != null && !address.isEmpty() && officeNo != null && !officeNo.isEmpty() && email != null && !email.isEmpty()) {
                                returnHelper = orderManagementBean.updateSalesConfirmationOrderCustomerContactDetails(Long.parseLong(id), company, name, email, officeNo, mobileNo, faxNo, address, isAdmin);
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                if (returnHelper.getResult() && sco != null) {
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=Please fill in all the required fields for edit contact";
                                }
                            }
                        }
                        break;

                    case "UpdateSCONotes":
                        if (true) {
                            String notes = request.getParameter("notes");
                            returnHelper = orderManagementBean.updateSalesConfirmationOrderNotes(Long.parseLong(id), notes, isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "UpdateSCORemarks":
                        if (true) {
                            String remarks = request.getParameter("remarks");
                            returnHelper = orderManagementBean.updateSalesConfirmationOrderRemarks(Long.parseLong(id), remarks, isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "RetrieveSCO":
                        if (true) {
                            List<Customer> customers = customerManagementBean.listCustomers();
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (customers == null || sco == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("customers", customers);
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id;
                            }
                        }
                        break;

                    case "PrintPDF":
                        if (true) {
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (sco == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/sco-print.jsp?id=" + id;
                            }
                        }
                        break;

                    case "EditLineItem":
                        if (true) {
                            String itemName = request.getParameter("itemName");
                            String itemDescription = request.getParameter("itemDescription");
                            String itemQty = request.getParameter("itemQty");
                            String itemUnitPrice = request.getParameter("itemUnitPrice");
                            String status = request.getParameter("status");

                            //Check for empty fields
                            if (itemName == null || itemName.isEmpty() || itemDescription == null || itemDescription.isEmpty() || itemQty == null || itemQty.isEmpty() || itemUnitPrice == null || itemUnitPrice.isEmpty()) {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&selectedCustomerID=" + customerID + "&terms=" + terms + "&editingLineItem=" + lineItemID + "&scoDate=" + scoDate + "&errMsg=Please fill in all the fields for the item.";
                                break;
                            }
                            //Edit line item
                            returnHelper = orderManagementBean.updateSCOlineItem(Long.parseLong(id), Long.parseLong(lineItemID), itemName, itemDescription, Integer.parseInt(itemQty), Double.parseDouble(itemUnitPrice), isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", orderManagementBean.getSalesConfirmationOrder(sco.getId()));
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&status=" + status + "&editingLineItem=" + lineItemID + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "RemoveLineItem":
                        if (true) {
                            returnHelper = orderManagementBean.deleteSCOlineItem(Long.parseLong(id), Long.parseLong(lineItemID), isAdmin);
                            SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                            if (returnHelper.getResult() && sco != null) {
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&terms=" + terms + "&scoDate=" + scoDate + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "DeleteSCO":
                        if (true) {
                            returnHelper = orderManagementBean.deleteSalesConfirmationOrder(Long.parseLong(id), isAdmin);
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrderHelper> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                session.removeAttribute("sco");
                                nextPage = "OrderManagement/scoManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;
                    case "VoidSCO":
                        if (true) {
                            returnHelper = orderManagementBean.voidSalesConfirmationOrder(Long.parseLong(id), isAdmin);
                            if (returnHelper.getResult()) {
                                List<SalesConfirmationOrderHelper> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                                if (salesConfirmationOrders == null) {
                                    response.sendRedirect("error500.html");
                                    return;
                                } else {
                                    session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                }
                                session.removeAttribute("sco");
                                nextPage = "OrderManagement/scoManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;
                    case "GenerateDO":
                        if (true) {
                            String doDate = request.getParameter("doDate");
                            if (doDate != null && !doDate.isEmpty()) {
                                DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                                returnHelper = deliveryOrderManagementBean.createDeliveryOrder(Long.parseLong(id), sourceFormat.parse(doDate));
                                if (returnHelper.getResult()) {
                                    SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=Delivery Order date can not be empty";
                            }
                        }
                        break;

                    case "GeneratePO":
                        if (true) {
                            String poDate = request.getParameter("poDate");
                            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date poDateDate = sourceFormat.parse(poDate);

                            returnHelper = purchaseOrderManagementBean.createPurchaseOrder(Long.parseLong(id), poDateDate);
                            if (returnHelper.getResult()) {
                                SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                session.setAttribute("sco", sco);
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        }
                        break;

                    case "GenerateInvoice":
                        if (true) {
                            String invoiceDate = request.getParameter("invoiceDate");
                            if (invoiceDate != null && !invoiceDate.isEmpty()) {
                                DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                                returnHelper = invoiceManagementBean.createInvoice(Long.parseLong(id), sourceFormat.parse(invoiceDate));
                                if (returnHelper.getResult()) {
                                    SalesConfirmationOrder sco = orderManagementBean.getSalesConfirmationOrder(Long.parseLong(id));
                                    session.setAttribute("sco", sco);
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                nextPage = "OrderManagement/scoManagement_add.jsp?id=" + id + "&errMsg=Invoice date can not be empty";
                            }
                        }
                        break;

                    case "RefreshSCOs":
                        if (true) {
                            returnHelper = orderManagementBean.refreshSCOs(staff.getId());
                            List<SalesConfirmationOrderHelper> salesConfirmationOrders = orderManagementBean.listAllSalesConfirmationOrder(loggedInStaffID);
                            if (salesConfirmationOrders == null) {
                                response.sendRedirect("error500.html");
                                return;
                            } else {
                                session.setAttribute("salesConfirmationOrders", salesConfirmationOrders);
                                if (returnHelper.getResult()) {
                                    nextPage = "OrderManagement/scoManagement.jsp?goodMsg=" + returnHelper.getDescription();
                                } else {
                                    nextPage = "OrderManagement/scoManagement.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            }
                        }
                        break;

                    default:
                        System.out.println("OrderManagementController: Unknown target value.");
                        break;
                }
            } else {
                response.sendRedirect("index.jsp?errMsg=Session Expired.");
                return;
            }

            if (nextPage.equals("")) {
                response.sendRedirect("OrderManagement/scoManagement.jsp?errMsg=An error has occured");
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
            if (staff == null) {
                return false;
            }
            return true;
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
