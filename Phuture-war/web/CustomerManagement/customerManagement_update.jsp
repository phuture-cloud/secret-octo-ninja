<%@page import="EntityManager.Customer"%>
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
        String customerID = request.getParameter("id");
        String name = request.getParameter("name");
        if (customerID == null || customerID.isEmpty() || name == null || name.isEmpty()) {
            response.sendRedirect("customerManagement.jsp?errMsg=An error has occured.");
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
                window.location.href = "../CustomerManagementController?target=ListAllCustomer";
            }
        </script>
        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Update Customer</h2>
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
                                <li><span>Update Customer &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../CustomerManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Customer</h2>
                                    </header>

                                    <div class="panel-body">

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Company Name</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" value="<%=name%>" required>
                                            </div>
                                        </div>

                                        <input type="hidden" name="id" value="<%=customerID%>">   
                                        <input type="hidden" name="target" value="UpdateCustomer">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-primary">Submit</button>
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