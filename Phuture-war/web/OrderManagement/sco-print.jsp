<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (sco == null) {
        response.sendRedirect("../scoManagement.jsp?errMsg=An Error Occured.");
    } else {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
%>
<!doctype html>
<html class="fixed">
    <head>
        <title>&nbsp;</title>

        <!-- Web Fonts  -->
        <link href="http://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800|Shadows+Into+Light" rel="stylesheet" type="text/css">
        <!-- Vendor CSS -->
        <link rel="stylesheet" href="../assets/vendor/bootstrap/css/bootstrap.css" />
        <link rel="stylesheet" href="../assets/vendor/font-awesome/css/font-awesome.css" />
        <link rel="stylesheet" href="../assets/vendor/magnific-popup/magnific-popup.css" />
        <link rel="stylesheet" href="../assets/vendor/bootstrap-datepicker/css/datepicker3.css" />
        <!-- Specific Page Vendor CSS -->
        <link rel="stylesheet" href="../assets/vendor/select2/select2.css" />
        <link rel="stylesheet" href="../assets/vendor/jquery-datatables-bs3/assets/css/datatables.css" />
        <link rel="stylesheet" href="../assets/vendor/pnotify/pnotify.custom.css" />
        <!-- Theme CSS -->
        <link rel="stylesheet" href="../assets/stylesheets/theme.css" />
        <!-- Skin CSS -->
        <link rel="stylesheet" href="../assets/stylesheets/skins/default.css" />
        <!-- Theme Custom CSS -->
        <link rel="stylesheet" href="../assets/stylesheets/theme-custom.css">
        <!-- Head Libs -->
        <script src="../assets/vendor/modernizr/modernizr.js"></script>
        <script src="../assets/vendor/jquery/jquery.min.js"></script>
        <style type="text/css">
            @media print{
                body{ background-color:#FFFFFF; background-image:none; color:#000000 }
                #ad{ display:none;}
                #leftbar{ display:none;}
                #contentarea{ width:100%;}
            }
        </style>
    </head>
    <body>
        <div class="invoice">
            <header class="clearfix">
                <div class="row">
                    <div class="col-sm-6 mt-md">
                        <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">SALES ORDER</h2>
                        <h4 class="h4 m-none text-dark text-weight-bold">SCO No. <%=staff.getStaffPrefix()%><%=sco.getSalesConfirmationOrderNumber()%></h4>
                    </div>
                    <br/>
                    <div class="col-sm-6 text-right">
                        <div class="ib">
                            <img src="../assets/images/invoice-logo.png" alt="Phuture International" />
                        </div>
                        <br/>
                        <address class="ib">
                            Phuture International Ltd
                            <br/>
                            28 Sin Ming Lane, #06-145 Midview City 
                            <br/>
                            Singapore (573972)
                            <br/>
                            Phone: (65) 6842 0198
                            <br/>
                            Fax: (65) 6285 6753
                        </address>
                    </div>
                </div>
            </header>

            <div class="bill-info">
                <div class="row">
                    <div class="col-md-6">
                        <div class="bill-to" style="padding-top: 0px;padding-bottom: 0px;">
                            <p class="h5 mb-xs text-dark text-weight-semibold">To:</p>
                            <address>
                                <div class="col-md-6">      
                                    <%
                                        out.print("<b>" + sco.getCustomerName() + "</b>");
                                        out.print("<br>" + sco.getContactAddress());
                                        out.print("<br><br><p><strong>Attention: </strong> " + sco.getContactName() + "</p>");
                                    %>
                                </div>
                            </address>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="bill-data text-right" style="padding-top: 0px; padding-bottom: 0px;">
                            <p class="mb-none">
                                <span class="text-dark">Salesperson: </span>
                                <span class="value"><%=staff.getName()%></span>
                            </p>
                            <p class="mb-none">
                                <span class="text-dark">Date:</span>
                                <span class="value">
                                    <%
                                        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
                                        String date = DATE_FORMAT.format(sco.getSalesConfirmationOrderDate());
                                        out.print(date);
                                    %>
                                </span>
                            </p>
                            <%if (sco.getContactOfficeNo() != null && !sco.getContactOfficeNo().isEmpty()) {%>
                            <p class="mb-none">
                                <span class="text-dark">Telephone</span>
                                <span class="value"><%=sco.getContactOfficeNo()%></span>
                            </p>
                            <%}%>
                            <%if (sco.getContactFaxNo() != null && !sco.getContactFaxNo().isEmpty()) {%>
                            <p class="mb-none">
                                <span class="text-dark">Fasimile</span>
                                <span class="value"><%=sco.getContactFaxNo()%></span>
                            </p>
                            <%if (sco.getContactMobileNo() != null && !sco.getContactMobileNo().isEmpty()) {%>
                            <p class="mb-none">
                                <span class="text-dark">Mobile</span>
                                <span class="value"><%=sco.getContactMobileNo()%></span>
                            </p>
                            <%}%>
                            <p class="mb-none">
                                <span class="text-dark">Terms:</span>
                                <span class="value">
                                    <%
                                        if (sco.getTerms() == 0) {
                                            out.print("Cash on delivery");
                                        } else if (sco.getTerms() == 14) {
                                            out.print("14 Days");
                                        } else if (sco.getTerms() == 30) {
                                            out.print("30 Days");
                                        }
                                    %>
                                </span>
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table invoice-items">
                    <thead>
                        <tr class="h4 text-dark">
                            <th id="cell-item" class="text-weight-semibold">Item</th>
                            <th id="cell-desc" class="text-weight-semibold">Description</th>
                            <th id="cell-price" class="text-center text-weight-semibold">Unit Price</th>
                            <th id="cell-qty" class="text-center text-weight-semibold">Quantity</th>
                            <th id="cell-total" class="text-center text-weight-semibold">Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- loop line item page -->
                        <%
                            for (int i = 0; i < sco.getItems().size(); i++) {
                                double price = 0;
                                out.print("<tr>");
                                out.print("<td class='text-weight-semibold text-dark'>" + sco.getItems().get(i).getItemName() + "</td>");
                                out.print("<td>" + sco.getItems().get(i).getItemDescription() + "</td>");
                                price = sco.getItems().get(i).getItemUnitPrice();
                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                out.print("<td class='text-center'>" + sco.getItems().get(i).getItemQty() + "</td>");
                                price = sco.getItems().get(i).getItemUnitPrice() * sco.getItems().get(i).getItemQty();
                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                out.print("</div>");
                                out.print("</tr>");
                            }
                        %>
                        <!-- end loop line item page -->
                    </tbody>
                </table>
            </div>

            <div class="invoice-summary" style="margin-top: 10px;">
                <div class="row">
                    <div class="col-sm-5">
                        Terms & Conditions
                        <ul>
                            <li>Acceptance of this Sales Order constitutes a contract between the buyer & Phuture International Pte Ltd whereby buyer will adhere to conditions stated on this Sales Order</li>
                            <li>Buyer shall be liable for at least 50% of total sales amount if buyer opt to cancel the order</li>
                        </ul>
                        <%
                            if (sco.getRemarks() != null && !sco.getRemarks().isEmpty()) {
                                out.print("Remarks: " + sco.getRemarks());
                            }
                        %>
                    </div>
                    <div class="col-sm-1"></div>
                    <div class="col-sm-6">
                        <table class="table h5 text-dark">
                            <tbody>
                                <tr class="b-top-none">
                                    <td colspan="2">Subtotal</td>
                                    <td class="text-left">
                                        <%
                                            double formatedPrice = 0;
                                            formatedPrice = (sco.getTotalPrice() / 107) * 100;
                                            out.print(formatter.format(formatedPrice));
                                        %>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2">7% GST</td>
                                    <td class="text-left">
                                        <%
                                            formatedPrice = sco.getTotalTax();
                                            out.print(formatter.format(formatedPrice));
                                        %>
                                    </td>
                                </tr>
                                <tr class="h4">
                                    <td colspan="2">Total (SGD)</td>
                                    <td class="text-left">
                                        <%=formatter.format(sco.getTotalPrice())%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <br><br>

            <div class="row">
                <div class="col-sm-6">
                    <b>AGREED & CONFIRMED</b>

                    <br><br><br><br><br><br>
                    ------------------------------------------------
                    <br>
                    Customer's Signature & Co. Stamp
                </div>
                <div class="col-sm-6">
                    <b>Phuture International Pte Ltd</b>
                    <%
                        if (staff.getSignature() != null && staff.getSignature().length > 0) {
                            out.write("<img class='img-responsive' src='http://localhost:8080/Phuture-war/sig?id=" + staff.getId() + "'>");
                        }
                    %>
                    <br>
                    ------------------------------------------------
                    <br>
                    <%=staff.getName()%>
                </div>
            </div>
        </div>
        <jsp:include page="../jspIncludePages/foot.html" />
        <script>
            window.print();
        </script>
    </body>
</html>
<%}%>

