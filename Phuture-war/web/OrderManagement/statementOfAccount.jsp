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
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script>
            function viewSOA(id) {
                soaManagement.id.value = id;
                soaManagement.target.value = "RetrieveSOA";
                document.soaManagement.action = "../StatementOfAccountManagementController";
                document.soaManagement.submit();
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
                            <form name="soaManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr> 
                                            <th></th>
                                            <th>Customer</th>
                                            <th>Total Amount Ordered</th>
                                            <th>Total Amount Invoiced</th>
                                            <th>Total Amount Paid</th>
                                            <th>Status</th>
                                            <th style="width: 300px; text-align: center">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                            if (statementOfAccounts != null && statementOfAccounts.size() > 0) {
                                                for (int i = 0; i < statementOfAccounts.size(); i++) {
                                        %>
                                        <tr>
                                            <td><%=statementOfAccounts.get(i).getId()%></td>
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
                                                <button class="btn btn-default btn-block" onclick="javascript:viewSCO(<%=statementOfAccounts.get(i).getId()%>)">View</button>
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