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
        <jsp:include page="../head.html" />
    </head>
    <body>
        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Add Customer</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Customer Management</span></li>
                                <li><span>Add Customer &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../CustomerManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Add Customer</h2>
                                    </header>

                                    <div class="panel-body">
                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Company Name</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="companyName" required>
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
                                            <label class="col-md-3 control-label">Name</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" required>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Email</label>
                                            <div class="col-md-6">
                                                <input type="email" class="form-control" name="email" required>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Office Numbers</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="officeNo" required>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Mobile Numbers</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="mobileNo" required>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Fax Numbers</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="faxNo" required>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Address</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="address" required>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label" for="textareaDefault">Notes</label>
                                            <div class="col-md-6">
                                                <textarea class="form-control" rows="3" name="notes"></textarea>
                                            </div>
                                        </div>

                                        <input type="hidden" name="target" value="AddCustomer">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-primary">Submit</button>
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


        <jsp:include page="../foot.html" />
    </body>
</html>
<%
    }
%>