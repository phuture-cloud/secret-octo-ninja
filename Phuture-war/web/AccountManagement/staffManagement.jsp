<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    List<Staff> staffs = (List<Staff>) (session.getAttribute("staffs"));
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
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script>
            function updateStaff(id) {
                window.location.href = "staffManagement_update.jsp?id=" + id;
            }
            function removeStaff(id) {
                staffManagement.id.value = id;
                staffManagement.target.value = "DisableStaff";
                document.staffManagement.action = "../AccountManagementController";
                document.staffManagement.submit();
            }
            function addStaff() {
                window.location.href = "staffManagement_add.jsp";
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Staff Management</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Staff Management &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Staff Management</h2>
                        </header>
                        <div class="panel-body">

                            <div class="row">
                                <div class="col-md-12"> 
                                    <button class="btn btn-primary" type="button" onclick="addStaff()">Add Staff</button>
                                </div>
                            </div>
                            <br/>

                            <form name="staffManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Prefix</th>
                                            <th>Username</th>
                                            <th>Status</th>
                                            <th style="width: 300px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            int temp = 2;
                                            if (staff.getIsAdmin()) {
                                                temp = 1;
                                            }

                                            if (staffs.size() > temp) {
                                                for (int i = 0; i < staffs.size(); i++) {
                                                    if (staffs != null && !staffs.get(i).getUsername().equals(staff.getUsername()) && !staffs.get(i).getIsAdmin()) {
                                        %>
                                        <tr>        
                                            <td><%=staffs.get(i).getName()%></td>
                                            <td><%=staffs.get(i).getStaffPrefix()%></td>
                                            <td><%=staffs.get(i).getUsername()%></td>
                                            <td>
                                                <%
                                                    if (!staffs.get(i).getIsDisabled()) {
                                                        out.print("<span class='label label-success' style='font-size: 100%;'>Active</span>");
                                                    } else {
                                                        out.print("<span class='label label-warning' style='font-size: 100%; background-color:#B8B8B8;'>Disabled</span>");
                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <% if (!staffs.get(i).getIsDisabled()) {%>
                                                <div class="btn-group" role="group" aria-label="...">
                                                    <button type="button" class="btn btn-default" onclick="javascript:updateStaff('<%=staffs.get(i).getId()%>')">Update</button>
                                                    <button type="button" class="modal-with-move-anim btn btn-default" href="#modalRemove<%=staffs.get(i).getId()%>">Disable</button>
                                                </div>
                                                <div id="modalRemove<%=staffs.get(i).getId()%>" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <header class="panel-heading">
                                                            <h2 class="panel-title">Are you sure?</h2>
                                                        </header>
                                                        <div class="panel-body">
                                                            <div class="modal-wrapper">
                                                                <div class="modal-icon">
                                                                    <i class="fa fa-question-circle"></i>
                                                                </div>
                                                                <div class="modal-text">
                                                                    <p>Are you sure that you want to disable this staff?</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <footer class="panel-footer">
                                                            <div class="row">
                                                                <div class="col-md-12 text-right">
                                                                    <button class="btn btn-primary modal-confirm" onclick="removeStaff(<%=staffs.get(i).getId()%>)">Confirm</button>
                                                                    <button class="btn btn-default modal-dismiss">Cancel</button>
                                                                </div>
                                                            </div>
                                                        </footer>
                                                    </section>
                                                </div>
                                                <% } else {%>
                                                <input type="button" class="btn btn-primary" value="Update" disabled/>
                                                <input type="button" class="btn btn-primary" value="Disable" disabled/>
                                                <%  }  %>
                                            </td>
                                        </tr>
                                        <%
                                                    }
                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>

                                <input type="hidden" name="id" value="">
                                <input type="hidden" name="target" value="">    
                            </form>
                        </div>

                    </section>
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