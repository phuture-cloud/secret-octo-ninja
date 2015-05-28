<%@page import="EntityManager.Customer"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    List<Customer> customers = (List<Customer>) (session.getAttribute("customers"));
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
    <body>
        <section class="body">
            <script>
                function back() {
                    window.location.href = "../AccountManagementController?target=ListAllStaff";
                }
            </script>
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Create Sales Confirmation Order</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span>Sales Confirmation Order</span></li>
                                <li><span>New Sales Confirmation Order &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <div class="panel-body">
                            <div class="invoice">
                                <header class="clearfix">
                                    <div class="row">
                                        <div class="col-sm-6 mt-md">
                                            <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Sales Confirmation Order</h2>
                                            <h4 class="h4 m-none text-dark text-weight-bold">#76598345</h4>
                                        </div>
                                        <div class="col-sm-6 text-right mt-md mb-md">
                                            <address class="ib mr-xlg">
                                                Phuture International Ltd
                                                <br/>
                                                28 Sin Ming Lane, #06-145 Midview City S(573972)
                                                <br/>
                                                Phone: (65) 6842 0198
                                            </address>
                                            <div class="ib">
                                                <img src="../assets/images/invoice-logo.png" alt="OKLER Themes" />
                                            </div>
                                        </div>
                                    </div>
                                </header>
                                <div class="bill-info">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="bill-to">
                                                <p class="h5 mb-xs text-dark text-weight-semibold">To:</p>
                                                <address>
                                                    <form class="form-horizontal form-bordered" action="#">
                                                        <div class="col-md-6" style="padding-left: 0px;">
                                                            <select data-plugin-selectTwo class="form-control populate">
                                                                <optgroup label="Customer">
                                                                    <%
                                                                        if (customers != null && customers.size() > 0) {
                                                                            for (int i = 0; i < customers.size(); i++) {
                                                                                out.print("<option value='" + customers.get(i).getId() + "'>" + customers.get(i).getCustomerName() + "</option>");
                                                                            }
                                                                        }
                                                                    %>
                                                                </optgroup>
                                                            </select>
                                                        </div>
                                                    </form>
                                                    <br/><br/>
                                                    121 King Street, Melbourne, Australia
                                                    <br/>
                                                    Phone: +61 3 8376 6284
                                                    <br/>
                                                    info@envato.com
                                                </address>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="bill-data text-right">
                                                <p class="mb-none">
                                                    <span class="text-dark">Date:</span>
                                                    <span class="value"></span>
                                                </p>
                                                <p class="mb-none">
                                                    <span class="text-dark">Terms:</span>
                                                    <span class="value">06/20/2014</span>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="table-responsive">
                                    <table class="table invoice-items">
                                        <thead>
                                            <tr class="h4 text-dark">
                                                <th id="cell-id"     class="text-weight-semibold">#</th>
                                                <th id="cell-item"   class="text-weight-semibold">Item</th>
                                                <th id="cell-desc"   class="text-weight-semibold">Description</th>
                                                <th id="cell-price"  class="text-center text-weight-semibold">Price</th>
                                                <th id="cell-qty"    class="text-center text-weight-semibold">Quantity</th>
                                                <th id="cell-total"  class="text-center text-weight-semibold">Total</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>123456</td>
                                                <td class="text-weight-semibold text-dark">Porto HTML5 Template</td>
                                                <td>Multipourpouse Website Template</td>
                                                <td class="text-center">$14.00</td>
                                                <td class="text-center">2</td>
                                                <td class="text-center">$28.00</td>
                                            </tr>
                                            <tr>
                                                <td>654321</td>
                                                <td class="text-weight-semibold text-dark">Tucson HTML5 Template</td>
                                                <td>Awesome Website Template</td>
                                                <td class="text-center">$17.00</td>
                                                <td class="text-center">1</td>
                                                <td class="text-center">$17.00</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="invoice-summary">
                                    <div class="row">
                                        <div class="col-sm-4 col-sm-offset-8">
                                            <table class="table h5 text-dark">
                                                <tbody>
                                                    <tr class="b-top-none">
                                                        <td colspan="2">Subtotal</td>
                                                        <td class="text-left">$73.00</td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2">Shipping</td>
                                                        <td class="text-left">$0.00</td>
                                                    </tr>
                                                    <tr class="h4">
                                                        <td colspan="2">Grand Total</td>
                                                        <td class="text-left">$73.00</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="text-right mr-lg">
                                <a href="#" class="btn btn-default">Submit Invoice</a>
                                <a href="#" target="_blank" class="btn btn-primary ml-sm"><i class="fa fa-print"></i> Print</a>
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