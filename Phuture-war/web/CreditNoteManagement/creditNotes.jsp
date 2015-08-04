<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.CreditNote"%>
<%@page import="EntityManager.Customer"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));

    List<CreditNote> creditNotes = (List<CreditNote>) (session.getAttribute("listOfCreditNotes"));
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
        <script>
            function viewCreditNote(id) {
                window.location.href = "../PurchaseOrderManagementController?target=RetrievePO&id=" + id;
            }

            function back() {
                window.location.href = "../CustomerManagementController?target=ListAllCustomer";
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Credit Notes</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li><a href="../AccountManagement/workspace.jsp"><i class="fa fa-home"></i></a></li>
                                <li><span><a href= "../CustomerManagementController?target=ListAllCustomer">Customer Management</a></span></li>
                                <li><span>Credit Notes &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Credit Notes</h2>
                        </header>
                        <div class="panel-body">
                            <button type='button' class='btn btn-primary modal-with-form' href='#modalAddPayment'>Add Payment</button>
                            <br>

                            <form name="creditNotesForm">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Credit Note No.</th>
                                            <th>Credit Note Date</th>
                                            <th>Amount</th>
                                            <th>Applied To</th>
                                            <th>Date Applied</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            if (creditNotes != null) {
                                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy hh:mm:ss");
                                                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                for (int i = 0; i < creditNotes.size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=creditNotes.get(i).getCreditNoteNumber()%></td>
                                            <td>
                                                <%=DATE_FORMAT.format(creditNotes.get(i).getDateIssued())%>
                                            </td>
                                            <td><%=formatter.format(creditNotes.get(i).getCreditNoteNumber())%></td>
                                            <td>
                                                <%
                                                    if (creditNotes.get(i).getAppliedToInvoice().getInvoiceNumber() != null) {
                                                        out.print(creditNotes.get(i).getAppliedToInvoice().getInvoiceNumber());
                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                    if (creditNotes.get(i).getDateUsed() != null) {
                                                        out.print(creditNotes.get(i).getDateUsed());
                                                    }
                                                %>
                                            </td>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewCreditNote('<%=creditNotes.get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>
                                    </tbody>
                                </table>
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
<%}%>

