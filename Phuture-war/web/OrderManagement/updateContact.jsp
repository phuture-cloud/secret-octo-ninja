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
        String scoID = request.getParameter("id");
        List<Contact> contacts = (List<Contact>) (session.getAttribute("contacts"));
        List<Customer> customers = (List<Customer>) session.getAttribute("customers");
        String selectedCustomerID = request.getParameter("selectedCustomerID");
        String selectedContactID = request.getParameter("selectedContactID");
        Contact contact = null;
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body>
        <script>
            window.onbeforeunload = function () {
                return 'There are unsaved changes to this page. If you continue, you will lose them';
            };

            $(function () {
                $('button[type=submit]').click(function (e) {
                    window.onbeforeunload = null;
                });
            });

            function getCustomerContacts() {
                window.onbeforeunload = null;
                var customerID = document.getElementById("customerList").value;
                if (customerID !== "") {
                    window.location.href = "../OrderManagementController?target=ListCustomerContacts&customerID=" + customerID + "&source=addressBook";
                }
            }
        </script>
        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Update Contact</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li>
                                    Sales Confirmation Order
                                </li>
                                <li><span>Update Sales Confirmation Order Contact Details &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" action="../CustomerManagementController">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Sales Confirmation Order Contact Details</h2>
                                    </header>
                                    <div class="panel-body">


                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Company</label>
                                            <div class="col-md-6">
                                                <select id="customerList" name="customerID" data-plugin-selectTwo class="form-control populate" onchange="javascript:getCustomerContacts()" required>
                                                    <option value="">Select a customer</option>
                                                    <%                                                            if (customers != null && customers.size() > 0) {
                                                            for (int i = 0; i < customers.size(); i++) {
                                                                if (selectedCustomerID != null && selectedCustomerID.equals(customers.get(i).getId().toString())) {
                                                                    out.print("<option value='" + customers.get(i).getId() + "' selected>" + customers.get(i).getCustomerName() + "</option>");
                                                                } else {
                                                                    out.print("<option value='" + customers.get(i).getId() + "'>" + customers.get(i).getCustomerName() + "</option>");
                                                                }
                                                            }
                                                        }
                                                    %>
                                                </select>
                                            </div>

                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Contact Person</label>
                                            <div class="col-md-6">
                                                <select id="customerContactid" name="contactID" data-plugin-selectTwo class="form-control populate"  onchange="javascript:selectCustomerContact()" required>
                                                    <option value="">Select a contact</option>
                                                    <%
                                                        if (contacts != null && contacts.size() > 0) {
                                                            for (int i = 0; i < contacts.size(); i++) {
                                                                if (selectedContactID != null && selectedContactID.equals(contacts.get(i).getId().toString())) {
                                                                    contact = contacts.get(i);
                                                                    out.print("<option value='" + contacts.get(i).getId() + "' selected>" + contacts.get(i).getName() + "</option>");
                                                                } else {
                                                                    out.print("<option value='" + contacts.get(i).getId() + "'>" + contacts.get(i).getName() + "</option>");
                                                                }
                                                            }
                                                        }
                                                    %>
                                                </select>
                                            </div>
                                        </div>

                                    </div>

                                    <footer class="panel-footer">
                                        <div class="row">
                                            <div class="col-sm-9 col-sm-offset-3">
                                                <button class="btn btn-success" type="submit">Save</button>
                                                <button class="btn btn-default" onclick="javascript:back()">Cancel</button>
                                            </div>
                                        </div>
                                    </footer>
                                </section>


                                <input type="hidden" name="target" value="UpdateContact">   
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
<%    }
%>