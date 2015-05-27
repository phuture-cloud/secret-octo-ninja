<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<SalesConfirmationOrder> salesConfirmationOrders = (List<SalesConfirmationOrder>) (session.getAttribute("salesConfirmationOrders"));
    Staff staff = (Staff) (session.getAttribute("staff"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
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
            $(document).ready(function () {
                $("#btn1").click(function () {
                    $("#lol").append(" <b>Appended text</b>.");
                });
            });

            function createSCO() {
                window.event.returnValue = true;
                document.scoManagement.action = "staffManagement_add.jsp";
                document.scoManagement.submit();
            }
        </script>

        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Sales Confirmation Order Management</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Sales Confirmation Order Management &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Sales Confirmation Order Management</h2>
                        </header>
                        <div class="panel-body">

                            <div class="row">
                                <div class="col-md-12"> 
                                    <button class="btn btn-primary" onclick="createSCO()">Create Sales Confirmation Order</button>
                                </div>
                            </div>
                            <br/>

                            <form name="scoManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr> 
                                            <th>SCO Order #</th>
                                            <th>Customer</th>
                                            <th>Date</th>
                                            <th>Total Amount</th>
                                            <th>Status</th>
                                            <th style="width: 300px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>

                                        <%
                                            if (salesConfirmationOrders != null && salesConfirmationOrders.size() > 0) {
                                                for (int i = 0; i < salesConfirmationOrders.size(); i++) {
                                        %>
                                        <tr>
                                            <td><%=salesConfirmationOrders.get(i).getId()%></td>
                                            <td><%=salesConfirmationOrders.get(i).getCustomerName()%></td>
                                            <td><%=salesConfirmationOrders.get(i).getDateCreated()%></td>
                                            <td><%=salesConfirmationOrders.get(i).getStatus()%></td>


                                            <td><input type="button" class="btn btn-default btn-block" value="View" onclick="javascript:viewSCO'<%=salesConfirmationOrders.get(i).getId()%>')"/></td>

                                        </tr>
                                        <%
                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>

                                <input type="hidden" name="id" value="">
                                <input type="hidden" name="target" value="">    
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