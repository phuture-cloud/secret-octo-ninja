<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    DeliveryOrder deliveryOrder = (DeliveryOrder) (session.getAttribute("do"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (deliveryOrder == null) {
        response.sendRedirect("deliveryOrder.jsp?errMsg=An Error Occured.");
    } else {
        try {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Delivery Order</title>
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
                    <h3>Delivery Order</h3>
                    <p>Co / GST Reg: 200919866N</p>
                </div>
            </div>
            <br>
            <div  class="row">
                <div class="col-xs-6">
                    <div  class="row">
                        <div class="col-xs-3">
                            <p><strong>SHIP TO</strong></p>
                        </div>
                        <div class="col-xs-9">
                            <p>
                                <strong><%=deliveryOrder.getCustomerName()%></strong>
                                <br>
                                <%=deliveryOrder.getContactAddress().replaceAll("\\r", "<br>")%>
                            </p>
                        </div>
                    </div>

                    <div  class="row">
                        <div class="col-xs-3">
                            <p>Attention</p>
                        </div>
                        <div class="col-xs-9">
                            <p><%=deliveryOrder.getContactName()%></p>
                        </div>
                    </div>
                </div>

                <div class="col-xs-2"><!--gap--></div>

                <div class="col-xs-2">
                    <p>
                        <strong>DELIVERY NO</strong>
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
                        <%=deliveryOrder.getDeliveryOrderNumber()%>
                    </strong>
                    <br>
                    <%=DATE_FORMAT.format(deliveryOrder.getDeliveryOrderDate())%>
                    <br>
                    <%=deliveryOrder.getContactOfficeNo()%>
                    <br>
                    <%=deliveryOrder.getContactFaxNo()%>
                    <br>
                    <%=deliveryOrder.getContactMobileNo()%>
                </div>
            </div>

            <br>
            <!-- / end client details section -->

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>SALESPERSON</h4></th>
                <th class='text-center'><h4>SCO</h4></th>
                <th class='text-center'><h4>PO Number</h4></th>
                <th class='text-center'><h4>BUYER</h4></th>
                </thead>
                <tbody>
                    <tr class="text-center">
                        <td><%=deliveryOrder.getSalesConfirmationOrder().getSalesPerson().getName()%></td>
                        <td><%=deliveryOrder.getSalesConfirmationOrder().getSalesPerson().getStaffPrefix() + deliveryOrder.getSalesConfirmationOrder().getSalesConfirmationOrderNumber()%></td>
                        <td><%=deliveryOrder.getCustomerPurchaseOrderNumber()%></td>
                        <td><%=deliveryOrder.getContactName()%></td>
                    </tr>
                </tbody>
            </table>

            <%
                int totalItems = deliveryOrder.getItems().size();
                int maxItemPerPage = 7;
                int maxItemCounter = maxItemPerPage;

                int loopCounter = (int) Math.ceil((double) totalItems / maxItemPerPage);
                int i = 0;

                for (int k = 0; k < loopCounter; k++) {
            %>  
            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>ITEM</h4></th>
                <th class='text-center'><h4>DESCRIPTION</h4></th>
                <th class='text-center'><h4>QTY</h4></th>
                </thead>
                <tbody>
                    <%
                        if (deliveryOrder.getItems().size() < maxItemPerPage) {
                            maxItemCounter = deliveryOrder.getItems().size();
                        }

                        while (i < maxItemCounter) {
                            out.print("<tr>");
                            out.print("<td>" + deliveryOrder.getItems().get(i).getItemName() + "</td>");
                            out.print("<td>" + deliveryOrder.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");
                            out.print("<td class='text-center'>" + deliveryOrder.getItems().get(i).getItemQty() + "</td>");
                            out.print("</tr>");
                            i++;
                        }

                        totalItems = totalItems - maxItemPerPage;

                        if (totalItems - maxItemPerPage > 0) {
                            maxItemCounter = maxItemCounter + maxItemPerPage;
                        } else {
                            maxItemCounter = maxItemCounter + totalItems;
                        }
                    %>
                </tbody>
            </table>
            <%
                    if ((k + 1) < loopCounter) {
                        out.print("<p style='page-break-after:always;'></p>");
                    }
                }
            %>

            <div class="row text-right">
                <div class="col-xs-7 text-left" style="font-size: 9px;">
                    <u>Terms & Conditions</u>
                    <ul style="padding-left: 20px;">
                        <li>All Goods Delivered Are Non Returnable / Refundable.</li>
                    </ul>
                    <%
                        if (deliveryOrder != null && deliveryOrder.getRemarks() != null && !deliveryOrder.getRemarks().isEmpty()) {
                            out.print("Remarks: ");
                            out.print(deliveryOrder.getRemarks().replaceAll("\\r", "<br>"));
                        }
                    %>
                </div>
            </div>

            <br> <br>

            <div class="row text-left">
                <div class="col-xs-8">
                    <strong>Goods Received In Good Condition & Order</strong>
                    <br>
                    <img src="../assets/images/thin-black-line.png" style='padding-top: 80px; padding-bottom: 5px;'>
                    <br>Customer's Signature & Co. Stamp
                </div>
            </div>
        </div>
    </body>
    <footer>
        <div class="divFooter">
            <p>
                <strong>Phuture International Pte Ltd</strong><br>
                28 Sin Ming Lane #06-145<br/>
                Midview City, Singapore 573972<br/>
                Tel: (65) 6842 0198 &nbsp; Fax: (65) 6285 6753
            </p>
        </div>
    </footer>
</html>
<%
        } catch (Exception ex) {
            out.print("<h1>An error has occured</h1>");
        }
    }
%>