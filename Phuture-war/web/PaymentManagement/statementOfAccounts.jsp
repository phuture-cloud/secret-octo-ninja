<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<StatementOfAccount> statementOfAccounts = (List<StatementOfAccount>) (session.getAttribute("statementOfAccounts"));
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (statementOfAccounts == null) {
        response.sendRedirect("../AccountManagement/workspace.jsp?errMsg=Session Expired.");
    } else {
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body onload="alertFunc()" data-loading-overlay>
        <jsp:include page="../displayNotification.jsp" />
        <script src="../assets/vendor/nprogress/nprogress.js"></script>
        <script>
        function viewSOA(id) {
            window.location.href = "../StatementOfAccountManagementController?target=RetrieveSOA&id=" + id;
        }
        function viewAllInvoice(id) {
            window.location.href = "../StatementOfAccountManagementController?target=ViewInvoiceTiedToCustomer&id=" + id;
        }
        function viewOverdueInvoice(id) {
            window.location.href = "../StatementOfAccountManagementController?target=ViewOverDueInvoiceTiedToCustomer&id=" + id;
        }

        function refreshSOA() {
            NProgress.start();
            window.location.href = "../StatementOfAccountManagementController?target=RefreshSOA"
        }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Statement of Accounts</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>SOA &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Statement of Account Management</h2>
                        </header>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-12"> 
                                    <button class="btn btn-default" onclick="refreshSOA();"><i class="fa fa-refresh"></i> Refresh Accounts</button>
                                </div>
                            </div>
                            <br/>

                            <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                <thead>
                                    <tr> 
                                        <th>Customer</th>
                                        <th>Total Amount Ordered</th>
                                        <th>Total Amount Invoiced</th>
                                        <th>Total Amount Paid</th>
                                        <th>Total Overdue</th>
                                        <th style="width: 480px; text-align: center">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                        if (statementOfAccounts != null && statementOfAccounts.size() > 0) {
                                            for (int i = 0; i < statementOfAccounts.size(); i++) {
                                    %>
                                    <tr>
                                        <td><a href="../CustomerManagementController?target=ListCustomerContacts&id=<%=statementOfAccounts.get(i).getCustomer().getId()%>"><%=statementOfAccounts.get(i).getCustomer().getCustomerName()%></a></td>
                                        <td>
                                            <%=formatter.format(statementOfAccounts.get(i).getTotalAmountOrdered())%>
                                        </td>
                                        <td>
                                            <%=formatter.format(statementOfAccounts.get(i).getTotalAmountInvoiced())%>
                                        </td>
                                        <td>
                                            <%=formatter.format(statementOfAccounts.get(i).getTotalAmountPaid())%>
                                        </td>
                                        <td>
                                            <%=formatter.format(statementOfAccounts.get(i).getTotalAmountOverDue())%>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group" aria-label="...">
                                                <button class="btn btn-default" onclick="javascript:viewSOA(<%=statementOfAccounts.get(i).getCustomer().getId()%>);">View Statement of Account</button>
                                                <button class="btn btn-default" onclick="javascript:viewAllInvoice(<%=statementOfAccounts.get(i).getCustomer().getId()%>);">View All Invoices</button>
                                                <button class="btn btn-default" onclick="javascript:viewOverdueInvoice(<%=statementOfAccounts.get(i).getCustomer().getId()%>);">View Overdue Invoices</button>
                                            </div>
                                        </td>
                                    </tr>
                                    <%
                                            }
                                        }
                                    %>
                                </tbody>
                            </table>
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