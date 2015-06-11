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
        <jsp:include page="../head.html" />
    </head>
    <body>
        <div class="invoice">
            <header class="clearfix">
                <div class="row">
                    <div class="col-sm-6 mt-md">
                        <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">SALES ORDER</h2>
                        <h4 class="h4 m-none text-dark text-weight-bold">SCO No. <%=sco.getSalesConfirmationOrderNumber()%></h4>
                    </div>
                    <br/>
                    <div class="col-sm-6 text-right mt-md mb-md">
                        <address class="ib">
                            Phuture International Ltd
                            <br/>
                            28 Sin Ming Lane, #06-145 Midview City S(573972)
                            <br/>
                            Phone: (65) 6842 0198
                        </address>
                        <br/>
                        <div class="ib">
                            <img src="../assets/images/invoice-logo.png" alt="Phuture International" />
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
                                <div class="col-md-6" style="padding-left: 0px;">      
                                    <%
                                        out.print("<b>" + sco.getCustomerName() + "</b>");
                                        out.print("<br>" + sco.getContactAddress());
                                        out.print("<br>" + sco.getContactOfficeNo());
                                        if (sco.getContactFaxNo() != null && !sco.getContactFaxNo().isEmpty()) {
                                            out.print("<br>" + sco.getContactFaxNo());
                                        }
                                        out.print("<p class='h5 mb-xs text-dark text-weight-semibold'>Attention:</p>");
                                        out.print(sco.getContactName() + " ");
                                        if (sco.getContactMobileNo() != null && !sco.getContactMobileNo().isEmpty()) {
                                            out.print("<br>" + sco.getContactMobileNo());
                                        }
                                        if (sco.getContactEmail() != null && !sco.getContactEmail().isEmpty()) {
                                            out.print("<br>" + sco.getContactEmail());
                                        }
                                    %>
                                    <br><br>
                                </div>
                            </address>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="bill-data text-right">
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
                            <p class="mb-none">
                                <span class="text-dark">Terms:</span>
                                <span class="value"><%=sco.getTerms()%></span>
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
                    <div class="col-sm-8">
                        <%
                            if (sco.getRemarks().isEmpty()) {
                                out.print("Remarks:");
                                out.print("<br>" + sco.getRemarks());
                            }
                        %>
                    </div>
                    <div class="col-sm-4">
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
                                            formatedPrice = (sco.getTotalPrice() / 107) * 7;
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
        </div>
        <jsp:include page="../foot.html" />
        <script>
            //window.print();
        </script>
    </body>
</html>
<%}%>

