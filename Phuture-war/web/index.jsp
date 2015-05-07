<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="header.html" />
    </head>
    <body>
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
                        <form action="AccountManagementController" method="post">
                            <div class="form-group mb-lg">
                                <label>Email</label>
                                <div class="input-group input-group-icon">
                                    <input name="email" type="email" class="form-control input-lg" />
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



    </body>
</html>