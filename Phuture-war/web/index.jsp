<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html class="fixed">
    <head>
        <link href="http://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800|Shadows+Into+Light" rel="stylesheet" type="text/css">
        <link rel="stylesheet" href="assets/vendor/bootstrap/css/bootstrap.css" />
        <link rel="stylesheet" href="assets/vendor/font-awesome/css/font-awesome.css" />
        <link rel="stylesheet" href="assets/stylesheets/theme.css" />
        <link rel="stylesheet" href="assets/stylesheets/skins/default.css" />
        <link rel="stylesheet" href="assets/vendor/pnotify/pnotify.custom.css" />
        <script src="assets/vendor/modernizr/modernizr.js"></script>
    </head>

    <body onload="alertFunc()">
        <script>
            <%
                String test = request.getParameter("test");
                if (test != null) {
            %>
            function alertFunc() {
                alert('yo');
                new PNotify({
                    title: 'Regular Notice',
                    text: 'Check me out! I\'m a notice.',
                    type: 'custom',
                    addclass: 'notification-primary',
                    icon: 'fa fa-twitter'
                });
            }
            <%
                }
            %>
        </script>
        <!-- start: page -->
        <section class="body-sign">
            <div class="center-sign">
                <a href="/" class="logo pull-left">
                    <img src="assets/images/logo.png" height="54" alt="Porto Admin" />
                </a>
                <div class="panel panel-sign">
                    <div class="panel-title-sign mt-xl text-right">
                        <h2 class="title text-uppercase text-bold m-none"><i class="fa fa-user mr-xs"></i> Sign In</h2>
                    </div>
                    <div class="panel-body">
                        <jsp:include page="displayMessageLong.jsp" />

                        <form action="AccountManagementController" >
                            <div class="form-group mb-lg">
                                <label>Username</label>
                                <div class="input-group input-group-icon">
                                    <input name="username" type="text" class="form-control input-lg" />
                                    <span class="input-group-addon">
                                        <span class="icon icon-lg">
                                            <i class="fa fa-user"></i>
                                        </span>
                                    </span>
                                </div>
                            </div>

                            <div class="form-group mb-lg">
                                <div class="clearfix">
                                    <label class="pull-left">Password</label>
                                    <a href="pages-recover-password.html" class="pull-right">Lost Password?</a>
                                </div>
                                <div class="input-group input-group-icon">
                                    <input name="pwd" type="password" class="form-control input-lg" />
                                    <span class="input-group-addon">
                                        <span class="icon icon-lg">
                                            <i class="fa fa-lock"></i>
                                        </span>
                                    </span>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-sm-12 text-right">
                                    <button type="submit" class="btn btn-primary">Sign In</button>
                                </div>
                            </div>

                            <input type="hidden" name="target" value="StaffLogin">

                        </form>
                    </div>
                </div>

                <p class="text-center text-muted mt-md mb-md">&copy; Copyright 2015. All Rights Reserved</p>
            </div>
        </section>
        <!-- end: page -->
        <script src="assets/vendor/jquery/jquery.js"></script>
        <script src="assets/vendor/jquery-browser-mobile/jquery.browser.mobile.js"></script>
        <script src="assets/vendor/bootstrap/js/bootstrap.js"></script>
        <script src="assets/javascripts/theme.js"></script>
        <script src="assets/javascripts/theme.init.js"></script>
        <script src="assets/vendor/pnotify/pnotify.custom.js"></script>



    </body>
</html>