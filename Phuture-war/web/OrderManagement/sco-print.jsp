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
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Sales Confirmation Order</title>
        <link rel="stylesheet" href="../assets/vendor/bootstrap/css/bootstrap.css">
        <link rel="stylesheet" href="../assets/stylesheets/invoice-print.css">
    </head>

    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-6">
                    <img src="../assets/images/invoice-logo.png" alt="Phuture International" />
                </div>
                <div class="col-xs-6 text-right">
                    <h3>SALES ORDER</h3>
                    <p>Co / GST Reg: 200919866N</p>
                </div>
            </div>

            <br>

            <div class="row">
                <div class="col-xs-6">
                    <div class="row">
                        <div class="col-xs-3">
                            <p><strong>To</strong></p>
                        </div>
                        <div class="col-xs-9">
                            <p>
                                <strong><%=sco.getCustomerName()%></strong>
                                <br>
                                <%=sco.getContactAddress().replaceAll("\\r", "<br>")%>
                            </p>
                        </div>
                    </div>

                    <div  class="row">
                        <div class="col-xs-3">
                            <p>Attention</p>
                        </div>
                        <div class="col-xs-9">
                            <p><%=sco.getContactName()%></p>
                        </div>
                    </div>
                </div>

                <div class="col-xs-2"><!--gap--></div>

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
                    <%=sco.getContactFaxNo()%>
                    <br>
                    <%=sco.getContactMobileNo()%>
                </div>
            </div>

            <br>
            <!-- / end client details section -->
            <table class="table table-bordered">
                <thead>
                <th class='text-center'><h4>SALESPERSON</h4></th>
                <th class='text-center'><h4>PO Number</h4></th>
                <th class='text-center'><h4>EST. DELIVERY DATE</h4></th>
                <th class='text-center'><h4>TERMS</h4> </th>
                </thead>
                <tbody>
                    <tr class="text-center">
                        <td><%=staff.getName()%></td>
                        <td><%=sco.getCustomerPurchaseOrderNumber()%></td>
                        <td><%=DATE_FORMAT.format(sco.getEstimatedDeliveryDate())%></td>
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
                <thead>
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
                            out.print("<td>" + sco.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");

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
                <div class="col-xs-7 text-left" style="font-size: 9px;">
                    <u>Terms & Conditions</u>
                    <ul style="padding-left: 20px;">
                        <li>Acceptance of this Sales Order constitutes a contract between the buyer & Phuture International Pte Ltd whereby buyer will adhere to conditions stated on this Sales Order.</li>
                        <li>Buyer shall be liable for at least 50% of total sales amount if buyer opt to cancel the order.</li>
                    </ul>
                    <%
                        if (sco.getRemarks() != null && !sco.getRemarks().isEmpty()) {
                            out.print("REMARKS: ");
                            out.print(sco.getRemarks().replaceAll("\\r", "<br>"));
                        }
                    %>
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

            <br><br>

            <div class="row text-left">
                <div class="col-xs-8">
                    <strong>AGREED & CONFIRMED</strong>
                    <br>
                    <img src="../assets/images/thin-black-line.png" style='padding-top: 80px; padding-bottom: 5px;'>
                    <br>Customer's Signature & Co. Stamp
                </div>
                <div class="col-xs-4">
                    <strong>Phuture International Pte Ltd</strong>
                    <%
                        if (staff.getSignature() != null && staff.getSignature().length > 0) {
                            out.write("<img class='img-responsive' style='height: 80px;' src='../sig?id=" + staff.getId() + "'>");
                            out.write("<img src='../assets/images/thin-black-line.png' style='padding-bottom: 5px;'>");
                        } else {
                            out.write("<br><img src='../assets/images/thin-black-line.png' style='padding-top: 80px; padding-bottom: 5px;'>");
                        }
                    %>
                    <br>Authorized Signature
                </div>
            </div>
        </div>
        <div class="row">
            <p style="text-align: center; color: #000;">
                <strong>Phuture International Pte Ltd</strong><br>
                28 Sin Ming Lane #06-145<br/>
                Midview City, Singapore 573972<br/>
                Tel: (65) 6842 0198 &nbsp; Fax: (65) 6285 6753
            </p>
        </div>
    </body>
</html>
<%
        } catch (Exception ex) {
            out.print("<h1>An error has occured</h1>");
        }
    }
%>