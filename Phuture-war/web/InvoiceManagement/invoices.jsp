<%@page import="java.text.NumberFormat"%>
<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="EntityManager.Invoice"%>
<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Customer"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    String previousMgtPage = (String) session.getAttribute("previousManagementPage");
    if (previousMgtPage == null) {
        previousMgtPage = "";
    }
    SalesConfirmationOrder sco = null;
    StatementOfAccount soa = null;
    if (previousMgtPage != null) {
        if (previousMgtPage.equals("sco")) {
            sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
            if (sco == null) {
                response.sendRedirect("../workspace.jsp?errMsg=An Error has occured.");
            }
        } else if (previousMgtPage.equals("soa")) {
            soa = (StatementOfAccount) (session.getAttribute("statementOfAccount"));
        }
    }

    List<Invoice> invoices = (List<Invoice>) (session.getAttribute("listOfInvoice"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
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
            function viewInvoice(id) {
                window.location.href = "../InvoiceManagementController?target=RetrieveInvoice&id=" + id;
            }

            function back() {
            <% if (previousMgtPage.equals("sco")) {%>
                window.location.href = "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>";
            <% } else if (previousMgtPage.equals("soa")) {%>
                window.location.href = "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=soa.getCustomer().getId()%>";
            <%}%>
            }
            function refreshInvoices() {
                NProgress.start();
                window.location.href = "../InvoiceManagementController?target=RefreshInvoices"
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Invoices</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>

                                <%if (previousMgtPage.equals("sco")) {%>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>"><%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                            <%} else if (previousMgtPage.equals("soa")) {%>
                                <li><span><a href= "../StatementOfAccountManagementController?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span><a href="../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=soa.getCustomer().getId()%>">SOA</a></span></li>
                                            <%}%>

                                <li><span>Invoices &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <%
                                if (previousMgtPage.equals("sco")) {
                            %>
                            <h2 class="panel-title">SCO No. <%=sco.getSalesConfirmationOrderNumber()%> - Invoices</h2>
                            <%} else {%>
                            <h2 class="panel-title">Invoices</h2>
                            <%}%>
                        </header>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-md-12"> 
                                    <button class="btn btn-default" onclick="refreshInvoices();"><i class="fa fa-refresh"></i> Refresh Invoices</button>
                                </div>
                            </div>
                            <br/>
                            <form name="scoManagement_invoice">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th>Invoice #</th>
                                            <th>Invoice Date</th>
                                            <th>Invoiced Amount</th>
                                            <th>Amount Paid</th>
                                            <th>Invoice Status</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (int i = 0; i < invoices.size(); i++) {
                                        %>
                                        <tr>        
                                            <td>
                                                <% if (invoices.get(i).getTotalAmountPaid()== null) {
                                                        //If there was some errors calculating the payment amount
                                                        out.println();
                                                    } else if (invoices.get(i).getTotalAmountPaid() < invoices.get(i).getTotalPrice()) {
                                                    //If total paid less than invoiced amount
                                                        //todo
                                                        out.println();
                                                    } else if (invoices.get(i).getTotalAmountPaid() < invoices.get(i).getTotalPrice()) {
                                                        //If total paid more than invoiced amount
                                                        out.println();
                                                    } else {
                                                        //If total paid equal invoiced
                                                    }%>
                                            </td>
                                            <td><%=invoices.get(i).getInvoiceNumber()%></td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                    String date = DATE_FORMAT.format(invoices.get(i).getDateCreated());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td><%=formatter.format(invoices.get(i).getTotalPrice())%></td>
                                            <td>
                                                <%
                                                    if (invoices.get(i).getTotalAmountPaid()!= null) {
                                                        out.print(formatter.format(invoices.get(i).getTotalAmountPaid()));
                                                    } else {
                                                        out.println("NA");
                                                    }
                                                %>
                                            </td>
                                            <td><%=invoices.get(i).getStatus()%></td>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewInvoice('<%=invoices.get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%
                                            }
                                        %>

                                    </tbody>
                                </table>
                                <br>
                                <%if (!previousMgtPage.equals("invoices")) {%>
                                <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                <%}%>
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