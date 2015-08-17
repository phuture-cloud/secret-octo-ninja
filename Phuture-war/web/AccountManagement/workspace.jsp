<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="EntityManager.Invoice"%>
<%@page import="EntityManager.SalesConfirmationOrderHelper"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else {
        List<SalesConfirmationOrderHelper> salesConfirmationOrders = (List<SalesConfirmationOrderHelper>) (session.getAttribute("salesConfirmationOrders"));
        List<Invoice> invoices = (List<Invoice>) (session.getAttribute("listOfInvoice"));
        List<DeliveryOrder> deliveryOrders = (List<DeliveryOrder>) (session.getAttribute("listOfDO"));
        int scoCompleted = 0;
        int scoUnfulfilled = 0;
        int scoWriteOff = 0;
        int scoVoided = 0;
        int invoiceSent = 0;
        int invoicePaid = 0;
        int invoiceVoided = 0;
        int doCreated = 0;
        int doShipped = 0;
        int doVoided = 0;

        for (SalesConfirmationOrderHelper salesConfirmationOrderHelper : salesConfirmationOrders) {
            SalesConfirmationOrder sco = salesConfirmationOrderHelper.getSco();
            String status = sco.getStatus();
            if (status.equals("Completed")) {
                scoCompleted++;
            } else if (status.equals("Unfulfilled")) {
                scoUnfulfilled++;
            } else if (status.equals("Write-Off")) {
                scoWriteOff++;
            } else if (status.equals("Voided")) {
                scoVoided++;
            }
        }

        for (Invoice invoice : invoices) {
            String status = invoice.getStatus();
            if (status.equals("Sent")) {
                invoiceSent++;
            } else if (status.equals("Paid")) {
                invoicePaid++;
            } else if (status.equals("Voided")) {
                invoiceVoided++;
            }
        }

        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            String status = deliveryOrder.getStatus();
            if (status.equals("Created")) {
                doCreated++;
            } else if (status.equals("Shipped")) {
                doShipped++;
            } else if (status.equals("Voided")) {
                doVoided++;
            }
        }
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <jsp:include page="../jspIncludePages/header.jsp" />
        <div class="inner-wrapper">
            <jsp:include page="../jspIncludePages/sidebar.jsp" />
            <section role="main" class="content-body">
                <header class="page-header">
                    <h2>Workspace</h2>
                    <div class="right-wrapper pull-right">
                        <ol class="breadcrumbs">
                            <li><i class="fa fa-home"></i></li>
                            <li><span>Workspace &nbsp;&nbsp</span></li>
                        </ol>
                    </div>
                </header>

                <!-- start: page -->

                <h3>Welcome back <b><%=staff.getName()%></b></h3>

                <br>

                <div class="row">
                    <div class="col-md-6 col-lg-6 col-xl-4">
                        <section class="panel panel-featured-left panel-featured-primary">
                            <div class="panel-body">
                                <div class="widget-summary">
                                    <div class="widget-summary-col widget-summary-col-icon">
                                        <div class="summary-icon bg-primary">
                                            <i class="fa fa-file-text"></i>
                                        </div>
                                    </div>
                                    <div class="widget-summary-col">
                                        <div class="summary">
                                            <h4 class="title">Total Sales Confirmation Order</h4>
                                            <div class="info">
                                                <strong class="amount"><%=salesConfirmationOrders.size()%></strong>
                                            </div>
                                        </div>
                                        <div class="summary-footer">
                                            <a class="text-muted text-uppercase" href="../OrderManagementController?target=ListAllSCO">(view all)</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </div>

                    <div class="col-md-6 col-lg-6 col-xl-4">
                        <section class="panel panel-featured-left panel-featured-secondary">
                            <div class="panel-body">
                                <div class="widget-summary">
                                    <div class="widget-summary-col widget-summary-col-icon">
                                        <div class="summary-icon bg-secondary">
                                            <i class="fa fa-usd"></i>
                                        </div>
                                    </div>
                                    <div class="widget-summary-col">
                                        <div class="summary">
                                            <h4 class="title">Total Invoices</h4>
                                            <div class="info">
                                                <strong class="amount"><%=invoices.size()%></strong>
                                            </div>
                                        </div>
                                        <div class="summary-footer">
                                            <a class="text-muted text-uppercase" href="../InvoiceManagementController?target=ListAllInvoice">(view all)</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </div>

                    <div class="col-md-6 col-lg-6 col-xl-4">
                        <section class="panel panel-featured-left panel-featured-tertiary">
                            <div class="panel-body">
                                <div class="widget-summary">
                                    <div class="widget-summary-col widget-summary-col-icon">
                                        <div class="summary-icon bg-tertiary">
                                            <i class="fa fa-truck"></i>
                                        </div>
                                    </div>
                                    <div class="widget-summary-col">
                                        <div class="summary">
                                            <h4 class="title">Total Delivery Orders</h4>
                                            <div class="info">
                                                <strong class="amount"><%=deliveryOrders.size()%></strong>
                                            </div>
                                        </div>
                                        <div class="summary-footer">
                                            <a class="text-muted text-uppercase" href="../DeliveryOrderManagementController?target=ListAllDO">(view all)</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4">
                        <section class="panel panel-primary">
                            <header class="panel-heading">
                                <div class="panel-actions">
                                    <a href="#" class="panel-action panel-action-toggle" data-panel-toggle></a>
                                    <a href="#" class="panel-action panel-action-dismiss" data-panel-dismiss></a>
                                </div>
                                <h2 class="panel-title">Sales Order Confirmation</h2>
                            </header>
                            <div class="panel-body">
                                <div id="scoPieChart" style="height: 300px;"></div>
                            </div>
                        </section>
                    </div>

                    <div class="col-md-4">
                        <section class="panel panel-secondary">
                            <header class="panel-heading">
                                <div class="panel-actions">
                                    <a href="#" class="panel-action panel-action-toggle" data-panel-toggle></a>
                                    <a href="#" class="panel-action panel-action-dismiss" data-panel-dismiss></a>
                                </div>
                                <h2 class="panel-title">Invoices</h2>
                            </header>
                            <div class="panel-body">
                                <div id="invoicePieChart" style="height: 300px;"></div>
                            </div>
                        </section>
                    </div>

                    <div class="col-md-4">
                        <section class="panel panel-tertiary">
                            <header class="panel-heading">
                                <div class="panel-actions">
                                    <a href="#" class="panel-action panel-action-toggle" data-panel-toggle></a>
                                    <a href="#" class="panel-action panel-action-dismiss" data-panel-dismiss></a>
                                </div>
                                <h2 class="panel-title">Delivery Orders</h2>
                            </header>
                            <div class="panel-body">
                                <div id="doPieChart" style="height: 300px;"></div>
                            </div>
                        </section>
                    </div>
                </div>

                <!-- end: page -->
            </section>
        </div>

        <jsp:include page="../jspIncludePages/foot.html" />
        <script src="../assets/javascripts/ui-elements/examples.charts.js"></script>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript">
        google.load("visualization", "1", {packages: ["corechart"]});
        google.setOnLoadCallback(drawSCOChart);
        function drawSCOChart() {
            var data = google.visualization.arrayToDataTable([
                ['Status', 'Orders'],
                ['Completed', <%=scoCompleted%>],
                ['Unfufilled', <%=scoUnfulfilled%>],
                ['Write-Off', <%=scoWriteOff%>],
                ['Voided', <%=scoVoided%>]
            ]);

            var options = {
            };

            var chart = new google.visualization.PieChart(document.getElementById('scoPieChart'));
            chart.draw(data, options);
        }

        google.load("visualization", "1", {packages: ["corechart"]});
        google.setOnLoadCallback(drawInvoiceChart);
        function drawInvoiceChart() {
            var data = google.visualization.arrayToDataTable([
                ['Status', 'Orders'],
                ['Sent', <%=invoiceSent%>],
                ['Paid', <%=invoicePaid%>],
                ['Voided', <%=invoiceVoided%>]
            ]);

            var options = {
            };

            var chart = new google.visualization.PieChart(document.getElementById('invoicePieChart'));
            chart.draw(data, options);
        }

        google.load("visualization", "1", {packages: ["corechart"]});
        google.setOnLoadCallback(drawDOChart);
        function drawDOChart() {
            var data = google.visualization.arrayToDataTable([
                ['Status', 'Orders'],
                ['Created', <%=doCreated%>],
                ['Shipped', <%=doShipped%>],
                ['Void', <%=doVoided%>]
            ]);

            var options = {
            };

            var chart = new google.visualization.PieChart(document.getElementById('doPieChart'));
            chart.draw(data, options);
        }
        </script>
    </body>
</html>
<%}%>

