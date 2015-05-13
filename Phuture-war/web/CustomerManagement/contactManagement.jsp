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
        String customerID = request.getParameter("id");
        if (customerID == null || customerID.isEmpty()) {
            response.sendRedirect("customerManagement.jsp?errMsg=An error has occured.");
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
            function updateContact(id) {
                contactManagement.id.value = id;
                document.contactManagement.action = "contactManagement_update.jsp";
                document.contactManagement.submit();
            }
            function removeContact(id) {
                contactManagement.id.value = id;
                contactManagement.target.value = "RemoveContact";
                document.contactManagement.action = "../CustomerManagementController";
                document.contactManagement.submit();
            }
            function addContact() {
                window.event.returnValue = true;
                document.contactManagement.action = "contactManagement_add.jsp";
                document.contactManagement.submit();
            }
        </script>

        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Contact Management</h2>
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
                                <li><span>Contact Management &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Contact Management</h2>
                        </header>
                        <div class="panel-body">

                            <div class="row">
                                <div class="col-md-12">
                                    <input class="btn btn-primary" name="btnAdd" type="submit" value="Add Contact" onclick="addContact()"  />
                                    <a href="#myModal" data-toggle="modal"><button class="btn btn-primary">Remove Contact</button></a>
                                </div>
                            </div>
                            <br/>

                            <form name="contactManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Email</th>
                                            <th>Office Number</th>
                                            <th>Mobile Number</th>
                                            <th>Fax Number</th>
                                            <th>Address</th>
                                            <th>Notes</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <%
                                                List<Contact> contacts = (List<Contact>) (session.getAttribute("contacts"));
                                                if (contacts.size() > 0) {
                                                    for (int i = 0; i < contacts.size(); i++) {
                                                        if (!contacts.get(i).getIsDeleted()) {
                                            %>
                                            <td><%=contacts.get(i).getName()%></td>
                                            <td><%=contacts.get(i).getEmail()%></td>
                                            <td><%=contacts.get(i).getOfficeNo()%></td>
                                            <td><%=contacts.get(i).getMobileNo()%></td>
                                            <td><%=contacts.get(i).getFaxNo()%></td>
                                            <td><%=contacts.get(i).getAddress()%></td>
                                            <td><%=contacts.get(i).getNotes()%></td>
                                            <td>
                                                <input type="button" name="btnEdit" class="btn btn-primary" value="Update" onclick="javascript:updateContact('<%=contacts.get(i).getId()%>')"/>
                                            </td>
                                            <%
                                                        }
                                                    }
                                                } else {
                                                    out.print("<td></td><td></td><td></td>");
                                                }
                                            %>
                                        </tr>
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


        <div role="dialog" class="modal fade" id="myModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4>Alert</h4>
                    </div>
                    <div class="modal-body">
                        <p id="messageBox">Contact will be Remove. Are you sure?</p>
                    </div>
                    <div class="modal-footer">                        
                        <input class="btn btn-primary" name="btnRemove" type="submit" value="Confirm" onclick="removeContact()"  />
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
    }
%>