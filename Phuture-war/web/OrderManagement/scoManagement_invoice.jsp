<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Customer"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    DeliveryOrder deliveryOrder = (DeliveryOrder) (session.getAttribute("do"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (sco == null || deliveryOrder == null) {
        response.sendRedirect("scoManagement.jsp?errMsg=An Error has occured.");
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

            function back(id) {
                window.location.href = "../DeliveryOrderManagementController?target=RetrieveDO&id=" + id;
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
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
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>">SCO No. <%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                <li><span><a href= "scoManagement_DO.jsp">DOs</a></span></li>
                                <li><span><a href= "doManagement.jsp">DO No. <%=deliveryOrder.getDeliveryOrderNumber()%> </a></span></li>
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
                                            if (sco != null) {
                                                for (int i = 0; i < sco.getInvoices().size(); i++) {
                                                    if (!sco.getInvoices().get(i).getIsDeleted()) {
                                        %>
                                        <tr>        
                                            <td><%=sco.getInvoices().get(i).getInvoiceNumber()%></td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy hh:mm:ss");
                                                    String date = DATE_FORMAT.format(sco.getInvoices().get(i).getDateCreated());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td><%=sco.getInvoices().get(i).getStatus()%></td>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewInvoice('<%=sco.getInvoices().get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%
                                                    }
                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>
                                <br>
                                <button type="button" class="btn btn-default" onclick="javascript:back(<%=deliveryOrder.getId()%>);">Back</button>   
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