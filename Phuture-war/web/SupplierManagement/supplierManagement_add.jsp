<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else {
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body>
        <script>
            function back() {
                window.location.href = "../SupplierManagementController?target=ListAllSupplier";
            }
        </script>
        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Add Supplier</h2>
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
                                <li><span>Add Supplier &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../SupplierManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Add Supplier</h2>
                                    </header>

                                    <div class="panel-body">
                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Company Name <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="companyName" title="Please do not insert illegal characters" pattern="^[A-Za-z0-9 _@]*[A-Za-z0-9][A-Za-z0-9 _@    ]*$" required>
                                            </div>
                                        </div>
                                    </div>
                                </section>

                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Primary Contact</h2>
                                    </header>
                                    <div class="panel-body">

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Name <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" title="Please do not insert illegal characters" pattern="^[A-Za-z0-9 _@]*[A-Za-z0-9][A-Za-z0-9 _@    ]*$" required>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Email <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <div class="input-group">
                                                    <span class="input-group-addon">
                                                        <i class="fa fa-envelope"></i>
                                                    </span>
                                                    <input type="email" class="form-control" name="email" required>
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
                                                    <input type="text" class="form-control" name="officeNo" required>
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
                                                    <input type="text" class="form-control" name="mobileNo">
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
                                                    <input type="text" class="form-control" name="faxNo">
                                                </div>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                            <div class="col-md-6">
                                                <textarea class="form-control" rows="3" name="address" required></textarea>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label" for="textareaDefault">Notes</label>
                                            <div class="col-md-6">
                                                <textarea class="form-control" rows="3" name="notes"></textarea>
                                            </div>
                                        </div>

                                        <input type="hidden" name="target" value="AddSupplier">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-primary">Submit</button>
                                                <input type="button"  class="btn btn-default" value="Back" onclick="javascript:back()"/>
                                                <button type="reset" class="btn btn-default">Reset</button>
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
%>