<%@page import="EntityManager.Invoice"%>
<%@page import="EntityManager.DeliveryOrder"%>
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
        String previousPage = request.getParameter("previousPage");
        String scoID = request.getParameter("id");
        List<Contact> contacts = (List<Contact>) (session.getAttribute("contacts"));
        List<Customer> customers = (List<Customer>) session.getAttribute("customers");
        String selectedCustomerID = request.getParameter("selectedCustomerID");
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
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

            function getCustomerContacts1() {
                window.onbeforeunload = null;
                var scoID = document.getElementById("scoID").value;
                var customerID = document.getElementById("customerList").value;
                if (customerID !== "") {
                    window.location.href = "../OrderManagementController?target=ListCustomerContacts&customerID=" + customerID + "&source=addressBook&id=" + scoID;
                }
            }

            function getCustomerContacts2() {
                window.onbeforeunload = null;
                var customerID = document.getElementById("customerList").value;
                if (customerID !== "") {
            <%if (previousPage.equals("delivery")) {%>
                    window.location.href = "../DeliveryOrderManagementController?target=ListCustomerContacts&customerID=" + customerID + "&source=addressBook";
            <%} else if (previousPage.equals("invoice")) {%>
                    window.location.href = "../InvoiceManagementController?target=ListCustomerContacts&customerID=" + customerID + "&source=addressBook";
            <%}%>
                }
            }

            function save() {
                window.onbeforeunload = null;
            <% if (previousPage.equals("sco")) {%>
                document.UpdateContactForm.action = "../OrderManagementController";
            <%} else if (previousPage.equals("delivery")) {%>
                document.UpdateContactForm.action = "../DeliveryOrderManagementController";
            <%} else if (previousPage.equals("invoice")) {%>
                document.UpdateContactForm.action = "../InvoiceManagementController";
            <%}%>
                document.UpdateContactForm.submit();
            }

            function back() {
                window.onbeforeunload = null;
            <% if (previousPage.equals("sco")) {%>
                window.location.href = "scoManagement_add.jsp?id=" + <%=scoID%>;
            <%} else if (previousPage.equals("delivery")) {%>
                window.location.href = "deliveryOrder.jsp";
            <%} else if (previousPage.equals("invoice")) {%>
                window.location.href = "invoice.jsp";
            <%}%>
            }
        </script>
        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Update Order Contact</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Update Order Contact Details &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" name="UpdateContactForm">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Order Contact Details</h2>
                                    </header>
                                    <div class="panel-body">
                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Company</label>
                                            <div class="col-md-6">
                                                <%
                                                    if (scoID != null) {
                                                        out.print("<select id='customerList' name='customerID' data-plugin-selectTwo class='form-control populate' onchange='javascript:getCustomerContacts1()' required>");
                                                    } else {
                                                        out.print("<select id='customerList' name='customerID' data-plugin-selectTwo class='form-control populate' onchange='javascript:getCustomerContacts2()' required>");
                                                    }
                                                    out.print("<option value=''>Select a customer</option>");
                                                    if (customers != null && customers.size() > 0) {
                                                        for (int i = 0; i < customers.size(); i++) {
                                                            if (selectedCustomerID != null && selectedCustomerID.equals(customers.get(i).getId().toString())) {
                                                                out.print("<option value='" + customers.get(i).getId() + "' selected>" + customers.get(i).getCustomerName() + "</option>");
                                                            } else {
                                                                out.print("<option value='" + customers.get(i).getId() + "'>" + customers.get(i).getCustomerName() + "</option>");
                                                            }
                                                        }
                                                    }
                                                    out.print("</select>");
                                                %>

                                            </div>

                                        </div>

                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Contact Person</label>
                                            <div class="col-md-6">
                                                <select id="customerContactid" name="contactID" data-plugin-selectTwo class="form-control populate" required>
                                                    <option value="">Select a contact</option>
                                                    <%
                                                        if (contacts != null && contacts.size() > 0) {
                                                            for (int i = 0; i < contacts.size(); i++) {
                                                                out.print("<option value='" + contacts.get(i).getId() + "'>" + contacts.get(i).getName() + "</option>");
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
                                                <button type="button" class="btn btn-success" type="submit" onclick="javascript:save();">Save</button>
                                                <button type="button" class="btn btn-default" onclick="javascript:back();">Cancel</button>
                                            </div>
                                        </div>
                                    </footer>
                                </section>

                                <%if (previousPage.equals("sco")) {%>
                                <input type="hidden" name="id" id="scoID" value="<%=scoID%>">   
                                <input type="hidden" name="target" value="UpdateSCOContact">   
                                <input type="hidden" name="source" value="UpdateContact">   
                                <%} else if (previousPage.equals("invoice")) {%>
                                <input type="hidden" name="target" value="UpdateInvoiceContact">   
                                <input type="hidden" name="source" value="UpdateContact">   
                                <%} else if (previousPage.equals("delivery")) {%>
                                <input type="hidden" name="target" value="UpdateDOContact">   
                                <input type="hidden" name="source" value="UpdateContact">   
                                <%}%>
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
%>