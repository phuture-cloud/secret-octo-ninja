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
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Sales Confirmation Order</title>
        <link rel="stylesheet" href="../assets/vendor/bootstrap/css/bootstrap.css">
        <style>
            body {
                font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
                font-size: 12px;
                line-height: 1.42857143;
                color: #333333;
                background-color: #ffffff;
            }

            h4, .h4, h5, .h5, h6, .h6 {
                margin-top: 0;
                margin-bottom: 0;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-6">
                    <h1>
                        <img src="../assets/images/invoice-logo.png" alt="Phuture International" />
                    </h1>
                </div>
                <div class="col-xs-6 text-right">
                    <h1>SALES ORDER</h1>
                </div>
            </div>

            <br>

            <div  class="row">
                <div class="col-xs-1">
                    <p>To </p>
                </div>
                <div class="col-xs-4">
                    <p>
                        <strong>
                            <%=sco.getCustomerName()%>
                            <br>
                            <%=sco.getContactAddress()%>
                        </strong>
                    </p>
                </div>

                <div class="col-xs-3"><!--gap--></div>

                <div class="col-xs-2">
                    <p>
                        <strong>SCO No.</strong>
                        <br>
                        Date
                        <br>
                        Telephone
                        <br>
                        Fasimile
                        <br>
                        Mobile
                    </p>
                </div>
                <div class="col-xs-2">
                    <strong>
                        <%=sco.getSalesPerson().getStaffPrefix()%><%=sco.getSalesConfirmationOrderNumber()%>
                    </strong>
                    <br>
                    <%=DATE_FORMAT.format(sco.getSalesConfirmationOrderDate())%>
                    <br>
                    <%=sco.getContactOfficeNo()%>
                    <br>
                    <%=sco.getContactOfficeNo()%>
                    <br>
                    <%=sco.getContactMobileNo()%>
                </div>
            </div>

            <br>
            <div  class="row">
                <div class="col-xs-2">
                    <p>
                        Attention
                    </p>
                </div>
                <div class="col-xs-2">
                    <p>
                        <%=sco.getContactName()%>
                    </p>
                </div>
            </div>

            <br>
            <!-- / end client details section -->

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>SALESPERSON</h4></th>
                <th class='text-center'><h4>EST. DELIVERY DATE</h4></th>
                <th class='text-center'><h4>TERMS</h4> </th>
                </thead>
                <tbody>
                    <tr class="text-center">
                        <td><%=staff.getName()%></td>
                        <td><%=sco.getEstimatedDeliveryDate()%></td>
                        <td>
                            <%
                                if (sco.getTerms() == 0) {
                                    out.print("Cash on delivery");
                                } else if (sco.getTerms() == 14) {
                                    out.print("14 Days");
                                } else if (sco.getTerms() == 30) {
                                    out.print("30 Days");
                                }
                            %>
                        </td>
                    </tr>
                </tbody>
            </table>

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>QTY</h4></th>
                <th class='text-center'><h4>ITEM</h4></th>
                <th class='text-center'><h4>DESCRIPTION</h4></th>
                <th class='text-center'><h4>UNIT PRICE</h4> </th>
                <th class='text-center'><h4>AMOUNT</h4></th>
                </thead>
                <tbody>
                    <%
                        for (int i = 0; i < sco.getItems().size(); i++) {
                            double price = 0;
                            out.print("<tr>");
                            out.print("<td class='text-center'>" + sco.getItems().get(i).getItemQty() + "</td>");
                            out.print("<td>" + sco.getItems().get(i).getItemName() + "</td>");
                            out.print("<td>" + sco.getItems().get(i).getItemDescription() + "</td>");

                            price = sco.getItems().get(i).getItemUnitPrice();
                            out.print("<td class='text-center'>" + formatter.format(price) + "</td>");

                            price = sco.getItems().get(i).getItemUnitPrice() * sco.getItems().get(i).getItemQty();
                            out.print("<td class='text-center'>" + formatter.format(price) + "</td>");

                            out.print("</tr>");
                        }
                    %>
                </tbody>
            </table>

            <div class="row text-right">
                <div class="col-xs-7 text-left">
                    <u>Terms & Conditions</u>
                    <ul>
                        <li>Acceptance of this Sales Order constitutes a contract between the buyer & Phuture International Pte Ltd whereby buyer will adhere to conditions stated on this Sales Order</li>
                        <li>Buyer shall be liable for at least 50% of total sales amount if buyer opt to cancel the order</li>
                    </ul>
                </div>

                <div class="col-xs-3">
                    <p>
                        <strong>
                            SUBTOTAL : <br>
                            7% GST : <br>
                            Total (SGD) : <br>
                        </strong>
                    </p>
                </div>
                <div class="col-xs-2">
                    <strong>
                        <%
                            double formatedPrice = 0;
                            formatedPrice = (sco.getTotalPrice() / 107) * 100;
                            out.print(formatter.format(formatedPrice));
                        %>
                        <br>
                        <%
                            formatedPrice = sco.getTotalTax();
                            out.print(formatter.format(formatedPrice));
                        %>
                        <br>
                        <%=formatter.format(sco.getTotalPrice())%>
                        <br>
                    </strong>
                </div>
            </div>

            <br> <br>

            <div class="row text-left">
                <div class="col-xs-8">
                    AGREED & CONFIRMED

                    <br><br><br><br><br>
                    <img src="../assets/images/thin-black-line.png">
                    <br>
                    Customer's Signature & Co. Stamp
                </div>
                <div class="col-xs-4">
                    Phuture International Pte Ltd

                    <%
                        if (staff.getSignature() != null && staff.getSignature().length > 0) {
                            out.write("<img class='img-responsive' src='http://localhost:8080/Phuture-war/sig?id=" + staff.getId() + "'>");
                        } else {
                    %>
                    <br><br><br><br><br>
                    <img src="../assets/images/thin-black-line.png">
                    <%}%>
                    <br><%=staff.getName()%>
                </div>
            </div>
        </div>
    </body>
</html>
<%}%>