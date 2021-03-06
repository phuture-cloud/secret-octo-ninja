<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="EntityManager.PurchaseOrder"%>
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
    if (previousMgtPage.equals("sco")) {
        sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    } else if (previousMgtPage.equals("soa")) {
        soa = (StatementOfAccount) (session.getAttribute("statementOfAccount"));
    }

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
            $(document).ready(function () {
                $('#datatable-default').DataTable({
                    "order": [[1, "desc"]]
                });
            });
            
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
                        <h2>Purchase Orders</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <% if (previousMgtPage.equals("sco")) {%>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>"><%=sco.getSalesPerson().getStaffPrefix()%><%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                            <%}%>
                                <li><span>Purchase Orders &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <%
                                if (previousMgtPage.equals("sco")) {
                            %>
                            <h2 class="panel-title"><%=sco.getSalesPerson().getStaffPrefix()%><%=sco.getSalesConfirmationOrderNumber()%> - Purchase Orders</h2>
                            <%} else {%>
                            <h2 class="panel-title">Purchase Orders</h2>
                            <%}%>
                        </header>
                        <div class="panel-body">
                            <form name="scoManagement_PO">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Purchase Order No.</th>
                                            <th>Purchase Date</th>
                                            <th>Purchase Status</th>
                                            <th style="text-align: center;">Action</th>
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
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                                                    String date = DATE_FORMAT.format(purchaseOrders.get(i).getPurchaseOrderDate());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <%
                                                if (purchaseOrders.get(i).getStatus().equals("Pending")) {
                                                    out.print("<td class='info'>Pending</td>");
                                                } else if (purchaseOrders.get(i).getStatus().equals("Completed")) {
                                                    out.print("<td class='success'>Completed</td>");
                                                } else {
                                                    out.print("<td>" + purchaseOrders.get(i).getStatus() + "</td>");
                                                }
                                            %>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewPO('<%=purchaseOrders.get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>
                                    </tbody>
                                </table>

                                <br>
                                <%if (!previousMgtPage.equals("purchaseOrders")) {%>
                                <div class="col-sm-12 text-right" style="padding-right: 0px;">
                                    <button type="button" style="min-width: 100px;" class="btn btn-default" onclick="javascript:back()">Back</button>   
                                </div>
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