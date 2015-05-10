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
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script>
            function updateStaff(username) {
                staffManagement.username.value = username;
                document.staffManagement.action = "staffManagement_update.jsp";
                document.staffManagement.submit();
            }
            function removeStaff() {
                checkboxes = document.getElementsByName('delete');
                var numOfTicks = 0;
                for (var i = 0, n = checkboxes.length; i < n; i++) {
                    if (checkboxes[i].checked) {
                        numOfTicks++;
                    }
                }
                if (checkboxes.length == 0 || numOfTicks == 0) {
                    staffManagement.target.value = 'ListAllStaff';
                    window.event.returnValue = true;
                    document.staffManagement.action = "../AccountManagementController";
                    document.staffManagement.submit();
                } else {
                    staffManagement.target.value = 'RemoveStaff';
                    window.event.returnValue = true;
                    document.staffManagement.action = "../AccountManagementController";
                    document.staffManagement.submit();
                }
            }
            function addStaff() {
                window.event.returnValue = true;
                document.staffManagement.action = "staffManagement_add.jsp";
                document.staffManagement.submit();
            }
            function checkAll(source) {
                checkboxes = document.getElementsByName('delete');
                for (var i = 0, n = checkboxes.length; i < n; i++) {
                    checkboxes[i].checked = source.checked;
                }
            }
        </script>

        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
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
                                <li><span>Account Management</span></li>
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
                                    <input class="btn btn-primary" name="btnAdd" type="submit" value="Add Staff" onclick="addStaff()"  />
                                    <a href="#myModal" data-toggle="modal"><button class="btn btn-primary">Disable Staff</button></a>
                                </div>
                            </div>
                            <br/>

                            <form name="staffManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th style="width: 5px;">&nbsp</th>
                                            <th>Name</th>
                                            <th>Prefix</th>
                                            <th>Username</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <%
                                                List<Staff> staffs = (List<Staff>) (session.getAttribute("staffs"));
                                                if (staffs != null) {
                                                    for (int i = 0; i < staffs.size(); i++) {
                                                        if (!staffs.get(i).getUsername().equals(staff.getUsername()) && !staffs.get(i).getIsDisabled()) {
                                            %>
                                            <td><input type="checkbox" name="delete" value="<%=staffs.get(i).getUsername()%>" /></td>
                                            <td><%=staffs.get(i).getName()%></td>
                                            <td><%=staffs.get(i).getStaffPrefix()%></td>
                                            <td><%=staffs.get(i).getUsername()%></td>
                                            <td><input type="button" name="btnEdit" class="btn btn-primary btn-block" id="<%=staffs.get(i).getUsername()%>" value="Update" onclick="javascript:updateStaff('<%=staffs.get(i).getUsername()%>')"/></td>
                                                <%
                                                            }
                                                        }
                                                    } else {
                                                        out.print("<td></td><td></td><td></td><td></td><td></td>");
                                                    }
                                                %>
                                        </tr>
                                    </tbody>
                                </table>

                                <input type="hidden" name="username" value="">
                                <input type="hidden" name="target" value="">    
                            </form>
                        </div>

                    </section>
                    <!-- end: page -->
                </section>
            </div>
        </section>


        <div role="dialog" class="modal fade" id="myModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4>Alert</h4>
                    </div>
                    <div class="modal-body">
                        <p id="messageBox">Staff will be disabled. Are you sure?</p>
                    </div>
                    <div class="modal-footer">                        
                        <input class="btn btn-primary" name="btnRemove" type="submit" value="Confirm" onclick="removeStaff()"  />
                        <a class="btn btn-default" data-dismiss ="modal">Close</a>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../foot.html" />
    </body>
</html>
<%
    }
%>