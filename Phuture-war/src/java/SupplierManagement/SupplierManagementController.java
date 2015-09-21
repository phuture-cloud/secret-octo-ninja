package SupplierManagement;

import EntityManager.Supplier;
import EntityManager.ReturnHelper;
import EntityManager.Staff;
import EntityManager.SupplierContact;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SupplierManagementController extends HttpServlet {

    @EJB
    private SupplierManagementBeanLocal supplierManagementBean;

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
                case "ListAllSupplier":
                    if (checkLogin(response)) {
                        List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                        if (suppliers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("suppliers", suppliers);
                            nextPage = "SupplierManagement/supplierManagement.jsp";
                        }
                    }
                    break;

                case "AddSupplier":
                    if (checkLogin(response)) {
                        returnHelper = supplierManagementBean.addSupplier(companyName);
                        if (returnHelper.getResult()) {
                            Long supID = returnHelper.getID();
                            returnHelper = supplierManagementBean.addContact(supID, name, email, officeNo, mobileNo, faxNo, address, notes);
                            if (returnHelper.getResult()) {
                                Long supplierContactID = returnHelper.getID();
                                returnHelper = supplierManagementBean.setPrimaryContact(supID, supplierContactID);
                                if (returnHelper.getResult()) {
                                    List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                                    if (suppliers == null) {
                                        nextPage = "error500.html";
                                    } else {
                                        session.setAttribute("suppliers", suppliers);
                                        nextPage = "SupplierManagement/supplierManagement.jsp?goodMsg=" + returnHelper.getDescription();
                                    }
                                } else {
                                    nextPage = "SupplierManagement/supplierManagement.jsp?errMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                nextPage = "SupplierManagement/supplierManagement.jsp?errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "UpdateSupplier":
                    if (checkLogin(response)) {
                        returnHelper = supplierManagementBean.updateSupplier(Long.parseLong(id), name);
                        if (returnHelper.getResult()) {
                            List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                            if (suppliers == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("suppliers", suppliers);
                                nextPage = "SupplierManagement/supplierManagement_update.jsp?id=" + id + "&name=" + name + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierManagement_update.jsp?id=" + id + "&name=" + name + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "RemoveSupplier":
                    if (checkLogin(response)) {
                        returnHelper = supplierManagementBean.deleteSupplier(Long.parseLong(id));
                        if (returnHelper.getResult()) {
                            List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                            if (suppliers == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("suppliers", suppliers);
                                nextPage = "SupplierManagement/supplierManagement.jsp?goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierManagement.jsp?errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "ListSupplierContacts":
                    if (checkLogin(response)) {
                        List<SupplierContact> supplierContacts = supplierManagementBean.listSupplierContacts(Long.parseLong(id));
                        List<Supplier> suppliers = supplierManagementBean.listSuppliers();
                        if (supplierContacts == null || suppliers == null) {
                            nextPage = "error500.html";
                        } else {
                            session.setAttribute("supplierContacts", supplierContacts);
                            session.setAttribute("suppliers", suppliers);
                            nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id;
                        }
                    }
                    break;

                case "SetPrimarySupplierContact":
                    if (checkLogin(response)) {
                        returnHelper = supplierManagementBean.setPrimaryContact(Long.parseLong(id), Long.parseLong(id2));
                        if (returnHelper.getResult()) {
                            List<SupplierContact> supplierContacts = supplierManagementBean.listSupplierContacts(Long.parseLong(id));
                            if (supplierContacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("supplierContacts", supplierContacts);
                                nextPage = "SupplierManagement/supplierManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "AddSupplierContact":
                    if (checkLogin(response)) {
                        returnHelper = supplierManagementBean.addContact(Long.parseLong(id), name, email, officeNo, mobileNo, faxNo, address, notes);
                        if (returnHelper.getResult()) {
                            List<SupplierContact> supplierContacts = supplierManagementBean.listSupplierContacts(Long.parseLong(id));
                            if (supplierContacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("supplierContacts", supplierContacts);
                                nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "UpdateSupplierContact":
                    if (checkLogin(response)) {
                        returnHelper = supplierManagementBean.updateContact(Long.parseLong(id2), name, email, officeNo, mobileNo, faxNo, address, notes);

                        if (returnHelper.getResult()) {
                            List<SupplierContact> supplierContacts = supplierManagementBean.listSupplierContacts(Long.parseLong(id));
                            if (supplierContacts == null) {
                                nextPage = "error500.html";
                            } else {
                                session.setAttribute("supplierContacts", supplierContacts);
                                nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                        }
                    }
                    break;

                case "RemoveSupplierContact":
                    if (checkLogin(response)) {
                        if (deleteArr != null) {
                            for (int i = 0; i < deleteArr.length; i++) {
                                if (!deleteArr[i].equals("")) {
                                    returnHelper = supplierManagementBean.deleteContact(Long.parseLong(deleteArr[i]));
                                }
                            }

                            if (returnHelper.getResult()) {
                                List<SupplierContact> supplierContacts = supplierManagementBean.listSupplierContacts(Long.parseLong(id));
                                if (supplierContacts == null) {
                                    nextPage = "error500.html";
                                } else {
                                    session.setAttribute("supplierContacts", supplierContacts);
                                    nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&goodMsg=" + returnHelper.getDescription();
                                }
                            } else {
                                nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&errMsg=" + returnHelper.getDescription();
                            }
                        } else {
                            nextPage = "SupplierManagement/supplierContactManagement.jsp?id=" + id + "&errMsg=Nothing is selected";
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
