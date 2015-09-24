<%@page import="EntityManager.PurchaseOrder"%>
<%@page import="EntityManager.Invoice"%>
<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="EntityManager.Supplier"%>
<%@page import="EntityManager.SupplierContact"%>
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
        List<SupplierContact> supplierContacts = (List<SupplierContact>) (session.getAttribute("supplierContacts"));
        List<Supplier> suppliers = (List<Supplier>) session.getAttribute("suppliers");
        String selectedSupplierID = request.getParameter("selectedSupplierID");
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

            function getSupplierSupplierContacts1() {
                window.onbeforeunload = null;
                var scoID = document.getElementById("scoID").value;
                var customerID = document.getElementById("customerList").value;
                if (customerID !== "") {
                    window.location.href = "../OrderManagementController?target=ListSupplierSupplierContacts&customerID=" + customerID + "&source=addressBook&id=" + scoID;
                }
            }

            function getSupplierSupplierContacts2() {
                window.onbeforeunload = null;
                var customerID = document.getElementById("customerList").value;
                if (customerID !== "") {
            <%if (previousPage.equals("delivery")) {%>
                    window.location.href = "../DeliveryOrderManagementController?target=ListSupplierSupplierContacts&customerID=" + customerID + "&source=addressBook";
            <%} else if (previousPage.equals("invoice")) {%>
                    window.location.href = "../InvoiceManagementController?target=ListSupplierSupplierContacts&customerID=" + customerID + "&source=addressBook";
            <%}%>
                }
            }

            function save() {
                window.onbeforeunload = null;
            <% if (previousPage.equals("sco")) {%>
                document.UpdateSupplierContactForm.action = "../OrderManagementController";
            <%} else if (previousPage.equals("delivery")) {%>
                document.UpdateSupplierContactForm.action = "../DeliveryOrderManagementController";
            <%} else if (previousPage.equals("invoice")) {%>
                document.UpdateSupplierContactForm.action = "../InvoiceManagementController";
            <%}%>
                document.UpdateSupplierContactForm.submit();
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
                        <h2>Update Order Supplier</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../PurchaseOrderManagementController?target=ListAllPO">Purchase Orders</a></span></li>
                                <li><span><a href= "../PurchaseOrderManagementController?target=RetrievePO&id=<%=purchaseOrder.getId()%>"><%=purchaseOrder.getSalesConfirmationOrder().getSalesPerson().getStaffPrefix()%><%=purchaseOrder.getPurchaseOrderNumber()%></a></span></li>
                                <li><span>Update Order Supplier Details &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <div class="row">
                        <div class="col-lg-12">
                            <form class="form-horizontal form-bordered" name="UpdateSupplierContactForm">
                                <section class="panel">
                                    <header class="panel-heading">
                                        <h2 class="panel-title">Update Order Supplier Details</h2>
                                    </header>
                                    <div class="panel-body">
                                        <div class="form-group">
                                            <label class="col-md-3 control-label">Supplier</label>
                                            <div class="col-md-6">
                                                <%
                                                    if (scoID != null) {
                                                        out.print("<select id='customerList' name='customerID' data-plugin-selectTwo class='form-control populate' onchange='javascript:getSupplierSupplierContacts1()' required>");
                                                    } else {
                                                        out.print("<select id='customerList' name='customerID' data-plugin-selectTwo class='form-control populate' onchange='javascript:getSupplierSupplierContacts2()' required>");
                                                    }
                                                    out.print("<option value=''>Select a customer</option>");
                                                    if (suppliers != null && suppliers.size() > 0) {
                                                        for (int i = 0; i < suppliers.size(); i++) {
                                                            if (selectedSupplierID != null && selectedSupplierID.equals(suppliers.get(i).getId().toString())) {
                                                                out.print("<option value='" + suppliers.get(i).getId() + "' selected>" + suppliers.get(i).getSupplierName() + "</option>");
                                                            } else {
                                                                out.print("<option value='" + suppliers.get(i).getId() + "'>" + suppliers.get(i).getSupplierName() + "</option>");
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
                                                <select id="customerSupplierContactid" name="contactID" data-plugin-selectTwo class="form-control populate" required>
                                                    <option value="">Select a contact</option>
                                                    <%
                                                        if (supplierContacts != null && supplierContacts.size() > 0) {
                                                            for (int i = 0; i < supplierContacts.size(); i++) {
                                                                out.print("<option value='" + supplierContacts.get(i).getId() + "'>" + supplierContacts.get(i).getName() + "</option>");
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
                                <input type="hidden" name="target" value="UpdateSCOSupplierContact">   
                                <input type="hidden" name="source" value="UpdateSupplierContact">   
                                <%} else if (previousPage.equals("invoice")) {%>
                                <input type="hidden" name="target" value="UpdateInvoiceSupplierContact">   
                                <input type="hidden" name="source" value="UpdateSupplierContact">   
                                <%} else if (previousPage.equals("delivery")) {%>
                                <input type="hidden" name="target" value="UpdateDOSupplierContact">   
                                <input type="hidden" name="source" value="UpdateSupplierContact">   
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