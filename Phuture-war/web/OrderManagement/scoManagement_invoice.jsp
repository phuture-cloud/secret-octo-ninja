<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Customer"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (sco == null) {
        response.sendRedirect("scoManagement.jsp?errMsg=An Error has occured.");
    } else {
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script>
            function viewInvoice(id) {

            }

            function back(id) {
                window.location.href = "../OrderManagementController?target=RetrieveSCO&id=" + id;
            }
        </script>

        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>SCO #<%=sco.getSalesConfirmationOrderNumber()%> - Invoices</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Invoices &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">SCO #<%=sco.getSalesConfirmationOrderNumber()%> - Invoices</h2>
                        </header>
                        <div class="panel-body">
                            <form name="scoManagement_invoice">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Invoice #</th>
                                            <th>Invoice Date</th>
                                            <th>Invoice Status</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            System.out.println("<<<<<<<<<<<<<<<<<<< " + sco.getInvoices() );
                                            if (sco != null && sco.getInvoices() != null) {
                                                for (int i = 0; i < sco.getInvoices().size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=sco.getInvoices().get(i).getInvoiceNumber()%></td>
                                            <td><%=sco.getInvoices().get(i).getDateCreated()%></td>
                                            <td><%=sco.getInvoices().get(i).getStatus()%></td>
                                            <td>
                                                <div class="btn-group" role="group" aria-label="...">
                                                    <button  class="btn btn-default" onclick="javascript:viewInvoice('<%=sco.getInvoices().get(i).getId()%>')">View</button>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>
                                <div class="col-sm-12 text-right mt-md mb-md">
                                    <button type="button" class="btn btn-default" onclick="javascript:back(<%=sco.getId()%>)">Back</button>   
                                </div>
                            </form>
                        </div>

                    </section>
                    <!-- end: page -->
                </section>
            </div>
        </section>
        <jsp:include page="../foot.html" />
    </body>
</html>
<%
    }
%>