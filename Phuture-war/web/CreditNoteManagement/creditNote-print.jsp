<%@page import="EntityManager.CreditNote"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    CreditNote creditNote = (CreditNote) (session.getAttribute("creditNote"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (creditNote == null) {
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
        <title>Credit Note</title>
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
                    <h3>CREDIT NOTE</h3>
                    <p>Co / GST Reg: 200919866N</p>
                </div>
            </div>

            <br>

            <div  class="row">
                <div class="col-xs-6">
                    <div  class="row">
                        <div class="col-xs-3">
                            <p><strong>CREDIT TO</strong></p>
                        </div>
                        <div class="col-xs-9">
                            <p>
                                <strong><%=creditNote.getCustomer().getCustomerName()%></strong>
                                <br>
                                <%=creditNote.getContactAddress().replaceAll("\\r", "<br>")%>
                            </p>
                        </div>
                    </div>

                    <div  class="row">
                        <div class="col-xs-3">
                            <p>Attention</p>
                        </div>
                        <div class="col-xs-9">
                            <p><%=creditNote.getContactName()%></p>
                        </div>
                    </div>
                </div>
                <div class="col-xs-2"><!--gap--></div>

                <div class="col-xs-2">
                    <p>
                        <strong>CN</strong>
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
                        <%=creditNote.getCreditNoteNumber()%>
                    </strong>
                    <br>
                    <%=DATE_FORMAT.format(creditNote.getDateIssued())%>
                    <br>
                    <%=creditNote.getContactOfficeNo()%>
                    <br>
                    <%=creditNote.getContactFaxNo()%>
                    <br>
                    <%=creditNote.getContactMobileNo()%>
                </div>
            </div>

            <br>
            <!-- / end client details section -->

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>SALESPERSON</h4></th>
                <th class='text-center'><h4>SCO</h4></th>
                <th class='text-center'><h4>PO Number</h4></th>
                <th class='text-center'><h4>INVOICE</h4></th>
                <th class='text-center'><h4>BUYER</h4></th>
                </thead>
                <tbody>
                    <tr class="text-center">
                        <td><%if (creditNote.getAppliedToInvoice() != null) {
                                out.print(creditNote.getAppliedToInvoice().getSalesConfirmationOrder().getSalesPerson().getName());
                            }%></td>
                        <td><%if (creditNote.getAppliedToInvoice() != null) {
                                out.print(creditNote.getAppliedToInvoice().getSalesConfirmationOrder().getSalesPerson().getStaffPrefix() + creditNote.getAppliedToInvoice().getSalesConfirmationOrder().getSalesConfirmationOrderNumber());
                            }%></td>
                        <td><%if (creditNote.getAppliedToInvoice() != null) {
                                out.print(creditNote.getAppliedToInvoice().getSalesConfirmationOrder().getCustomerPurchaseOrderNumber());
                            }%></td>
                        <td>
                            <%
                                if (creditNote.getAppliedToInvoice() != null) {
                                    out.print(creditNote.getAppliedToInvoice().getInvoiceNumber());
                                }
                            %>
                        </td>
                        <td><%=creditNote.getContactName()%></td>
                    </tr>
                </tbody>
            </table>

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>ITEM</h4></th>
                <th class='text-center'><h4>DESCRIPTION</h4></th>
                <th class='text-center'><h4>QTY</h4></th>
                <th class='text-center'><h4>UPRICE S$</h4> </th>
                <th class='text-center'><h4>TOTAL S$</h4></th>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            Item
                        </td>
                        <td>
                            Credit Given Off Invoice (<%if (creditNote.getAppliedToInvoice() != null) {
                                    out.print(creditNote.getAppliedToInvoice().getInvoiceNumber());
                                }%>)
                        </td>
                        <td class='text-center'>
                            1
                        </td>
                        <td>
                            <%=formatter.format(creditNote.getCreditAmount())%>
                        </td>
                        <td>
                            (<%=formatter.format(creditNote.getCreditAmount())%>)
                        </td>
                    </tr>
                </tbody>
            </table>

            <div class="row text-right">
                <div class="col-xs-7 text-left">
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
                            double taxRate = 7.0;
                            double totalTax = creditNote.getCreditAmount() * (taxRate / 100);
                            double subtotal = creditNote.getCreditAmount() - totalTax;
                            out.print(formatter.format(subtotal) + "<br>");
                            out.print(formatter.format(totalTax) + "<br>");
                            out.print(formatter.format(creditNote.getCreditAmount()));
                        %>
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