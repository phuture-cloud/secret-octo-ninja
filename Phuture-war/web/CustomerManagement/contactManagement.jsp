<%@page import="EntityManager.Customer"%>
<%@page import="EntityManager.Customer"%>
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
            List<Customer> customers = (List<Customer>) session.getAttribute("customers");
            Customer customer = new Customer();
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getId() == Long.parseLong(customerID)) {
                    customer = customers.get(i);
                }
            }

%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script>
            function updateContact(id, contactID) {
                contactManagement.id.value = id;
                contactManagement.contactID.value = contactID;
                document.contactManagement.action = "contactManagement_update.jsp";
                document.contactManagement.submit();
            }
            function removeContact(id) {
                checkboxes = document.getElementsByName('delete');
                var numOfTicks = 0;
                for (var i = 0, n = checkboxes.length; i < n; i++) {
                    if (checkboxes[i].checked) {
                        numOfTicks++;
                    }
                }
                contactManagement.id.value = id;
                contactManagement.target.value = "RemoveContact";
                document.contactManagement.action = "../CustomerManagementController";
                document.contactManagement.submit();
            }
            function checkAll(source) {
                checkboxes = document.getElementsByName('delete');
                for (var i = 0, n = checkboxes.length; i < n; i++) {
                    checkboxes[i].checked = source.checked;
                }
            }
            function addContact(id) {
                contactManagement.id.value = id;
                document.contactManagement.action = "contactManagement_add.jsp";
                document.contactManagement.submit();
            }
            function back() {
                contactManagement.target.value = "ListAllCustomer";
                document.contactManagement.action = "../CustomerManagementController";
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
                    <form name="contactManagement">
                        <section class="panel">
                            <header class="panel-heading">
                                <h2 class="panel-title">Contact Management - <%=customer.getCustomerName()%></h2>
                            </header>
                            <div class="panel-body">

                                <div class="row">
                                    <div class="col-md-12">
                                        <input class="btn btn-primary" name="btnAdd" type="submit" value="Add Contact" onclick="addContact(<%=customerID%>)"  />
                                        <a class="mb-xs mt-xs mr-xs modal-with-move-anim btn btn-primary" href="#modalRemove">Remove Contact</a>
                                        <div id="modalRemove" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
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
                                                            <p>Are you sure that you want to remove these contact(s)?</p>
                                                        </div>
                                                    </div>
                                                </div>
                                                <footer class="panel-footer">
                                                    <div class="row">
                                                        <div class="col-md-12 text-right">
                                                            <input class="btn btn-primary modal-confirm" name="btnRemove" type="submit" value="Confirm" onclick="removeContact(<%=customerID%>)"  />
                                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                                        </div>
                                                    </div>
                                                </footer>
                                            </section>
                                        </div>
                                        <input class="btn btn-primary" name="btnBack" type="submit" value="Back" onclick="back()"  />
                                    </div>
                                </div>
                                <br/>


                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th><input type="checkbox"onclick="checkAll(this)" /></th>
                                            <th>Name</th>
                                            <th>Email</th>
                                            <th>Office Number</th>
                                            <th>Mobile Number</th>
                                            <th>Fax Number</th>
                                            <th>Address</th>
                                            <th>Notes</th>
                                            <th>Action</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            List<Contact> contacts = (List<Contact>) (session.getAttribute("contacts"));
                                            if (contacts.size() > 0) {
                                                for (int i = 0; i < contacts.size(); i++) {
                                                    if (!contacts.get(i).getIsDeleted()) {
                                        %>
                                        <tr>
                                            <td><input type="checkbox" name="delete" value="<%=contacts.get(i).getId()%>" /></td>
                                            <td><%=contacts.get(i).getName()%></td>
                                            <td><%=contacts.get(i).getEmail()%></td>
                                            <td><%=contacts.get(i).getOfficeNo()%></td>
                                            <td><%=contacts.get(i).getMobileNo()%></td>
                                            <td><%=contacts.get(i).getFaxNo()%></td>
                                            <td><%=contacts.get(i).getAddress()%></td>
                                            <td>
                                                <a class="modal-with-move-anim btn btn-default" href="#modalNotes">View</a>

                                                <div id="modalNotes" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <header class="panel-heading">
                                                            <h2 class="panel-title">Notes</h2>
                                                        </header>
                                                        <div class="panel-body">
                                                            <div class="modal-wrapper">
                                                                <div class="modal-text" style="height: 350px;">
                                                                    <textarea style="height:100%; width: 100%;"><%=contacts.get(i).getNotes()%></textarea>
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
                                            </td>
                                            <td>
                                                <input type="button" name="btnEdit" class="btn btn-primary" value="Update" onclick="javascript:updateContact('<%=customerID%>', '<%=contacts.get(i).getId()%>')"/>
                                            </td>
                                            <td>
                                                <%
                                                    if (contacts.get(i).getIsPrimaryContact()) {
                                                        out.print("<i class='fa fa-star' style='color:gold'></i>");
                                                    } else {
                                                        out.print("<a href='../CustomerManagementController?target=SetPrimaryContact&id=" + customerID + "&id2=" + contacts.get(i).getId() + "'><i class='fa fa-star-o'></i></a>");
                                                    }
                                                %>
                                            </td>

                                            <%
                                                        }
                                                    }
                                                } else {
                                                    out.print("<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>");
                                                }
                                            %>
                                        </tr>
                                    </tbody>
                                </table>
                                <input type="hidden" name="contactID" value="">
                                <input type="hidden" name="id" value="">
                                <input type="hidden" name="target" value="">    

                            </div>

                        </section>
                    </form>
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