<%@page import="EntityManager.Supplier"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    List<Supplier> suppliers = (List<Supplier>) (session.getAttribute("suppliers"));
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
            function updateSupplier(id, name) {
                window.location.href = "supplierManagement_update.jsp?id=" + id + "&name=" + name;
            }
            function removeSupplier(id) {
                window.location.href = "../SupplierManagementController?target=RemoveSupplier&id=" + id;
            }
            function addSupplier() {
                window.location.href = "supplierManagement_add.jsp";
            }
            function viewContact(id) {
                window.location.href = "../SupplierManagementController?target=ListSupplierContacts&id=" + id;
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Supplier Management</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Supplier Management &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->

                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title">Supplier Management</h2>
                        </header>
                        <div class="panel-body">

                            <div class="row">
                                <div class="col-md-12">
                                    <button class="btn btn-primary" onclick="addSupplier()">Add Supplier</button>
                                </div>
                            </div>
                            <br/>

                            <form name="supplierManagement">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Company</th>
                                            <th style="width: 600px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>

                                        <%
                                            if (suppliers != null && suppliers.size() > 0) {
                                                for (int i = 0; i < suppliers.size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=suppliers.get(i).getSupplierName()%></td>
                                            <td>
                                                <div class="btn-group" role="group" aria-label="...">
                                                    <button type="button" class="btn btn-default" onclick="javascript:viewContact('<%=suppliers.get(i).getId()%>')">Contact Management</button>
                                                    <button type="button" class="btn btn-default" onclick="javascript:updateSupplier('<%=suppliers.get(i).getId()%>', '<%=suppliers.get(i).getSupplierName()%>')">Update</button>
                                                    <button type="button" class="modal-with-move-anim btn btn-default" href="#modalRemove<%=suppliers.get(i).getId()%>">Remove</button>
                                                </div>

                                                <div id="modalRemove<%=suppliers.get(i).getId()%>" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
                                                    <section class="panel">
                                                        <header class="panel-heading">
                                                            <h2 class="panel-title">Are you sure?</h2>
                                                        </header>
                                                        <div class="panel-body">
                                                            <div class="modal-wrapper">
                                                                <div class="modal-icon">
                                                                    <i class="fa fa-question-circle"></i>
                                                                </div>
                                                                <div class="modal-text">
                                                                    <p>Are you sure that you want to delete this supplier?</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <footer class="panel-footer">
                                                            <div class="row">
                                                                <div class="col-md-12 text-right">
                                                                    <button class="btn btn-primary modal-confirm" onclick="javascript:removeSupplier('<%=suppliers.get(i).getId()%>')">Confirm</button>
                                                                    <button class="btn btn-default modal-dismiss">Cancel</button>
                                                                </div>
                                                            </div>
                                                        </footer>
                                                    </section>
                                                </div>
                                            </td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>

                                    </tbody>
                                </table>
                                <input type="hidden" name="name" value="">
                                <input type="hidden" name="id" value="">
                                <input type="hidden" name="target" value="">    
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