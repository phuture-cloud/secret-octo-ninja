<%@page import="java.util.Date"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="EntityManager.Invoice"%>
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
            <% if (previousMgtPage.equals("sco")) {%>
            window.location.href = "../InvoiceManagementController?target=RefreshSCOInvoices";
            <%} else if (previousMgtPage.equals("invoices") || previousMgtPage.equals("soa")) {%>
            <%if (request.getParameter("show") != null && request.getParameter("show").equals("overdue")) {%>
            window.location.href = "../StatementOfAccountManagementController?target=ViewOverDueInvoiceTiedToCustomer";
            <%} else {%>
            window.location.href = "../InvoiceManagementController?target=RefreshInvoices";
            <%}
                }%>
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
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>"><%=sco.getSalesPerson().getStaffPrefix()%><%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                            <%} else if (previousMgtPage.equals("soa")) {%>
                                <li><span><a href= "../StatementOfAccountManagementController?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span><a href="../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=soa.getCustomer().getId()%>"><%=soa.getCustomer().getCustomerName()%></a></span></li>
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
                            <h2 class="panel-title"><%=sco.getSalesPerson().getStaffPrefix()%><%=sco.getSalesConfirmationOrderNumber()%> - Invoices</h2>
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
                                            <th>Invoice #</th>
                                            <th>Invoice Date</th>
                                            <th>Customer</th>
                                            <th>Invoiced Amount</th>
                                            <th>Amount Paid</th>
                                            <th>Credits Applied</th>
                                            <th></th>
                                            <th>Due Date</th>
                                            <th>Invoice Status</th>
                                            <th style="text-align: center;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (int i = 0; i < invoices.size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=invoices.get(i).getInvoiceNumber()%></td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                                                    String date = DATE_FORMAT.format(invoices.get(i).getDateSent());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td><%=invoices.get(i).getCustomerName()%></td>
                                            <td><%=formatter.format(invoices.get(i).getTotalPriceBeforeCreditNote())%></td>
                                            <td>
                                                <%
                                                    if (invoices.get(i).getTotalAmountPaid() != null) {
                                                        out.print(formatter.format(invoices.get(i).getTotalAmountPaid()));
                                                    } else {
                                                        out.println("NA");
                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    if (invoices.get(i).getTotalCreditNoteAmount() != null) {
                                                        out.print(formatter.format(invoices.get(i).getTotalCreditNoteAmount()));
                                                    } else {
                                                        out.println("NA");
                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <% if (invoices.get(i).getTotalAmountPaid() == null || invoices.get(i).getTotalCreditNoteAmount() == null || invoices.get(i).getTotalPriceAfterCreditNote() == null || invoices.get(i).getTotalPriceBeforeCreditNote() == null) {
                                                        //If there was some errors calculating the payment amount
                                                        out.println("<i class='fa fa-exclamation-triangle' style='color:yellow' data-toggle='tooltip' data-placement='top' title='Unable to calculate payment amount'></i>");
                                                    } else if (invoices.get(i).getTotalAmountPaid() == 0 &&  Math.round(invoices.get(i).getTotalCreditNoteAmount() * 100.0) / 100.0  > Math.round(invoices.get(i).getTotalPriceBeforeCreditNote() * 100.0) / 100.0) {
                                                        //Invoice fully paid using credit note and credit note is over invoice amount
                                                        //do nothing
                                                    } else if ( Math.round(invoices.get(i).getTotalAmountPaid() * 100.0) / 100.0 < Math.round(invoices.get(i).getTotalPriceAfterCreditNote() * 100.0) / 100.0) {
                                                        System.out.print(">>>>>>>>>>>>>>>" + invoices.get(i).getTotalPriceAfterCreditNote());
                                                        //If total paid less than invoiced amount
                                                        out.println("<i class='fa fa-exclamation-circle' style='color:red' data-toggle='tooltip' data-placement='top' title='Payment not fully received'></i>");
                                                    } else if (Math.round((invoices.get(i).getTotalCreditNoteAmount() + invoices.get(i).getTotalAmountPaid()) * 100.0) / 100.0  > Math.round(invoices.get(i).getTotalPriceBeforeCreditNote()*100.0)/100.0) {
                                                        //If total paid more than invoiced amount
                                                        out.println("<i class='fa fa-exclamation-circle' style='color:orange' data-toggle='tooltip' data-placement='top' title='Invoice overpaid'></i>");
                                                    } else {
                                                        //If total paid equal invoiced
                                                    }%>
                                            </td>
                                            <td>
                                                <%
                                                    Date dueDate = invoices.get(i).getDateDue();
                                                    if (dueDate != null) {
                                                        date = DATE_FORMAT.format(dueDate);
                                                        out.print(date);
                                                    }
                                                %>
                                            </td>
                                            <td><%=invoices.get(i).getStatus()%></td>
                                            <td>
                                                <button type="button" class="btn btn-default btn-block" onclick="javascript:viewInvoice('<%=invoices.get(i).getId()%>')">View</button>
                                            </td>
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