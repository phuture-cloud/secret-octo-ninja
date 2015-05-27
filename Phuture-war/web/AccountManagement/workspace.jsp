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
            <jsp:include page="../header.jsp" />
            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
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

                    <!-- end: page -->
                </section>
            </div>

            <jsp:include page="../foot.html" />
        </section>

    </body>
</html>
<%
    }
%>