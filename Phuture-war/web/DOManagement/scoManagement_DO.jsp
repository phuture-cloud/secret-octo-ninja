<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="EntityManager.DeliveryOrder"%>
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
        if (sco == null) {
            response.sendRedirect("../workspace.jsp?errMsg=An Error has occured.");
        }
    } else if (previousMgtPage.equals("soa")) {
        soa = (StatementOfAccount) (session.getAttribute("statementOfAccount"));
    }

    List<DeliveryOrder> deliveryOrders = (List<DeliveryOrder>) (session.getAttribute("listOfDO"));
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
            function viewDO(id) {
                window.location.href = "../DeliveryOrderManagementController?target=RetrieveDO&id=" + id;
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
                        <h2>Delivery Orders</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <%if (previousMgtPage.equals("sco")) {%>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=sco.getId()%>"><%=sco.getSalesConfirmationOrderNumber()%></a></span></li>
                                            <%} else if (previousMgtPage.equals("soa")) {%>
                                            <%}%>

                                <li><span>Delivery Orders &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <header class="panel-heading">
                            <%if (previousMgtPage.equals("sco")) {%>
                            <h2 class="panel-title">SCO No.<%=sco.getSalesConfirmationOrderNumber()%> - Delivery Orders</h2>
                            <%} else {%>
                            <h2 class="panel-title">Delivery Orders</h2>
                            <%}%>
                        </header>
                        <div class="panel-body">
                            <form name="scoManagement_DO">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Delivery No.</th>
                                            <th>Delivery Date</th>
                                            <th>Delivery Status</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (int i = 0; i < deliveryOrders.size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=deliveryOrders.get(i).getDeliveryOrderNumber()%></td>
                                            <td>
                                                <%
                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                    String date = DATE_FORMAT.format(deliveryOrders.get(i).getDeliveryOrderDate());
                                                    out.print(date);
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    if (deliveryOrders.get(i).getStatus().equals("Created")) {
                                                        out.print("<td>Created</td>");
                                                    } else if (deliveryOrders.get(i).getStatus().equals("Shipped")) {
                                                        out.print("<td class='info'>Shipped</td>");
                                                    } else if (deliveryOrders.get(i).getStatus().equals("Delivered")) {
                                                        out.print("<td class='success'>Delivered</td>");
                                                    }
                                                %>
                                            </td>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewDO('<%=deliveryOrders.get(i).getId()%>')">View</button></td>
                                        </tr>
                                        <%
                                            }
                                        %>

                                    </tbody>
                                </table>

                                <br>
                                <%if (!previousMgtPage.equals("deliveryOrders")) {%>
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