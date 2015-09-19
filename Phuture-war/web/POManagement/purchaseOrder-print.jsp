<%@page import="EntityManager.PurchaseOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    PurchaseOrder purchaseOrder = (PurchaseOrder) (session.getAttribute("po"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (purchaseOrder == null) {
        response.sendRedirect("purchaseOrder.jsp?errMsg=An Error Occured.");
    } else {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Purchase Order</title>
        <link rel="stylesheet" href="../assets/vendor/bootstrap/css/bootstrap.css">
        <style>
            body {
                font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
                font-size: 10px;
                line-height: 1.42857143;
                color: #333333;
                background-color: #ffffff;
            }

            h4, .h4, h5, .h5, h6, .h6 {
                font-size: 10px;
                margin-top: 0;
                margin-bottom: 0;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-6">
                    <img src="../assets/images/invoice-logo.png" alt="Phuture International" />
                </div>
                <div class="col-xs-6 text-right">
                    <h3>Purchase Order</h3>
                    <p>Co / GST Reg: 200919866N</p>
                </div>
            </div>
            <br>

            <div  class="row">
                <div class="col-xs-6">
                    <div  class="row">
                        <div class="col-xs-3">
                            <p><strong>Vendor</strong></p>
                        </div>
                        <div class="col-xs-9">
                            <p>
                                <strong><%=purchaseOrder.getCompanyName()%></strong>
                                <br>
                                <%=purchaseOrder.getSupplierAddress().replaceAll("\\r", "<br>")%>
                            </p>
                        </div>
                    </div>

                    <div  class="row">
                        <div class="col-xs-3">
                            <p>Attention</p>
                        </div>
                        <div class="col-xs-9">
                            <p><%=purchaseOrder.getSupplierName()%></p>
                        </div>
                    </div>
                </div>

                <div class="col-xs-2"><!--gap--></div>

                <div class="col-xs-2">
                    <p>
                        <strong>PO NO.</strong>
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
                        <%=purchaseOrder.getPurchaseOrderNumber()%>
                    </strong>
                    <br>
                    <%=DATE_FORMAT.format(purchaseOrder.getPurchaseOrderDate())%>
                    <br>
                    <%
                        if (purchaseOrder.getSupplierOfficeNo() != null) {
                            out.print(purchaseOrder.getSupplierOfficeNo());
                        }
                    %>

                    <br>
                    <%
                        if (purchaseOrder.getSupplierFaxNo() != null) {
                            out.print(purchaseOrder.getSupplierFaxNo());
                        }
                    %>

                    <br>
                    <%
                        if (purchaseOrder.getSupplierMobileNo() != null) {
                            out.print(purchaseOrder.getSupplierMobileNo());
                        }
                    %>
                </div>
            </div>

            <br>
            <!-- / end client details section -->

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>BUYER</h4></th>
                <th class='text-center'><h4>SCO</h4></th>
                <th class='text-center'><h4>TERMS</h4></th>
                <th class='text-center'><h4>DELIVERY DATE</h4></th>
                </thead>
                <tbody>
                    <tr class="text-center">
                        <td>
                            <%=purchaseOrder.getSalesConfirmationOrder().getSalesPerson().getName()%>
                        </td>
                        <td>
                            <%
                                if (purchaseOrder.getSalesConfirmationOrder().getSalesConfirmationOrderNumber() != null) {
                                    out.print(purchaseOrder.getSalesConfirmationOrder().getSalesConfirmationOrderNumber());
                                }
                            %>
                        </td>
                        <td>
                            <%
                                if (purchaseOrder.getTerms() != null) {
                                    out.print(purchaseOrder.getTerms());
                                }
                            %>
                        </td>
                        <td>
                            <%
                                if (purchaseOrder.getDeliveryDate() != null) {
                                    out.print(purchaseOrder.getDeliveryDate());
                                }
                            %>
                        </td>
                    </tr>
                </tbody>
            </table>

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>ITEM</h4></th>
                <th class='text-center'><h4>DESCRIPTION</h4></th>
                <th class='text-center'><h4>QTY</h4></th>
                <th class='text-center'><h4>UNIT PRICE (<%if (purchaseOrder.getCurrency() != null) {
                        out.print(purchaseOrder.getCurrency());
                    }%>)</h4></th>
                <th class='text-center'><h4>TOTAL (<%if (purchaseOrder.getCurrency() != null) {
                        out.print(purchaseOrder.getCurrency());
                    }%>)</h4></th>
                </thead>
                <tbody>
                    <%
                        for (int i = 0; i < purchaseOrder.getItems().size(); i++) {
                            double price = 0;
                            out.print("<tr>");
                            out.print("<td>" + purchaseOrder.getItems().get(i).getItemName() + "</td>");
                            out.print("<td>" + purchaseOrder.getItems().get(i).getItemDescription() + "</td>");

                            out.print("<td class='text-center'>" + purchaseOrder.getItems().get(i).getItemQty() + "</td>");

                            price = purchaseOrder.getItems().get(i).getItemUnitPrice();
                            out.print("<td class='text-center'>" + formatter.format(price) + "</td>");

                            price = purchaseOrder.getItems().get(i).getItemUnitPrice() * purchaseOrder.getItems().get(i).getItemQty();
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
                        <li>Phuture International Pte Ltd reserves all right to cancel this Purchase Order due to supplier unable to fulfill or meet the buyer's requirements.</li>
                    </ul>
                    <%
                        if (purchaseOrder.getRemarks() != null && !purchaseOrder.getRemarks().isEmpty()) {
                            out.print("REMARKS: ");
                            out.print(purchaseOrder.getRemarks().replaceAll("\\r", "<br>"));
                        }
                    %>
                </div>


                <div class="col-xs-3">
                    <p>
                        <strong>
                            Total (<%if (purchaseOrder.getCurrency() != null) {
                                    out.print(purchaseOrder.getCurrency());
                                }%>) : <br>
                        </strong>
                    </p>
                </div>
                <div class="col-xs-2">
                    <strong>
                        <%=formatter.format(purchaseOrder.getTotalPrice())%>
                        <br>
                    </strong>
                </div>
            </div>

            <br><br>

            <div class="row text-left">
                <div class="col-xs-8">
                </div>
                <div class="col-xs-4">
                    <strong>Phuture International Pte Ltd</strong>
                    <%
                        if (staff.getSignature() != null && staff.getSignature().length > 0) {
                            out.write("<img class='img-responsive' src='http://localhost:8080/Phuture-war/sig?id=" + staff.getId() + "'>");
                        }
                    %>
                    <img src="../assets/images/thin-black-line.png" style='padding-bottom: 5px;'>
                    <br>Authorized Signature
                </div>
            </div>
        </div>
    </body>
</html>
<%}%>