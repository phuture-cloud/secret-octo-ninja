<%@page import="EntityManager.SupplierContact"%>
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
        String supplierID = request.getParameter("id");
        String supplierContactID = request.getParameter("supplierContactID");
        if (supplierContactID == null || supplierID == null || supplierContactID.isEmpty() || supplierID.isEmpty()) {
            response.sendRedirect("supplierManagement.jsp?errMsg=An error has occured.");
        } else {
            List<SupplierContact> supplierContacts = (List<SupplierContact>) session.getAttribute("supplierContacts");
            SupplierContact supplierContact = new SupplierContact();
            for (int i = 0; i < supplierContacts.size(); i++) {
                if (supplierContacts.get(i).getId() == Long.parseLong(supplierContactID)) {
                    supplierContact = supplierContacts.get(i);
                }
            }
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body>
        <script>
            function back() {
                window.onbeforeunload = null;
                window.location.href = "../SupplierManagementController?target=ListSupplierContacts&id=<%=supplierID%>";
            }

            window.onbeforeunload = function () {
                return 'There are unsaved changes to this page. If you continue, you will lose them';
            };

            $(function () {
                $('button[type=submit]').click(function (e) {
                    window.onbeforeunload = null;
                });
            });
        </script>
        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Update Contact</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li>
                                    <a href="../SupplierManagementController?target=ListAllSupplier">
                                        Supplier Management
                                    </a>
                                </li>
                                <li>
                                    <a href="../SupplierManagementController?target=ListSupplierContacts&id=<%=supplierID%>">
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
                            <form class="form-horizontal form-bordered" action="../SupplierManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Contact</h2>
                                    </header>
                                    <div class="panel-body">

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Name <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" value="<%=supplierContact.getName()%>" title="Please do not insert illegal characters" pattern="^[A-Za-z0-9 _@]*[A-Za-z0-9][A-Za-z0-9 _@    ]*$" required>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Email <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-envelope"></i>
                                                    </span>
                                                    <input type="email" class="form-control" name="email" value="<%=supplierContact.getEmail()%>" required>
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
                                                    <input type="text" class="form-control" name="officeNo" value="<%=supplierContact.getOfficeNo()%>" required>
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
                                                    <input type="text" class="form-control" name="mobileNo" value="<%=supplierContact.getMobileNo()%>">
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
                                                    <input type="text" class="form-control" name="faxNo" value="<%=supplierContact.getFaxNo()%>">
                                                </div>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <textarea class="form-control" rows="3" name="address" required><%=supplierContact.getAddress()%></textarea>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label" for="textareaDefault">Notes</label>
                                            <div class="col-md-6">
                                                <textarea class="form-control" rows="3" name="notes"><%=supplierContact.getNotes()%></textarea>
                                            </div>
                                        </div>
                                        <input type="hidden" name="id" value="<%=supplierID%>">   
                                        <input type="hidden" name="id2" value="<%=supplierContactID%>">   
                                        <input type="hidden" name="target" value="UpdateSupplierContact">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-success" type="submit">Save</button>
                                                <input type="button"  class="btn btn-default" value="Back" onclick="javascript:back()"/>
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
        <jsp:include page="../jspIncludePages/foot.html" />
    </body>
</html>
<%
        }
    }
%>