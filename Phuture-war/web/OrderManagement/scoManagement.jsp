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
        </script>

        <section class="body">
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>SCO Management</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>SCO Management</span></li>
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
                                <div class="col-md-6">
                                    <section class="panel">
                                        <button id="btn1">Test</button>
                                        <div id="lol" class="panel-body" style="min-height: 150px;">
                                            <p>This is an initial content. Click on refresh icon to append content.</p>
                                        </div>
                                    </section>
                                </div>
                            </div>



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