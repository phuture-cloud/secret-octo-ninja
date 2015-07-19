<%@page import="EntityManager.PurchaseOrder"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Customer"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>) (session.getAttribute("listOfPO"));
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

            function back(id) {
                window.location.href = "../OrderManagementController?target=RetrieveSCO&id=" + id;
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Purchase Orders</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">PO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>"><%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                <li><span>POs &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">SCO No. <%=sco.getSalesConfirmationOrderNumber()%> - Purchase Orders</h2>
                        </header>
                        <div class="panel-body">
                            <form name="scoManagement_PO">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Purchase Order No.</th>
                                            <th>Purchase Date</th>
                                            <th>Purchase Status</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            if (purchaseOrders != null) {
                                                for (int i = 0; i < purchaseOrders.size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=purchaseOrders.get(i).getPurchaseOrderNumber()%></td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy hh:mm:ss");
                                                    String date = DATE_FORMAT.format(purchaseOrders.get(i).getPurchaseOrderDate());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td><%=purchaseOrders.get(i).getStatus()%></td>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewPO('<%=purchaseOrders.get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%

                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>

                                <br>
                                <button type="button" class="btn btn-default" onclick="javascript:back(<%=sco.getId()%>)">Back</button>   

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