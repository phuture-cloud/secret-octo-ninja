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
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Purchase Order</title>
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
                                <strong><%=purchaseOrder.getSupplierName()%></strong>
                                <br>
                                <%=purchaseOrder.getContactAddress().replaceAll("\\r", "<br>")%>
                            </p>
                        </div>
                    </div>

                    <div  class="row">
                        <div class="col-xs-3">
                            <p>Attention</p>
                        </div>
                        <div class="col-xs-9">
                            <p><%=purchaseOrder.getContactName()%></p>
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
                        if (purchaseOrder.getContactOfficeNo() != null) {
                            out.print(purchaseOrder.getContactOfficeNo());
                        }
                    %>

                    <br>
                    <%
                        if (purchaseOrder.getContactFaxNo() != null) {
                            out.print(purchaseOrder.getContactFaxNo());
                        }
                    %>

                    <br>
                    <%
                        if (purchaseOrder.getContactMobileNo() != null) {
                            out.print(purchaseOrder.getContactMobileNo());
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
                                    out.print(purchaseOrder.getSalesConfirmationOrder().getSalesPerson().getStaffPrefix() + purchaseOrder.getSalesConfirmationOrder().getSalesConfirmationOrderNumber());
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
                                    out.print(DATE_FORMAT.format(purchaseOrder.getDeliveryDate()));
                                }
                            %>
                        </td>
                    </tr>
                </tbody>
            </table>

            <%
                int totalItems = purchaseOrder.getItems().size();
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
                <th class='text-center'><h4>UNIT PRICE (<%if (purchaseOrder.getCurrency() != null) {
                        out.print(purchaseOrder.getCurrency());
                    }%>)</h4></th>
                <th class='text-center'><h4>TOTAL (<%if (purchaseOrder.getCurrency() != null) {
                        out.print(purchaseOrder.getCurrency());
                    }%>)</h4></th>
                </thead>
                <tbody>
                    <%
                        if (purchaseOrder.getItems().size() < maxItemPerPage) {
                            maxItemCounter = purchaseOrder.getItems().size();
                        }

                        while (i < maxItemCounter) {
                            double price = 0;
                            out.print("<tr>");
                            out.print("<td>" + purchaseOrder.getItems().get(i).getItemName() + "</td>");
                            out.print("<td>" + purchaseOrder.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");
                            out.print("<td class='text-center'>" + purchaseOrder.getItems().get(i).getItemQty() + "</td>");

                            price = purchaseOrder.getItems().get(i).getItemUnitPrice();
                            out.print("<td class='text-center'>" + formatter.format(price) + "</td>");

                            price = purchaseOrder.getItems().get(i).getItemUnitPrice() * purchaseOrder.getItems().get(i).getItemQty();
                            out.print("<td class='text-center'>" + formatter.format(price) + "</td>");

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
                        <li>Phuture International Pte Ltd reserves all right to cancel this Purchase Order due to supplier unable to fulfill or meet the buyer's requirements.</li>
                        <li>Purchase Order is not subjected to GST/VAT.</li>
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
                            Total <%if (purchaseOrder.getCurrency() != null && !purchaseOrder.getCurrency().isEmpty()) {
                                    out.print("(" + purchaseOrder.getCurrency() + ")");
                                }%> : <br>
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

        <jsp:include page="../footer.html" />
    </body>
</html>
<%
        } catch (Exception ex) {
            out.print("<h1>An error has occured</h1>");
        }
    }
%>