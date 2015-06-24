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
%>
<html class="fixed">
    <head>
        <jsp:include page="jspIncludePages/head-root.html" />
    </head>
    <body>
        <script>
            function updateProfile() {
                window.onbeforeunload = null;
                window.location.href = "../AccountManagementController?target=UpdateStaff";
            }
            window.onbeforeunload = function () {
                return 'There are unsaved changes to this page. If you continue, you will lose them';
            };
        </script>
        <section class="body">
            <jsp:include page="jspIncludePages/header-root.jsp" />

            <div class="root-wrapper">
                <jsp:include page="jspIncludePages/sidebar-root.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>User Profile</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>User Profile &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../AccountManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">User Profile</h2>
                                    </header>

                                    <div class="panel-body">

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Name</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="name" value="<%=staff.getName()%>">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Prefix</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="prefix" value="<%=staff.getStaffPrefix()%>">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Username</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="username" value="<%=staff.getUsername()%>" disabled>
                                            </div>
                                        </div>

                                        <br>
                                        <h4 align="center">Change Password (leave blank unless setting a new password).</h4>
                                        <br>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">New Password</label>
                                            <div class="col-md-6">
                                                <input id="password" type="pwd" title="Password must contain at least 6 characters, including UPPER/lowercase and numbers" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}"  name="pwd" class="form-control" onchange="form.repassword.pattern = this.value;">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Re-enter Password</label>
                                            <div class="col-md-6">
                                                <input id="repassword" type="password" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}" name="repassword" class="form-control">
                                            </div>
                                        </div>  
                                        <input type="hidden" name="target" value="UpdateProfile">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-success" onclick="javascript:updateProfile()">Save</button>
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
        <jsp:include page="jspIncludePages/foot-root.html" />
    </body>
</html>
<%
    }
%>