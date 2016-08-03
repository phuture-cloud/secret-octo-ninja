<%@page import="EntityManager.Invoice"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    Invoice invoice = (Invoice) (session.getAttribute("invoice"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (invoice == null) {
        response.sendRedirect("invoice.jsp?errMsg=An Error Occured.");
    } else {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Tax Invoice</title>
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
                    <h3>TAX INVOICE</h3>
                    <p>Co / GST Reg: 200919866N</p>
                </div>
            </div>

            <br>

            <div  class="row">
                <div class="col-xs-6">
                    <div  class="row">
                        <div class="col-xs-3">
                            <p><strong>BILL TO</strong></p>
                        </div>
                        <div class="col-xs-9">
                            <p>
                                <strong><%=invoice.getCustomerName()%></strong>
                                <br>
                                <%=invoice.getContactAddress().replaceAll("\\r", "<br>")%>
                            </p>
                        </div>
                    </div>

                    <div  class="row">
                        <div class="col-xs-3">
                            <p>Attention</p>
                        </div>
                        <div class="col-xs-9">
                            <p><%=invoice.getContactName()%></p>
                        </div>
                    </div>
                </div>

                <div class="col-xs-2"><!--gap--></div>

                <div class="col-xs-2">
                    <p>
                        <strong>INVOICE</strong>
                        <br>
                        Date
                        <br>
                        Delivery Order
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
                        <%=invoice.getInvoiceNumber()%>
                    </strong>
                    <br>
                    <%=DATE_FORMAT.format(invoice.getDateSent())%>
                    <br>
                    <%
                        if (invoice.getSalesConfirmationOrder().getDeliveryOrders() != null) {
                            for (int k = 0; k < invoice.getSalesConfirmationOrder().getDeliveryOrders().size(); k++) {
                                if ((k + 1) == invoice.getSalesConfirmationOrder().getDeliveryOrders().size()) {
                                    out.print(invoice.getSalesConfirmationOrder().getDeliveryOrders().get(k).getDeliveryOrderNumber());
                                } else {
                                    out.print(invoice.getSalesConfirmationOrder().getDeliveryOrders().get(k).getDeliveryOrderNumber() + ", ");
                                }
                            }
                        }
                    %>
                    <br>
                    <%=invoice.getContactOfficeNo()%>
                    <br>
                    <%=invoice.getContactFaxNo()%>
                    <br>
                    <%=invoice.getContactMobileNo()%>
                </div>
            </div>

            <br>
            <!-- / end client details section -->

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>SALESPERSON</h4></th>
                <th class='text-center'><h4>SCO</h4></th>
                <th class='text-center'><h4>PO Number</h4></th>
                <th class='text-center'><h4>PAYMENT TERMS</h4></th>
                <th class='text-center'><h4>BUYER</h4></th>
                </thead>
                <tbody>
                    <tr class="text-center">
                        <td><%=invoice.getSalesConfirmationOrder().getSalesPerson().getName()%></td>
                        <td><%=invoice.getSalesConfirmationOrder().getSalesPerson().getStaffPrefix() + invoice.getSalesConfirmationOrder().getSalesConfirmationOrderNumber()%></td>
                        <td><%=invoice.getCustomerPurchaseOrderNumber()%></td>
                        <td>
                            <%
                                if (invoice.getTerms() == 0) {
                                    out.print("Cash on delivery");
                                } else if (invoice.getTerms() == 14) {
                                    out.print("14 Days");
                                } else if (invoice.getTerms() == 30) {
                                    out.print("30 Days");
                                }
                            %>
                        </td>
                        <td><%=invoice.getContactName()%></td>
                    </tr>
                </tbody>
            </table>

            <%
                int totalItems = invoice.getItems().size();
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
                <th class='text-center'><h4>UNIT PRICE</h4> </th>
                <th class='text-center'><h4>AMOUNT</h4></th>
                </thead>
                <tbody>
                    <%
                        if (invoice.getItems().size() < maxItemPerPage) {
                            maxItemCounter = invoice.getItems().size();
                        }

                        while (i < maxItemCounter) {
                            double price = 0;
                            out.print("<tr>");
                            out.print("<td>" + invoice.getItems().get(i).getItemName() + "</td>");
                            out.print("<td>" + invoice.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");

                            out.print("<td class='text-center'>" + invoice.getItems().get(i).getItemQty() + "</td>");

                            price = invoice.getItems().get(i).getItemUnitPrice();
                            out.print("<td class='text-center'>" + formatter.format(price) + "</td>");

                            price = invoice.getItems().get(i).getItemUnitPrice() * invoice.getItems().get(i).getItemQty();
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
                        <li>Acceptance of this merchandise constitutes a contract between the buyer & Phuture International Pte Ltd whereby buyer will adhere to conditions stated on this invoice.</li>
                        <li>All cheques payment are to be crossed & made payable to "<strong>Phuture International Pte Ltd</strong>".</li>
                    </ul>

                    <%
                        if (invoice.getRemarks() != null && !invoice.getRemarks().isEmpty()) {
                            out.print("REMARKS: ");
                            out.print(invoice.getRemarks().replaceAll("\\r", "<br>"));
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
                            formatedPrice = (invoice.getTotalPriceBeforeCreditNote() / 107) * 100;
                            out.print(formatter.format(formatedPrice));
                        %>
                        <br>
                        <%
                            formatedPrice = invoice.getTotalTax();
                            out.print(formatter.format(formatedPrice));
                        %>
                        <br>
                        <%=formatter.format(invoice.getTotalPriceBeforeCreditNote())%>
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