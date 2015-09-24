<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("index.jsp?errMsg=Session Expired.");
    } else {
%>
<html class="fixed">
    <head>
        <jsp:include page="jspIncludePages/head-root.html" />
        <link rel="stylesheet" href="assets/vendor/bootstrap-fileupload/bootstrap-fileupload.min.css" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="displayNotification.jsp" />
        <script>
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
            <jsp:include page="jspIncludePages/header-root.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="jspIncludePages/sidebar-root.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Profile</h2>
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
                            <form method="POST" enctype="multipart/form-data" class="form-horizontal form-bordered" action="AccountManagementController">
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
                                            <label class="col-md-3 control-label">Signature <span style="color: red;" data-toggle='tooltip' data-placement='top' title='Upload image width:150px - 350px, height: 80px'>(?)</span><br>(select file only if updating signature)</label>
                                            <div class="col-md-6">
                                                <div class="fileupload fileupload-new" data-provides="fileupload">
                                                    <div class="input-append">
                                                        <div class="uneditable-input">
                                                            <i class="fa fa-file fileupload-exists"></i>
                                                            <span class="fileupload-preview"></span>
                                                        </div>

                                                        <span class="btn btn-default btn-file">
                                                            <span class="fileupload-exists">Change</span>
                                                            <span class="fileupload-new">Select file</span>
                                                            <input type="file" id="picture" name="signature"> 
                                                        </span>
                                                        <a href="#" class="btn btn-default fileupload-exists" data-dismiss="fileupload">Remove</a>
                                                        <%if (staff.getSignature() != null && staff.getSignature().length > 0) {%>
                                                        <button type="button" href="#viewSign" class="btn btn-default modal-with-form">View Current Signature</button>
                                                        <%}%>
                                                    </div>
                                                </div>

                                                <div id="viewSign" class="modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <header class="panel-heading">
                                                            <h2 class="panel-title">Signature</h2>
                                                        </header>
                                                        <div class="panel-body">
                                                            <div class="form-group mt-lg">
                                                                <div class="col-sm-12">
                                                                    <center><img class="img-responsive" src="/sig?id=<%=staff.getId()%>"></center>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <footer class="panel-footer">
                                                            <div class="row">
                                                                <div class="col-md-12 text-right">
                                                                    <button class="btn btn-default modal-dismiss">Close</button>
                                                                </div>
                                                            </div>
                                                        </footer>
                                                    </section>
                                                </div>
                                            </div>
                                        </div>


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Username</label>
                                            <div class="col-md-6">
                                                <input type="text" class="form-control" name="username" value="<%=staff.getUsername()%>" disabled>
                                            </div>
                                        </div>

                                        <br>
                                        <h4 align="center">Change Password (leave blank unless setting a new password)</h4>
                                        <br>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">New Password</label>
                                            <div class="col-md-6">
                                                <input id="password" type="password" title="Password must contain at least 6 characters, including UPPER/lowercase and numbers" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}"  name="pwd" class="form-control" onchange="form.repassword.pattern = this.value;">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Re-enter Password</label>
                                            <div class="col-md-6">
                                                <input id="repassword" type="password" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}" name="repassword" class="form-control">
                                            </div>
                                        </div>
                                        <input type="hidden" name="id" value="<%=staff.getId()%>">   
                                        <input type="hidden" name="target" value="UpdateProfile">   
                                        <input type="hidden" name="username" value="<%=staff.getUsername()%>">   
                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-success" type="submit">Save</button>
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
        <script src="assets/vendor/jquery-autosize/jquery.autosize.js"></script>
        <script src="assets/vendor/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
        <jsp:include page="jspIncludePages/foot-root.html" />
    </body>
</html>
<%
    }
%>