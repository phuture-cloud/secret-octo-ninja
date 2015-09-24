<%@page import="java.util.ArrayList"%>
<%@page import="EntityManager.PaymentRecord"%>
<%@page import="EntityManager.SOALineItem"%>
<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Contact"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    StatementOfAccount statementOfAccount = (StatementOfAccount) (session.getAttribute("statementOfAccount"));
    List<PaymentRecord> paymentRecords = (List<PaymentRecord>) (session.getAttribute("statementOfAccountPayments"));
    if (paymentRecords == null) {
        paymentRecords = new ArrayList();
    }
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (statementOfAccount == null) {
        response.sendRedirect("../PaymentManagement/statementOfAccounts.jsp?errMsg=An Error has occured.");
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
        <section class="body">
            <script>
                function back() {
                    window.onbeforeunload = null;
                    window.location.href = "../StatementOfAccountManagementController?target=ListAllSOA";
                }
                function viewSCO(id) {
                    window.location.href = "../OrderManagementController?previousManagementPage=soa&target=RetrieveSCO&id=" + id;
                }
                function viewInvoice(id) {
                    window.location.href = "../InvoiceManagementController?previousManagementPage=soa&target=RetrieveInvoice&id=" + id;
                }
                function viewOverdueInvoice(id) {
                    window.location.href = "../StatementOfAccountManagementController?target=ViewOverDueInvoiceTiedToCustomer&id=" + id;
                }
            </script>
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Statement of Account</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../StatementOfAccountManagementController?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span>SOA &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <div class="panel-body">
                            <div class="invoice">
                                <header class="clearfix">
                                    <div class="row">
                                        <div class="col-sm-6 mt-md">
                                            <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Statement of Account</h2>
                                            <h3><%=statementOfAccount.getCustomer().getCustomerName()%></h3>
                                            <br>
                                        </div>
                                    </div>
                                </header>

                                <div class="bill-info">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="bill-to">
                                                <!--<p class="h5 mb-xs text-dark text-weight-semibold">To:</p>-->
                                                <address>
                                                    <div class="col-md-6" style="padding-left: 0px;">
                                                    </div>
                                                    <br/><br/>
                                                </address>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="bill-data text-right">
                                                <p class="mb-none">
                                                    <span class="">Above 90 days</span>
                                                    <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                        <%
                                                            if (statementOfAccount != null && statementOfAccount.getAmountOverDueOver91Days() != null) {
                                                                out.print(formatter.format(statementOfAccount.getAmountOverDueOver91Days()));
                                                            }
                                                        %>
                                                    </span>
                                                </p>
                                                <p class="mb-none">
                                                    <span class="">61 to 90 days</span>
                                                    <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                        <%
                                                            if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom61to90Days() != null) {
                                                                out.print(formatter.format(statementOfAccount.getAmountOverDueFrom61to90Days()));
                                                            }
                                                        %>
                                                    </span>
                                                </p>
                                                <p class="mb-none">
                                                    <span class="">31 to 60 days</span>
                                                    <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                        <%
                                                            if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom31to60Days() != null) {
                                                                out.print(formatter.format(statementOfAccount.getAmountOverDueFrom31to60Days()));
                                                            }
                                                        %>
                                                    </span>
                                                </p>
                                                <p class="mb-none">
                                                    <span class="">0 to 30 days</span>
                                                    <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                        <%
                                                            if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom0to30Days() != null) {
                                                                out.print(formatter.format(statementOfAccount.getAmountOverDueFrom0to30Days()));
                                                            }
                                                        %>
                                                    </span>
                                                </p>
                                                <p class="mb-none">
                                                    <span class="value text-dark" style="min-width: 200px; font-size: 10.5pt; text-align: left; margin-top: 5px; margin-right: 30px;">
                                                        <button class="btn btn-default" onclick="javascript:viewOverdueInvoice(<%=statementOfAccount.getCustomer().getId()%>);">View Overdue Invoices</button>
                                                    </span>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="table-responsive">
                                    <table class="table invoice-items">
                                        <thead>
                                            <tr class="h4 text-dark">
                                                <th class="text-weight-semibold">Entry Date</th>
                                                <th class="text-weight-semibold">Type</th>
                                                <th class="text-weight-semibold">Ref. No</th>
                                                <th class="text-weight-semibold">Method</th>
                                                <th id="cell-price" class="text-weight-semibold">Debit</th>
                                                <th id="cell-price" class="text-weight-semibold">Credit</th>
                                                <th class="text-weight-semibold">Balance</th>
                                                <th class="text-weight-semibold">Due Date</th>
                                                <th class="text-weight-semibold" style="width: 240px; text-align: center">Details</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                List<SOALineItem> soali = statementOfAccount.getLineItem();
                                                if (soali != null) {
                                                    for (int i = 0; i < soali.size(); i++) {
                                            %>
                                            <tr>        
                                                <td>
                                                    <%
                                                        if (soali.get(i).getEntryDate() != null) {
                                                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                            String date = DATE_FORMAT.format(soali.get(i).getEntryDate());
                                                            out.print(date);
                                                        }
                                                    %>
                                                </td>
                                                <td><%=soali.get(i).getType()%></td>
                                                <td><%=soali.get(i).getReferenceNo()%></td>
                                                <td><%=soali.get(i).getMethod()%></td>
                                                <td><%=formatter.format(soali.get(i).getDebit())%></td>
                                                <td><%=formatter.format(soali.get(i).getCredit())%></td>
                                                <%if (i == soali.size() - 1) {%>
                                                <td><b><%=formatter.format(soali.get(i).getBalance())%></b></td>
                                                <%} else {%>
                                                <td><%=formatter.format(soali.get(i).getBalance())%></td>
                                                <%}%>
                                                <td>
                                                    <%
                                                        if (soali.get(i).getDueDate() != null) {
                                                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                            String date = DATE_FORMAT.format(soali.get(i).getDueDate());
                                                            out.print(date);
                                                        }
                                                    %>
                                                </td>
                                                <td>
                                                    <%if (soali.get(i).getScoID() != null) {%>
                                                    <button type="button" class="btn btn-default" onclick="javascript:viewSCO('<%=soali.get(i).getScoID()%>');">SCO</button>
                                                    <%}
                                                        if (soali.get(i).getInvoiceID() != null) {%>
                                                    <button type="button" class="btn btn-default" onclick="javascript:viewInvoice('<%=soali.get(i).getInvoiceID()%>');">Invoice</button>
                                                    <%}
                                                        if (soali.get(i).getPaymentID() != null) {%>
                                                    <button type="button" class="btn btn-default modal-with-form" href="#modalPayment<%=soali.get(i).getPaymentID()%>">Payment</button>
                                                    <%}%>
                                                </td>
                                            </tr>
                                            <%
                                                    }
                                                }
                                            %>

                                        </tbody>
                                    </table>
                                </div>


                                <div class="invoice-summary" style="margin-top: 10px;">
                                    <div class="row">
                                        <div class="col-sm-5"></div>
                                        <div class="col-sm-3"></div>
                                        <div class="col-sm-4">
                                            <table class="table h5 text-dark">
                                                <tbody>
                                                    <tr class="h4">
                                                        <td colspan="2">
                                                            Total 
                                                        </td>
                                                        <td class="text-left">
                                                            <%
                                                                if (statementOfAccount != null && statementOfAccount.getTotalAmountOverDue() != null) {
                                                                    out.print(formatter.format(statementOfAccount.getTotalAmountOverDue()));
                                                                }
                                                            %>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>


                                <br>

                                <div class="row">
                                    <div class="col-sm-6">
                                        <div class="btn-group">
                                            <%
                                                if (statementOfAccount != null) {
                                                    out.print("<a href='statementOfAccount-print.jsp' target='_blank' class='btn btn-default'><i class='fa fa-print'></i> Print PDF</a>");
                                                }
                                            %> 
                                        </div>
                                    </div>

                                    <div class="col-sm-6 text-right">
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    <%
                        for (PaymentRecord payment : paymentRecords) {
                    %>
                    <div id="modalPayment<%=payment.getId()%>" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <header class="panel-heading">
                                <h2 class="panel-title">View Payment Record</h2>
                            </header>
                            <div class="panel-body">

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">Amount</label>
                                    <div class="col-sm-9">
                                        <%=formatter.format(payment.getAmount())%>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">Payment Date</label>
                                    <div class="col-md-9">
                                        <%
                                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                            String date = DATE_FORMAT.format(payment.getPaymentDate());
                                            out.println(date);
                                        %>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">Method</label>
                                    <div class="col-sm-9">
                                        <%if (payment.getPaymentMethod() != null) {
                                                out.print(payment.getPaymentMethod());
                                            }%>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">Reference No.</label>
                                    <div class="col-sm-9">
                                        <%if (payment.getPaymentReferenceNumber() != null) {
                                                out.print(payment.getPaymentReferenceNumber());
                                            }%>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">Notes</label>
                                    <div class="col-sm-9">
                                        <%if (payment.getNotes() != null) {
                                                out.print(payment.getNotes());
                                            }%>
                                    </div>
                                </div>

                                <br>
                            </div>
                            <footer class="panel-footer">
                                <div class="row">
                                    <div class="col-md-12 text-right">
                                        <button class="btn btn-success" onclick="viewInvoice(<%=payment.getInvoice().getId()%>)">View Related Invoice</button>
                                        <button class="btn btn-default modal-dismiss">Close</button>
                                    </div>
                                </div>
                            </footer>
                            <%}%>
                        </section>
                    </div>
                    <!-- end: page -->
                </section>
            </div>
        </section>
        <jsp:include page="../jspIncludePages/foot.html" />
    </body>
</html>
<%}%>

