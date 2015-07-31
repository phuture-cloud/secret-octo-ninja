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
    Invoice invoice = (Invoice) (session.getAttribute("invoice"));
    List<PaymentRecord> paymentRecords = (List<PaymentRecord>) (session.getAttribute("paymentRecords"));
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
            function back(id) {
                window.location.href = "../InvoiceManagementController?target=RetrieveInvoice&id=" + id;
            }

            function deletePaymentRecord(id) {
                window.location.href = "../PaymentManagementController?target=DeletePaymentRecord&id=" + id;
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
                            <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                <thead>
                                    <tr>
                                        <th>Payment Date</th>
                                        <th>Payment Method</th>
                                        <th>Payment Reference Number</th>
                                        <th>Amount</th>
                                        <th>Notes</th>
                                        <th style="width: 400px;">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        for (int i = 0; i < paymentRecords.size(); i++) {
                                            System.out.print(i);
                                            PaymentRecord paymentRecord = paymentRecords.get(i);
                                            if (!paymentRecord.getIsDeleted()) {

                                    %>
                                    <tr>        
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
                                            <%                                                   
                                                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                out.print(formatter.format(paymentRecord.getAmount()));
                                            %>
                                        </td>
                                        <td>
                                            <a class="modal-with-move-anim btn btn-default btn-block" href="#modalNotes<%=paymentRecord.getId()%>">View</a>
                                            <div id="modalNotes<%=paymentRecord.getId()%>" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
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
                                            <div class="btn-group" role="group" aria-label="...">
                                                <button class="btn btn-default modal-with-form" href="#modalEditForm<%=paymentRecord.getId()%>">Edit Payment Record</button>
                                                <div id="modalEditForm<%=paymentRecord.getId()%>" class="modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <form name="editContactForm" action="../PaymentManagementController" class="form-horizontal mb-lg">
                                                            <header class="panel-heading">
                                                                <h2 class="panel-title">Edit Payment Record</h2>
                                                            </header>
                                                            <div class="panel-body">

                                                                <div class="form-group">
                                                                    <label class="col-sm-3 control-label">Amount <span class="required">*</span></label>
                                                                    <div class="col-sm-9">
                                                                        <input type="number" value="<%=paymentRecord.getAmount()%>" class="form-control" id="price" name="amount" min="0" step="0.01" size="4" title="CDA Currency Format - no dollar sign and no comma(s) - cents (.##) are optional" required/>
                                                                    </div>
                                                                </div>

                                                                <div class="form-group">
                                                                    <label class="col-sm-3 control-label">Payment Date <span class="required">*</span></label>
                                                                    <div class="col-md-9">
                                                                        <%
                                                                            DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
                                                                            date = DATE_FORMAT.format(paymentRecord.getPaymentDate());
                                                                        %>
                                                                        <input type="text" value="<%=date%>"name="paymentDate" data-plugin-datepicker data-date-format="dd/mm/yyyy" class="form-control" placeholder="dd/mm/yyyy" required/>
                                                                    </div>
                                                                </div>

                                                                <div class="form-group">
                                                                    <label class="col-sm-3 control-label">Payment Method</label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text"value="<%if (paymentRecord.getPaymentMethod() != null) {
                                                                                out.print(paymentRecord.getPaymentMethod());
                                                                            }%>" name="paymentMethod" class="form-control"/>
                                                                    </div>
                                                                </div>

                                                                <div class="form-group">
                                                                    <label class="col-sm-3 control-label">Payment Reference Number</label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" value="<%if (paymentRecord.getPaymentReferenceNumber() != null) {
                                                                                out.print(paymentRecord.getPaymentReferenceNumber());
                                                                            }%>" name="paymentReferenceNumber" class="form-control"/>
                                                                    </div>
                                                                </div>

                                                                <div class="form-group">
                                                                    <label class="col-sm-3 control-label">Notes</label>
                                                                    <div class="col-sm-9">
                                                                        <textarea class="form-control" rows="5" name="notes"><%if (paymentRecord.getNotes() != null) {
                                                                                out.print(paymentRecord.getNotes());
                                                                            }%></textarea>
                                                                    </div>
                                                                </div>

                                                                <br>
                                                                <input type="hidden" name="target" value="UpdatePaymentRecord">    
                                                                <input type="hidden" name="id" value="<%=paymentRecord.getId()%>">  
                                                            </div>
                                                            <footer class="panel-footer">
                                                                <div class="row">
                                                                    <div class="col-md-12 text-right">
                                                                        <button class="btn btn-success" type="submit">Save Changes</button>
                                                                        <button class="btn btn-default modal-dismiss">Cancel</button>
                                                                    </div>
                                                                </div>
                                                            </footer>
                                                        </form>
                                                    </section>
                                                </div>

                                                <button class="modal-with-move-anim btn btn-default" href="#modalRemove<%=paymentRecord.getId()%>">Remove</button>
                                                <div id="modalRemove<%=paymentRecord.getId()%>" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <header class="panel-heading">
                                                            <h2 class="panel-title">Are you sure?</h2>
                                                        </header>
                                                        <div class="panel-body" style="padding-top: 0px;">
                                                            <div class="modal-wrapper">
                                                                <div class="modal-icon">
                                                                    <i class="fa fa-question-circle" style="top: 0px;"></i>
                                                                </div>
                                                                <div class="modal-text">
                                                                    <p>Are you sure that you want to delete this Payment?<br> All associated records will also be deleted together!</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <footer class="panel-footer">
                                                            <div class="row">
                                                                <div class="col-md-12 text-right">
                                                                    <button class="btn btn-primary modal-confirm" onclick="javascript:deletePaymentRecord(<%=paymentRecord.getId()%>);">Confirm</button>
                                                                    <button class="btn btn-default modal-dismiss">Cancel</button>
                                                                </div>
                                                            </div>
                                                        </footer>
                                                    </section>
                                                </div>
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
                            <button type="button" class="btn btn-default" onclick="javascript:back(<%=invoice.getId()%>);">Back</button>  
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