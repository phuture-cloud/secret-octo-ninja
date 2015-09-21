<%@page import="EntityManager.Supplier"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else {
        String supplierID = request.getParameter("id");
        String name = request.getParameter("name");
        if (supplierID == null || supplierID.isEmpty() || name == null || name.isEmpty()) {
            response.sendRedirect("supplierManagement.jsp?errMsg=An error has occured.");
        } else {
%>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
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
                        <h2>Update Supplier</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li>
                                    <a href="../SupplierManagementController?target=ListAllSupplier">
                                        Supplier Management
                                    </a>
                                </li>
                                <li><span>Update Supplier &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../SupplierManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Supplier</h2>
                                    </header>

                                    <div class="panel-body">

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Company Name</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" value="<%=name%>" title="Please do not insert illegal characters" pattern="^[A-Za-z0-9 _@]*[A-Za-z0-9][A-Za-z0-9 _@    ]*$" required>
                                            </div>
                                        </div>

                                        <input type="hidden" name="id" value="<%=supplierID%>">   
                                        <input type="hidden" name="target" value="UpdateSupplier">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-success">Save</button>
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