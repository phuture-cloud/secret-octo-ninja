<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="head.html" />
    </head>
    <body>
        <section class="body">

            <jsp:include page="header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="sidebar.jsp" />

                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Dashboard</h2>

                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="index.html">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Dashboard &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <!-- end: page -->
                </section>
            </div>

            <jsp:include page="foot.html" />
        </section>

    </body>
</html>