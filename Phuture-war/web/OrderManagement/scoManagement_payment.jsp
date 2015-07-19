<%@page import="java.text.NumberFormat"%>
<%@page import="EntityManager.PaymentRecord"%>
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
    Invoice invoice = (Invoice) (session.getAttribute("Invoice"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (invoice == null) {
        response.sendRedirect("invoiceManagement.jsp?errMsg=An Error has occured.");
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
            function viewInvoice(id) {
                window.location.href = "../InvoiceManagementController?target=RetrieveInvoice&id=" + id;
            }

            function back() {
                window.location.href = "invoiceManagement.jsp";
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Invoice #<%=invoice.getInvoiceNumber()%> - Payments</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=invoice.getSalesConfirmationOrder().getId()%>"><%=invoice.getSalesConfirmationOrder().getSalesConfirmationOrderNumber()%></a></span></li>
                                <li><span><a href= "invoiceManagement.jsp"><%=invoice.getInvoiceNumber()%></a></span></li>
                                <li><span>Invoices &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Invoice #<%=invoice.getInvoiceNumber()%> - Payments</h2>
                        </header>
                        <div class="panel-body">
                            <form name="scoManagement_payment">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Amount</th>
                                            <th>Payment Date</th>
                                            <th>Payment Method</th>
                                            <th>Payment Reference Number</th>
                                            <th>Notes</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            List<PaymentRecord> paymentRecords = invoice.getPaymentRecords();
                                            for (int i = 0; i < paymentRecords.size(); i++) {
                                                PaymentRecord paymentRecord = paymentRecords.get(i);
                                                if (!paymentRecord.getIsDeleted()) {

                                        %>
                                        <tr>        
                                            <td>
                                                <%                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                    out.print(formatter.format(paymentRecord.getAmount()));
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                    String date = DATE_FORMAT.format(paymentRecord.getPaymentDate());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td><%=paymentRecord.getPaymentMethod()%></td>
                                            <td><%=paymentRecord.getPaymentReferenceNumber()%></td>
                                            <td>
                                                <a class="modal-with-move-anim btn btn-default btn-block" href="#modalNotes">View</a>

                                                <div id="modalNotes" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <header class="panel-heading">
                                                            <h2 class="panel-title">Notes</h2>
                                                        </header>
                                                        <div class="panel-body">
                                                            <div class="modal-wrapper">
                                                                <div class="modal-text" style="height: 350px;">
                                                                    <textarea style="height:100%; width: 100%;"><%=paymentRecord.getNotes()%></textarea>
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
                                                <div class="btn-group"
                                                     <input type="button" class="btn btn-default btn-block" value="Update" onclick="javascript:updatePaymentRecord()"/>
                                                    <input type="button" class="btn btn-danger btn-block" value="Delete" onclick="javascript:updatePaymentRecord()"/>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>
                                <br>
                                <button type="button" class="btn btn-default" onclick="javascript:back();">Back</button>  
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