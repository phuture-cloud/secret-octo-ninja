<%@page import="EntityManager.SOALineItem"%>
<%@page import="EntityManager.StatementOfAccount"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Contact"%>
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
        response.sendRedirect("../PaymentManagement/statementOfAccounts.jsp?errMsg=An Error has occured.");
    } else {
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <section class="body">
            <script>
                $(document).ready(function () {
                    $('#input_itemQty, #input_itemUnitPrice').change(function () {
                        var itemUnitPrice = parseFloat($('#input_itemUnitPrice').val());
                        var itemQty = parseInt($('#input_itemQty').val());
                        var itemAmount = itemUnitPrice * itemQty;
                        var subtotal = parseFloat($('#subtotal').val());
                        var gst = parseFloat($('#gst').val());
                        var totalPrice = parseFloat($('#totalPrice').val());

                        if (!isNaN(itemAmount)) {
                            if (isNaN(subtotal)) {
                                subtotal = 0;
                            }
                            if (isNaN(gst)) {
                                gst = 0;
                            }
                            if (isNaN(totalPrice)) {
                                totalPrice = 0;
                            }

                            var newSubtotal = subtotal + itemAmount;
                            var newGst = newSubtotal * 0.07;
                            var newTotalPrice = newSubtotal + newGst;

                            $('#input_itemAmount').val(itemAmount.toFixed(2));
                            $('#output_subtotal').text("$" + newSubtotal.toFixed(2));
                            $('#output_gst').text("$" + newGst.toFixed(2));
                            $('#output_totalPrice').text("$" + newTotalPrice.toFixed(2));
                        }

                        if (isNaN(itemUnitPrice) || isNaN(itemQty)) {
                            $('#input_itemAmount').val("");
                            $('#output_subtotal').text("$" + subtotal.toFixed(2));
                            $('#output_gst').text("$" + gst.toFixed(2));
                            $('#output_totalPrice').text("$" + totalPrice.toFixed(2));
                        }
                    });
                });

                function back() {
                    window.onbeforeunload = null;
                    window.location.href = "scoManagement_DO.jsp";
                }

                function back2() {
                    window.onbeforeunload = null;
                    window.location.href = "doManagement.jsp";
                }

                function addLineItemToExistingDO() {
                    window.onbeforeunload = null;
                    doManagement.target.value = "UpdateDO";
                    doManagement.source.value = "AddLineItemToExistingDO";
                    document.doManagement.action = "../DeliveryOrderManagementController";
                    document.doManagement.submit();
                }

                function updateDO() {
                    window.onbeforeunload = null;
                    doManagement.target.value = "UpdateDO";
                    document.doManagement.action = "../DeliveryOrderManagementController";
                    document.doManagement.submit();
                }

                function editLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.lineItemID.value = lineItemID;
                    window.location.href = "doManagement.jsp?editingLineItem=" + lineItemID;
                }

                function saveEditLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.lineItemID.value = lineItemID;
                    doManagement.itemName.value = document.getElementById("itemName" + lineItemID).value;
                    doManagement.itemDescription.value = document.getElementById("itemDescription" + lineItemID).value;
                    doManagement.itemUnitPrice.value = document.getElementById("itemUnitPrice" + lineItemID).value;
                    doManagement.itemQty.value = document.getElementById("itemQty" + lineItemID).value;
                    doManagement.target.value = "EditLineItem";
                    document.doManagement.action = "../DeliveryOrderManagementController?editingLineItem=" + lineItemID;
                    document.doManagement.submit();
                }

                function removeLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.lineItemID.value = lineItemID;
                    doManagement.target.value = "RemoveLineItem";
                    document.doManagement.action = "../DeliveryOrderManagementController";
                    document.doManagement.submit();
                }

                function deleteDO() {
                    window.onbeforeunload = null;
                    window.location.href = "../DeliveryOrderManagementController?target=DeleteDO";
                }

                function addressBook() {
                    window.onbeforeunload = null;
                    editContactForm.target.value = "ListAllCustomer";
                    document.editContactForm.action = "../DeliveryOrderManagementController";
                    document.editContactForm.submit();
                }

                function listAllInvoices(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../OrderManagementController?target=RetrieveSCO&source=listAllInvoices&id=" + id;
                }

                window.onbeforeunload = function () {
                    return 'There may be unsaved changes to this page. If you continue, you will lose them.';
                };

                $(function () {
                    $('button[type=submit]').click(function (e) {
                        window.onbeforeunload = null;
                    });
                });
            </script>
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Statement of Account</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../PaymentManagement?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span>SOA &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <form name="doManagement" action="../DeliveryOrderManagementController">
                        <section class="panel">
                            <div class="panel-body">
                                <div class="invoice">
                                    <header class="clearfix">
                                        <div class="row">
                                            <div class="col-sm-6 mt-md">
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Statement of Account</h2>
                                                <br><h3><%=statementOfAccount.getCustomer().getCustomerName()%></h3>
                                            </div>
                                            <br/>
                                            <div class="col-sm-6 text-right mt-md mb-md">
                                                <address class="ib mr-xlg">
                                                    Phuture International Ltd
                                                    <br/>
                                                    28 Sin Ming Lane, #06-145 Midview City S(573972)
                                                    <br/>
                                                    Phone: (65) 6842 0198
                                                    <br/>
                                                    Fax: (65) 6285 6753
                                                </address>
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
                                                        </div>
                                                        <br/><br/>
                                                    </address>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="bill-data text-right">
                                                    <p class="mb-none">
                                                        <span class="text-dark">Total Overdue </span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (statementOfAccount != null && statementOfAccount.getTotalAmountOverDue() != null) {
                                                                    out.print(statementOfAccount.getTotalAmountOverDue());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">0 to 30 days</span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom0to30Days() != null) {
                                                                    out.print(statementOfAccount.getAmountOverDueFrom0to30Days());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">31 to 60 days</span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom31to60Days() != null) {
                                                                    out.print(statementOfAccount.getAmountOverDueFrom31to60Days());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">61 to 90 days</span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (statementOfAccount != null && statementOfAccount.getAmountOverDueFrom61to90Days() != null) {
                                                                    out.print(statementOfAccount.getAmountOverDueFrom61to90Days());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Above 90 days</span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (statementOfAccount != null && statementOfAccount.getAmountOverDueOver91Days() != null) {
                                                                    out.print(statementOfAccount.getAmountOverDueOver91Days());
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
                                                    <th id="cell-item" class="text-weight-semibold">Entry Date</th>
                                                    <th class="text-weight-semibold">Reference No</th>
                                                    <th class="text-center text-weight-semibold">Payment Method</th>
                                                    <th class="text-center text-weight-semibold">Description</th>
                                                    <th class="text-center text-weight-semibold">Due Date</th>
                                                    <th id="cell-price" class="text-center text-weight-semibold">Debit</th>
                                                    <th id="cell-price" class="text-center text-weight-semibold">Credit</th>
                                                    <th class="text-center text-weight-semibold">Balance</th>
                                                    <th></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <%
                                                    List<SOALineItem> soali = statementOfAccount.getLineItem();
                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                    if (soali != null) {
                                                        for (int i = 0; i < soali.size(); i++) {
                                                %>
                                                <tr>        

                                                    <td>
                                                        <%
                                                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                            String date = DATE_FORMAT.format(soali.get(i).getEntryDate());
                                                            out.print(date);
                                                        %>
                                                    </td>
                                                    <td><%=soali.get(i).getReferenceNo()%></td>
                                                    <td><%=soali.get(i).getMethod()%></td>
                                                    <td><%=soali.get(i).getDescription()%></td>
                                                    <td>
                                                        <%
                                                            DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                            date = DATE_FORMAT.format(soali.get(i).getDueDate());
                                                            out.print(date);
                                                        %>
                                                    </td>
                                                    <td><%=formatter.format(soali.get(i).getDebit())%></td>
                                                    <td><%=formatter.format(soali.get(i).getCredit())%></td>
                                                    <td><%=formatter.format(soali.get(i).getBalance())%></td>
                                                    <%if (soali.get(i).getScoID() != null) {%>
                                                    <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewSCO('<%=soali.get(i).getScoID()%>');">View SCO</button></td>
                                                    <%} else if (soali.get(i).getInvoiceID() != null) {%>
                                                    <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewInvoice('<%=soali.get(i).getInvoiceID()%>');">View Invoice</button></td>
                                                    <%} else if (soali.get(i).getPaymentID() != null) {%>
                                                    <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewPayment('<%=soali.get(i).getPaymentID()%>');">View Payment</button></td>
                                                    <%}%>
                                                </tr>
                                                <%
                                                        }
                                                    }
                                                %>

                                            </tbody>
                                        </table>
                                    </div>


                                </div>

                                <div class="row">
                                    <div class="col-sm-6 mt-md">
                                        <div class="btn-group">
                                            <%
                                                if (statementOfAccount != null) {
                                                    out.print("<a href='../OrderManagementController?target=PrintPDF&id=" + statementOfAccount.getId() + "' target='_blank' class='btn btn-default'><i class='fa fa-print'></i> Print PDF</a>");
                                                }
                                            %> 
                                        </div>
                                    </div>

                                    <div class="col-sm-6 text-right mt-md mb-md">
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </form>
                    <!-- end: page -->
                </section>
            </div>
        </section>
        <jsp:include page="../jspIncludePages/foot.html" />
    </body>
</html>
<%}%>

