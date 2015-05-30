<%@page import="EntityManager.Contact"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else {
        String customerID = request.getParameter("id");
        String contactID = request.getParameter("contactID");
        if (contactID == null || customerID == null || contactID.isEmpty() || customerID.isEmpty()) {
            response.sendRedirect("customerManagement.jsp?errMsg=An error has occured.");
        } else {
            List<Contact> contacts = (List<Contact>) session.getAttribute("contacts");
            Contact contact = new Contact();
            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i).getId() == Long.parseLong(contactID)) {
                    contact = contacts.get(i);
                }
            }
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body>
        <script>
            function back() {
                window.onbeforeunload = null;
                window.location.href = "../CustomerManagementController?target=ListCustomerContacts&id=<%=customerID%>";
            }

            window.onbeforeunload = function () {
                return 'There are unsaved changes to this page. If you continue, you will lose them';
            };
        </script>
        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Update Contact</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li>
                                    <a href="../CustomerManagementController?target=ListAllCustomer">
                                        Customer Management
                                    </a>
                                </li>
                                <li>
                                    <a href="../CustomerManagementController?target=ListCustomerContacts&id=<%=customerID%>">
                                        Contact Management
                                    </a>
                                </li>
                                <li><span>Add Contact &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../CustomerManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Contact</h2>
                                    </header>
                                    <div class="panel-body">

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Name <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" value="<%=contact.getName()%>" required>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Email <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-envelope"></i>
                                                    </span>
                                                    <input type="email" class="form-control" name="email" value="<%=contact.getEmail()%>" required>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Telephone <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-phone"></i>
                                                    </span>
                                                    <input type="text" class="form-control" name="officeNo" value="<%=contact.getOfficeNo()%>" required>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Mobile</label>
                                            <div class="col-md-6">
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-phone"></i>
                                                    </span>
                                                    <input type="text" class="form-control" name="mobileNo" value="<%=contact.getMobileNo()%>">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Fasimile</label>
                                            <div class="col-md-6">
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-fax"></i>
                                                    </span>
                                                    <input type="text" class="form-control" name="faxNo" value="<%=contact.getFaxNo()%>">
                                                </div>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="address" value="<%=contact.getAddress()%>" required>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label" for="textareaDefault">Notes</label>
                                            <div class="col-md-6">
                                                <textarea class="form-control" rows="3" name="notes"><%=contact.getNotes()%></textarea>
                                            </div>
                                        </div>
                                        <input type="hidden" name="id" value="<%=customerID%>">   
                                        <input type="hidden" name="id2" value="<%=contactID%>">   
                                        <input type="hidden" name="target" value="UpdateContact">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-primary">Submit</button>
                                                <input type="button"  class="btn btn-primary" value="Back" onclick="javascript:back()"/>
                                            </div>
                                        </div>
                                    </footer>
                                </section>
                            </form>
                        </div>
                    </div>
                    <!-- end: page -->
                </section>
            </div>
        </section>
        <jsp:include page="../foot.html" />
    </body>
</html>
<%
        }
    }
%>