<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.util.Date"%>
<%@page import="EntityManager.SOALineItem"%>
<%@page import="java.util.ArrayList"%>
<%@page import="EntityManager.PaymentRecord"%>
<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    StatementOfAccount statementOfAccount = (StatementOfAccount) (session.getAttribute("statementOfAccount"));
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (statementOfAccount == null) {
        response.sendRedirect("statementOfAccounts.jsp?errMsg=An Error Occured.");
    } else {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
%>
<!doctype html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <title>Statement of Account</title>
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
                font-size: 12px;
                margin-top: 0;
                margin-bottom: 0;
            }
        </style>
    </head>

    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-6">
                    <h3>Aged Accounts Receivable</h3>
                    <p><%=statementOfAccount.getCustomer().getCustomerName()%></p>
                </div>
                <div class="col-xs-6 text-right">
                    <p></p>
                </div>
            </div>

            <br>

            <div  class="row">
                <div class="col-xs-6">
                    <div  class="row">
                        <div class="col-xs-12">
                            <p>Aged as of <%=statementOfAccount.getVersion().toLocalDateTime().format(DateTimeFormatter.ISO_DATE)%></p>
                        </div>
                    </div>
                </div>

                <div class="col-xs-2"><!--gap--></div>

                <div class="col-xs-2">
                    <p>
                        Above 90 days
                        <br>
                        61 to 90 days
                        <br>
                        31 to 60 days
                        <br>
                        0 to 30 days
                        <br>
                        Total Overdue
                    </p>
                </div>
                <div class="col-xs-2">
                    <%
                        if (statementOfAccount != null && statementOfAccount.getAmountOverDueOver91Days() != null) {
                            out.print(formatter.format(statementOfAccount.getAmountOverDueOver91Days()));
                        }
                    %>
                    <br>
                    <%
                        if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom61to90Days() != null) {
                            out.print(formatter.format(statementOfAccount.getAmountOverDueFrom61to90Days()));
                        }
                    %>
                    <br>
                    <%
                        if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom31to60Days() != null) {
                            out.print(formatter.format(statementOfAccount.getAmountOverDueFrom31to60Days()));
                        }
                    %>
                    <br>
                    <%
                        if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom0to30Days() != null) {
                            out.print(formatter.format(statementOfAccount.getAmountOverDueFrom0to30Days()));
                        }
                    %>
                    <br>
                    <%
                        if (statementOfAccount != null && statementOfAccount.getTotalAmountOverDue() != null) {
                            out.print(formatter.format(statementOfAccount.getTotalAmountOverDue()));
                        }
                    %>
                </div>
            </div>

            <br>
            <!-- / end client details section -->

            <table class="table table-bordered">
                <thead style="background: #eeece1;">
                <th class='text-center'><h4>Entry Date</h4></th>
                <th class='text-center'><h4>Ref. No</h4></th>
                <th class='text-center'><h4>Method</h4></th>
                <th class='text-center'><h4>Description</h4></th>
                <th class='text-center'><h4>Due Date</h4></th>
                <th class='text-right'><h4>Debit</h4></th>
                <th class='text-right'><h4>Credit</h4></th>
                <th class='text-right'><h4>Balance</h4></th>
                </thead>
                <tbody>
                    <%
                        List<SOALineItem> soali = statementOfAccount.getLineItem();
                        if (soali != null) {
                            for (int i = 0; i < soali.size(); i++) {
                                out.print("<tr>");

                                out.print("<td>");
                                if (soali.get(i).getEntryDate() != null) {
                                    String date = DATE_FORMAT.format(soali.get(i).getEntryDate());
                                    out.print(date);
                                }
                                out.print("</td>");

                                out.print("<td>" + soali.get(i).getReferenceNo() + "</td>");
                                out.print("<td>" + soali.get(i).getMethod() + "</td>");
                                out.print("<td>" + soali.get(i).getDescription() + "</td>");

                                out.print("<td>");
                                if (soali.get(i).getDueDate() != null) {
                                    String date = DATE_FORMAT.format(soali.get(i).getDueDate());
                                    out.print(date);
                                }
                                out.print("</td>");

                                out.print("<td class='text-right'>" + formatter.format(soali.get(i).getDebit()) + "</td>");
                                out.print("<td class='text-right'>" + formatter.format(soali.get(i).getCredit()) + "</td>");
                                out.print("<td class='text-right'>" + formatter.format(soali.get(i).getBalance()) + "</td>");

                                out.print("</tr>");
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>
    </body>
</html>
<%}%>