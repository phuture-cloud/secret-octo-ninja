<%@page import="java.text.NumberFormat"%>
<%@page import="EntityManager.CreditNote"%>
<%@page import="EntityManager.StatementOfAccount"%>
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
    if (previousMgtPage.equals("sco")) {
        sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    }

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
            function viewPO(id) {
                window.location.href = "../PurchaseOrderManagementController?target=RetrievePO&id=" + id;
            }

            function back() {
            <% if (previousMgtPage.equals("sco")) {%>
                window.location.href = "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>";
            <% } else if (previousMgtPage.equals("soa")) {%>
                window.location.href = "todo";
            <%}%>
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
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <% if (previousMgtPage.equals("sco")) {%>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>"><%=staff.getStaffPrefix()%>-<%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                            <%
                                            } else if (previousMgtPage.equals("soa")) {

                                            %>
                                            <%}%>
                                <li><span>Credit Notes &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <%
                                if (previousMgtPage.equals("sco")) {
                            %>
                            <h2 class="panel-title">SCO No. <%=staff.getStaffPrefix()%>-<%=sco.getSalesConfirmationOrderNumber()%> - Credit Notes</h2>
                            <%} else {%>
                            <h2 class="panel-title">Purchase Orders</h2>
                            <%}%>
                        </header>
                        <div class="panel-body">
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
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewPO('<%=creditNotes.get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>
                                    </tbody>
                                </table>

                                <br>
                                <%if (!previousMgtPage.equals("customerManagement")) {%>
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