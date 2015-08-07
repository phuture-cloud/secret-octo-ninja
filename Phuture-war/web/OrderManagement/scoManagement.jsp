<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<SalesConfirmationOrder> salesConfirmationOrders = (List<SalesConfirmationOrder>) (session.getAttribute("salesConfirmationOrders"));
    String previousMgtPage = (String) session.getAttribute("previousManagementPage");
    if (previousMgtPage == null) {
        previousMgtPage = "";
    }

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
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script src="../assets/vendor/nprogress/nprogress.js"></script>
        <script>
        function createSCO() {
            window.location.href = "../OrderManagementController?target=ListAllCustomer";
        }
        function viewSCO(id) {
            scoManagement.id.value = id;
            scoManagement.target.value = "RetrieveSCO";
            document.scoManagement.action = "../OrderManagementController";
            document.scoManagement.submit();
        }
        function refreshSCOs() {
            NProgress.start();
            window.location.href = "../OrderManagementController?target=RefreshSCOs"
        }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Sales Confirmation Order Management</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <% if (previousMgtPage.equals("sco")) { %>
                                <li><span>SCO Management &nbsp;&nbsp</span></li>
                                    <%
                                    } else if (previousMgtPage.equals("soa")) {
                                    %>
                                    <% }%>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Sales Confirmation Order Management</h2>
                        </header>
                        <div class="panel-body">

                            <div class="row">
                                <div class="col-md-12"> 
                                    <button class="btn btn-primary" onclick="createSCO()">Create Sales Confirmation Order</button>
                                    <button class="btn btn-default" onclick="refreshSCOs();"><i class="fa fa-refresh"></i> Refresh Orders</button>
                                </div>
                            </div>
                            <br/>

                            <form name="scoManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr> 
                                            <th>SCO No.</th>
                                            <th>Customer</th>
                                            <th>SCO Date</th>
                                            <th>Total Amount</th>
                                            <th>Total Invoiced</th>
                                            <th>Delivery Orders</th>
                                            <th>Invoices</th>
                                            <th></th>
                                            <th>Status</th>
                                            <th style="width: 300px; text-align: center">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            if (salesConfirmationOrders != null && salesConfirmationOrders.size() > 0) {
                                                for (int i = 0; i < salesConfirmationOrders.size(); i++) {
                                        %>
                                        <tr>
                                            <td><%=salesConfirmationOrders.get(i).getSalesPerson().getStaffPrefix()+ "-" + salesConfirmationOrders.get(i).getSalesConfirmationOrderNumber()%></td>
                                            <td><a href="../CustomerManagementController?target=ListCustomerContacts&id=<%=salesConfirmationOrders.get(i).getCustomer().getId()%>"><%=salesConfirmationOrders.get(i).getCustomerName()%></a></td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                    String date = DATE_FORMAT.format(salesConfirmationOrders.get(i).getSalesConfirmationOrderDate());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                    out.print(formatter.format(salesConfirmationOrders.get(i).getTotalPrice()));
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    if (salesConfirmationOrders.get(i).getTotalInvoicedAmount() != null) {
                                                        out.print(formatter.format(salesConfirmationOrders.get(i).getTotalInvoicedAmount()));
                                                    } else {
                                                        out.println("NA");
                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <%
//                                                    if (salesConfirmationOrders.get(i).getDeliveryOrders().size() > 0) {
//                                                        for (int k = 0; k < salesConfirmationOrders.get(i).getDeliveryOrders().size(); k++) {
//                                                            if ((k + 1) == salesConfirmationOrders.get(i).getDeliveryOrders().size()) {
//                                                                out.print(salesConfirmationOrders.get(i).getDeliveryOrders().get(i).getDeliveryOrderNumber());
//                                                            } else {
//                                                                out.print(salesConfirmationOrders.get(i).getDeliveryOrders().get(i).getDeliveryOrderNumber() + " , ");
//                                                            }
//                                                        }
//                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <%
//                                                    if (salesConfirmationOrders.get(i).getInvoices().size() > 0) {
//                                                        for (int k = 0; k < salesConfirmationOrders.get(i).getInvoices().size(); k++) {
//                                                            if ((k + 1) == salesConfirmationOrders.get(i).getDeliveryOrders().size()) {
//                                                                out.print(salesConfirmationOrders.get(i).getInvoices().get(i).getInvoiceNumber());
//                                                            } else {
//                                                                out.print(salesConfirmationOrders.get(i).getInvoices().get(i).getInvoiceNumber() + " , ");
//                                                            }
//                                                        }
//                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <% if (salesConfirmationOrders.get(i).getTotalInvoicedAmount() == null) {
                                                        //If there was some errors calculating the invoiced amount
                                                        out.println("<i class='fa fa-exclamation-triangle' style='color:yellow' data-toggle='tooltip' data-placement='top' title='Unable to calculate invoiced amount'></i>");
                                                    } else if (salesConfirmationOrders.get(i).getTotalInvoicedAmount() < salesConfirmationOrders.get(i).getTotalPrice()) {
                                                        //If total invoiced less than SCO amount
                                                        out.println("<i class='fa fa-exclamation-circle' style='color:red' data-toggle='tooltip' data-placement='top' title='Amount invoiced is less than ordered amount'></i>");
                                                    } else if (salesConfirmationOrders.get(i).getTotalInvoicedAmount() > salesConfirmationOrders.get(i).getTotalPrice()) {
                                                        //If total invoiced more than SCO amount
                                                        out.println("<i class='fa fa-exclamation-circle' style='color:orange' data-toggle='tooltip' data-placement='top' title='Amount invoiced is more than ordered amount'></i>");
                                                    } else {
                                                        //If total invoiced equal
                                                    }%>
                                            </td>
                                            <%
                                                if (salesConfirmationOrders.get(i).getStatus().equals("Unfulfilled")) {
                                                    out.print("<td>Unfulfilled</td>");
                                                } else if (salesConfirmationOrders.get(i).getStatus().equals("Fulfilled")) {
                                                    out.print("<td class='info'>Fulfilled</td>");
                                                } else if (salesConfirmationOrders.get(i).getStatus().equals("Completed")) {
                                                    out.print("<td class='success'>Completed</td>");
                                                } else if (salesConfirmationOrders.get(i).getStatus().equals("Write-Off")) {
                                                    out.print("<td class='warning'>Write-Off</td>");
                                                }
                                            %>
                                            <td>
                                                <button class="btn btn-default btn-block" onclick="javascript:viewSCO(<%=salesConfirmationOrders.get(i).getId()%>)">View</button>
                                            </td>
                                        </tr>
                                        <%
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