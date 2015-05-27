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
        String staffID = request.getParameter("id");
        List<Staff> staffs = (List<Staff>) session.getAttribute("staffs");
        if (staffID == null || staffs == null) {
            response.sendRedirect("staffManagement.jsp?errMsg=An error has occured.");
        } else {
            staff = new Staff();
            for (int i = 0; i < staffs.size(); i++) {
                if (staffs.get(i).getId() == Long.parseLong(staffID)) {
                    staff = staffs.get(i);
                }
            }
%>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body>
        <script>
            function back() {
                window.location.href = "../AccountManagementController?target=ListAllStaff";
            }
        </script>
        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Update Staff</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Account Management</span></li>
                                <li><span><a href="../AccountManagementController?target=ListAllStaff">Staff Management</a></span></li>
                                <li><span>Update Staff &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../AccountManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Staff</h2>
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
                                        <input type="hidden" name="id" value="<%=staffID%>">   
                                        <input type="hidden" name="target" value="UpdateStaff">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-primary">Submit</button>
                                                <input type="button"  class="btn btn-primary" value="Back" onclick="javascript:back()"/>
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
    }
%>