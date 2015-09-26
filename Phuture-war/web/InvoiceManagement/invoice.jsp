<%@page import="EntityManager.CreditNote"%>
<%@page import="EntityManager.PaymentRecord"%>
<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Invoice"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Contact"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    String previousMgtPage = (String) session.getAttribute("previousManagementPage");
    if (previousMgtPage == null) {
        previousMgtPage = "";
    }
    Invoice invoice = (Invoice) (session.getAttribute("invoice"));
    List<PaymentRecord> invoicePayments = (List<PaymentRecord>) (session.getAttribute("invoicePayments"));
    List<CreditNote> creditNotes = (List<CreditNote>) session.getAttribute("customerAvailableCreditNotes");

    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (invoice == null || creditNotes == null) {
        response.sendRedirect("invoices.jsp?errMsg=An error has occured.");
    } else {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        String estimatedDeliveryDate = request.getParameter("estimatedDeliveryDate");
        String editingLineItem = request.getParameter("editingLineItem");
        String formDisablerFlag = "";
        if (invoice != null && invoice.getStatus().equals("Voided")) {
            formDisablerFlag = "disabled";
            editingLineItem = "disabled";
        }
        if (editingLineItem == null) {
            editingLineItem = "";
        } else {//Disable unneccessary fields when editing line item
            formDisablerFlag = "disabled";
        }
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
                <% if (previousMgtPage.equals("soa")) {%>
                    window.location.href = "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=invoice.getSalesConfirmationOrder().getCustomer().getId()%>";
                <%} else {%>
                    window.location.href = "invoices.jsp";
                <%}%>
                }

                function back2() {
                    window.onbeforeunload = null;
                    window.location.href = "invoice.jsp";
                }

                function detachCR(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../PaymentManagementController?target=DetachCreditNote&invoiceID=" + id;
                }

                function addLineItemToExistingInvoice() {
                    window.onbeforeunload = null;
                    invoiceManagement.target.value = "UpdateInvoice";
                    invoiceManagement.source.value = "AddLineItemToExistingInvoice";
                    document.invoiceManagement.action = "../InvoiceManagementController";
                    document.invoiceManagement.submit();
                }

                function updateInvoice() {
                    window.onbeforeunload = null;
                    invoiceManagement.target.value = "UpdateInvoice";
                    document.invoiceManagement.action = "../InvoiceManagementController";
                    document.invoiceManagement.submit();
                }

                function editLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    invoiceManagement.lineItemID.value = lineItemID;
                    window.location.href = "invoice.jsp?editingLineItem=" + lineItemID;
                }

                function saveEditLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    invoiceManagement.lineItemID.value = lineItemID;
                    invoiceManagement.itemName.value = document.getElementById("itemName" + lineItemID).value;
                    invoiceManagement.itemDescription.value = document.getElementById("itemDescription" + lineItemID).value;
                    invoiceManagement.itemUnitPrice.value = document.getElementById("itemUnitPrice" + lineItemID).value;
                    invoiceManagement.itemQty.value = document.getElementById("itemQty" + lineItemID).value;
                    invoiceManagement.target.value = "EditLineItem";
                    document.invoiceManagement.action = "../InvoiceManagementController?editingLineItem=" + lineItemID;
                    document.invoiceManagement.submit();
                }

                function removeLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    invoiceManagement.lineItemID.value = lineItemID;
                    invoiceManagement.target.value = "RemoveLineItem";
                    document.invoiceManagement.action = "../InvoiceManagementController";
                    document.invoiceManagement.submit();
                }

                function voidInvoice() {
                    window.onbeforeunload = null;
                    window.location.href = "../InvoiceManagementController?target=VoidInvoice";
                }

                function addressBook() {
                    window.onbeforeunload = null;
                    editContactForm.target.value = "ListAllCustomer";
                    document.editContactForm.action = "../InvoiceManagementController";
                    document.editContactForm.submit();
                }

                function listAllPayment() {
                    window.onbeforeunload = null;
                    window.location.href = "../PaymentManagementController?target=ListPaymentTiedToInvoice";
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
                        <h2>Invoice <%if (invoice != null && invoice.getInvoiceNumber() != null) {
                                out.print(invoice.getInvoiceNumber());
                            }%></h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <%if (previousMgtPage.equals("sco")) {%>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=invoice.getSalesConfirmationOrder().getId()%>"><%=invoice.getSalesConfirmationOrder().getSalesPerson().getStaffPrefix()%><%=invoice.getSalesConfirmationOrder().getSalesConfirmationOrderNumber()%></a></span></li>
                                <li><span><a href= "invoices.jsp">Invoices</a></span></li>
                                            <%} else if (previousMgtPage.equals("invoices")) {%>
                                <li><span><a href= "../InvoiceManagementController?target=ListAllInvoice">Invoices</a></span></li>
                                            <%} else if (previousMgtPage.equals("soa")) {%>
                                <li><span><a href= "../StatementOfAccountManagementController?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span><a href= "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=invoice.getSalesConfirmationOrder().getCustomer().getId()%>"><%=invoice.getSalesConfirmationOrder().getCustomer().getCustomerName()%></a></span></span></li>
                                            <%}%>
                                <li><span>Invoice &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <form name="invoiceManagement" action="../InvoiceManagementController">
                        <section class="panel">
                            <div class="panel-body">
                                <div class="invoice">
                                    <header class="clearfix">
                                        <div class="row">
                                            <div class="col-sm-6 mt-md">
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Invoice <%=invoice.getInvoiceNumber()%></h2><br>
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
                                                                out.print("<b>" + invoice.getCustomerName() + "</b>");
                                                                String repl = invoice.getContactAddress().replaceAll("(\\r|\\n|\\r\\n)+", "<br>");
                                                                out.print("<br>" + repl);
                                                                out.print("<br>" + invoice.getContactOfficeNo());
                                                                if (invoice.getContactFaxNo() != null && !invoice.getContactFaxNo().isEmpty()) {
                                                                    out.print("<br>" + invoice.getContactFaxNo());
                                                                }
                                                                out.print("<p class='h5 mb-xs text-dark text-weight-semibold'>Attention:</p>");
                                                                out.print(invoice.getContactName() + " ");
                                                                if (invoice.getContactMobileNo() != null && !invoice.getContactMobileNo().isEmpty()) {
                                                                    out.print("<br>" + invoice.getContactMobileNo());
                                                                }
                                                                if (invoice.getContactEmail() != null && !invoice.getContactEmail().isEmpty()) {
                                                                    out.print("<br>" + invoice.getContactEmail() + "<br>");
                                                                }
                                                                if (!formDisablerFlag.equals("disabled")) {
                                                                    out.print("<div class='text-right'><a href='#modalEditForm' class='modal-with-form'>edit</a></div>");
                                                                }
                                                                out.print("<br><br>");
                                                            %>
                                                        </div>
                                                        <br/><br/>
                                                    </address>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="bill-data text-right">
                                                    <p class="mb-none">
                                                        <span class="text-dark">Salesperson: </span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (invoice.getSalesConfirmationOrder().getSalesPerson().getName() != null) {
                                                                    out.print(invoice.getSalesConfirmationOrder().getSalesPerson().getName());
                                                                } else {
                                                                    out.print(staff.getName());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Invoice Date</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <%
                                                                if (invoice.getDateSent() != null) {
                                                                    String date = DATE_FORMAT.format(invoice.getDateSent());
                                                                    out.print("<input " + formDisablerFlag + " id='invoiceSent' name='invoiceSent' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "' required>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Date Paid:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <%
                                                                if (invoice.getDatePaid() != null) {
                                                                    String date = DATE_FORMAT.format(invoice.getDatePaid());
                                                                    out.print("<input " + formDisablerFlag + " id='invoicePaid' name='invoicePaid' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "'>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='invoicePaid' name='invoicePaid' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' placeholder='dd/mm/yyyy'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Terms:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <select <%=formDisablerFlag%> id="terms" name="terms" class="form-control input-sm" required>
                                                                <%
                                                                    if (invoice.getTerms() != null) {
                                                                        if (invoice.getTerms() == 0) {
                                                                            out.print("<option value='0' selected>COD</option>");
                                                                            out.print("<option value='14'>14 Days</option>");
                                                                            out.print("<option value='30'>30 Days</option>");
                                                                        } else if (invoice.getTerms() == 14) {
                                                                            out.print("<option value='0'>COD</option>");
                                                                            out.print("<option value='14' selected>14 Days</op  tion>");
                                                                            out.print("<option value='30'>30 Days</option>");
                                                                        } else if (invoice.getTerms() == 30) {
                                                                            out.print("<option value='0'>COD</option>");
                                                                            out.print("<option value='14'>14 Days</option>");
                                                                            out.print("<option value='30' selected>30 Days</option>");
                                                                        }
                                                                    } else {
                                                                        out.print("<option value='0'>COD</option>");
                                                                        out.print("<option value='14'>14 Days</option>");
                                                                        out.print("<option value='30'>30 Days</option>");
                                                                    }
                                                                %>
                                                            </select>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">PO Number:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <%
                                                                if (invoice.getCustomerPurchaseOrderNumber() != null && !invoice.getCustomerPurchaseOrderNumber().isEmpty()) {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' value='" + invoice.getCustomerPurchaseOrderNumber() + "'>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' placeholder='PO Number'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Estimated Delivery Date:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <%
                                                                if (estimatedDeliveryDate != null && !estimatedDeliveryDate.isEmpty()) {
                                                                    String date = DATE_FORMAT.format(estimatedDeliveryDate);
                                                                    out.print("<input " + formDisablerFlag + " id='estimatedDeliveryDate' name='estimatedDeliveryDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "'>");
                                                                } else if (invoice.getEstimatedDeliveryDate() != null && invoice.getEstimatedDeliveryDate() != null) {
                                                                    String date = DATE_FORMAT.format(invoice.getEstimatedDeliveryDate());
                                                                    out.print("<input " + formDisablerFlag + " id='estimatedDeliveryDate' name='estimatedDeliveryDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "'>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='estimatedDeliveryDate' name='estimatedDeliveryDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' placeholder='dd/mm/yyyy'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Status: </span>
                                                        <span class="value" style="min-width: 110px; text-align: left;">
                                                            <%
                                                                if ((invoice.getStatus() != null && !invoice.getStatus().isEmpty())) {
                                                                    if (invoice.getStatus().equals("Paid")) {
                                                                        out.print("<span class='label label-success' style='display: block; font-size: 10.5pt;'>Paid</span>");
                                                                    } else {
                                                                        out.print("<span class='label label-info' style='display: block; font-size: 10.5pt;'>" + invoice.getStatus() + "</span>");
                                                                    }
                                                                }
                                                            %>
                                                        </span>

                                                    </p>

                                                    <% if (invoice.getDateDue() != null) {%>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Date Due: </span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%=DATE_FORMAT.format(invoice.getDateDue())%>
                                                        </span>
                                                    </p>
                                                    <%}%>
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
                                                    <th id="cell-qty" class="text-center text-weight-semibold">Quantity</th>
                                                    <th id="cell-price" class="text-center text-weight-semibold">Unit Price</th>
                                                    <th id="cell-total" class="text-center text-weight-semibold">Amount</th>
                                                    <th id="cell-total" class="text-center text-weight-semibold"></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr>
                                                    <td>
                                                        <%if (!editingLineItem.equals("")) {
                                                                out.println("<input type='text' class='form-control' name='itemName' disabled/>");
                                                            } else {
                                                                out.println("<input type='text' class='form-control' name='itemName'/>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td>
                                                        <%if (!editingLineItem.equals("")) {
                                                                out.println("<textarea class='form-control' rows='2' name='itemDescription' disabled></textarea>");
                                                            } else {
                                                                out.println("<textarea class='form-control' rows='2' name='itemDescription'></textarea>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td class="text-center">
                                                        <%if (!editingLineItem.equals("")) {
                                                                out.println("<input type='number' class='form-control' id='input_itemQty' min='0' name='itemQty' disabled/>");
                                                            } else {
                                                                out.println("<input type='number' class='form-control' id='input_itemQty' min='0' name='itemQty'/>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <%if (!editingLineItem.equals("")) {
                                                                    out.println("<input type='number' class='form-control' id='input_itemUnitPrice' name='itemUnitPrice' min='0' step='any' disabled/>");
                                                                } else {
                                                                    out.println("<input type='number' class='form-control' id='input_itemUnitPrice' name='itemUnitPrice' min='0' step='any'/>");
                                                                }
                                                            %>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type="text" class="form-control" id="input_itemAmount" name="itemAmount" disabled/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <%
                                                            if (!editingLineItem.equals("")) {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToNewInvoice()' disabled>Add Item</button>");
                                                            } else {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToExistingInvoice(" + invoice.getId() + ")'>Add Item</button>");
                                                            }
                                                        %>
                                                    </td>
                                                </tr>

                                                <!-- loop line item page -->
                                                <%
                                                    if (invoice.getItems() != null) {
                                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                        for (int i = 0; i < invoice.getItems().size(); i++) {
                                                            if (!editingLineItem.isEmpty() && editingLineItem.equals(invoice.getItems().get(i).getId() + "")) {
                                                                //Print editable fields
                                                                double price = invoice.getItems().get(i).getItemUnitPrice();

                                                %>
                                                <tr>
                                                    <td>
                                                        <input type='text' class='form-control' name='itemName' id='itemName<%=invoice.getItems().get(i).getId()%>' value='<%=invoice.getItems().get(i).getItemName()%>'/>
                                                    </td>
                                                    <td>
                                                        <textarea class='form-control' rows='5' name='itemDescription' id='itemDescription<%=invoice.getItems().get(i).getId()%>'><%=invoice.getItems().get(i).getItemDescription()%></textarea>
                                                    </td>
                                                    <td class="text-center">
                                                        <input type='number' class='form-control' id='itemQty<%=invoice.getItems().get(i).getId()%>' min='0' name='itemQty' value='<%=invoice.getItems().get(i).getItemQty()%>'/>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type='number' class='form-control' id='itemUnitPrice<%=invoice.getItems().get(i).getId()%>' name='itemUnitPrice' min='0' step='any' value='<%=price%>'/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type="text" class="form-control" id="input_itemAmount" name="itemAmount" disabled="" value=""/>
                                                        </div>
                                                    </td>
                                                    <% //Print buttons for current editing line item
                                                        out.print("<td class='text-center'><div class='btn-group'><button class='btn btn-default' type='button' onclick='javascript:saveEditLineItem(" + invoice.getItems().get(i).getId() + ")'>Save</button>&nbsp;");
                                                        out.print("<button class='btn btn-default' type='button' onclick='javascript:back2()' >Back</button></div></td>");
                                                    %>
                                                </tr> 
                                                <%
                                                            } else {
                                                                //Print normal text
                                                                double price = 0;
                                                                out.print("<tr>");
                                                                out.print("<td class='text-weight-semibold text-dark'>" + invoice.getItems().get(i).getItemName() + "</td>");
                                                                out.print("<td>" + invoice.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");
                                                                out.print("<td class='text-center'>" + invoice.getItems().get(i).getItemQty() + "</td>");
                                                                price = invoice.getItems().get(i).getItemUnitPrice();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                price = invoice.getItems().get(i).getItemUnitPrice() * invoice.getItems().get(i).getItemQty();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'><div class='btn-group'><button " + formDisablerFlag + " class='btn btn-default' type='button' onclick='javascript:editLineItem(" + invoice.getItems().get(i).getId() + ")'>Edit</button>&nbsp;");
                                                                out.print("<button " + formDisablerFlag + " class='btn btn-default' onclick='javascript:removeLineItem(" + invoice.getItems().get(i).getId() + ")'>Del</button></div></td>");
                                                                out.print("</div>");
                                                                out.print("</tr>");
                                                            }
                                                        }
                                                    }
                                                %>
                                                <!-- end loop line item page -->
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="invoice-summary" style="margin-top: 10px;">
                                        <div class="row">
                                            <div class="col-sm-5">
                                                <%
                                                    if (invoice.getRemarks() != null && !invoice.getRemarks().isEmpty()) {
                                                        out.print("REMARKS: ");
                                                        out.print(invoice.getRemarks().replaceAll("\\r", "<br>"));
                                                    }
                                                %>
                                            </div>
                                            <div class="col-sm-3"></div>
                                            <div class="col-sm-4">
                                                <table class="table h5 text-dark">
                                                    <tbody>
                                                        <tr class="b-top-none">
                                                            <td colspan="2">Subtotal</td>
                                                            <td class="text-left">
                                                                <%
                                                                    double formatedPrice = 0;
                                                                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                                    if (invoice == null) {
                                                                        out.print("<span id='output_subtotal'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = invoice.getTotalPriceBeforeCreditNote() / (invoice.getTaxRate() / 100 + 1);
                                                                        out.print("<span id='output_subtotal'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + (invoice.getTotalPriceBeforeCreditNote() / (invoice.getTaxRate() / 100 + 1)) + "' id='subtotal'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2">
                                                                <%
                                                                    if (invoice == null) {
                                                                        out.print("7.0% GST");
                                                                    } else {
                                                                        out.print("" + invoice.getTaxRate() + "% GST");
                                                                    }
                                                                %>
                                                            </td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (invoice == null) {
                                                                        out.print("<span id='output_gst'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = invoice.getTotalTax();
                                                                        out.print("<span id='output_gst'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + invoice.getTotalTax() + "' id='gst'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                        <tr class="h4">
                                                            <td colspan="2">Total (SGD)</td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (invoice == null) {
                                                                        out.print("<span id='output_totalPrice'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = invoice.getTotalPriceBeforeCreditNote();
                                                                        out.print("<span id='output_totalPrice'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + invoice.getTotalPriceBeforeCreditNote() + "' id='totalPrice'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-sm-6 mt-md">
                                        <div class="btn-group">
                                            <%
                                                if (invoice.getItems().size() > 0) {
                                                    out.print("<a href='invoice-print.jsp' target='_blank' class='btn btn-default'><i class='fa fa-print'></i> Print PDF</a>");
                                                }
                                                if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
                                                    out.print("<button type='button' class='btn btn-info modal-with-form' href='#modalNotes'>Notes</button>");
                                                } else {
                                                    out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalNotes'>Notes</button>");
                                                }
                                                out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalRemarks' data-toggle='tooltip' data-placement='top' title='*Remarks will be reflected in the Invoice'>Remarks</button>");
                                            %> 
                                        </div>
                                        &nbsp;
                                        <div class="btn-group">
                                            <%
                                                if (invoicePayments != null) {
                                                    if (invoicePayments.size() > 0) {
                                                        out.print("<button type='button' class='btn btn-default' onclick='javascript:listAllPayment(" + invoice.getId() + ")'>Payments <span class='badge' style='background-color:#0088CC'>" + invoice.getNumOfPaymentRecords() + "</span></button>");
                                                    }
                                                }
                                            %>         
                                        </div>
                                    </div>

                                    <div class="col-sm-6 text-right mt-md mb-md">
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                            <%if (!invoice.getStatus().equals("Voided")) {%>
                                            <button type='button' class='modal-with-move-anim btn btn-danger' href='#modalRemove'>Void Invoice</button>
                                            <%}%>
                                            <%
                                                if (invoice.getItems().size() > 0) {
                                                    if (invoice.getTotalCreditNoteAmount() > 0) {
                                                        out.print("<button " + formDisablerFlag + " type='button' class='btn btn-primary' onclick='javascript:detachCR(" + invoice.getId() + ")'>Detach Credit Note</button>");
                                                    } else {
                                                        out.print("<button " + formDisablerFlag + " type='button' class='btn btn-primary modal-with-form' href='#modalAttachCN'>Attach Credit Note</button>");
                                                    }
                                                    out.print("<button type='button' " + formDisablerFlag + " class='btn btn-primary modal-with-form' href='#modalAddPayment'>Add Payment</button>");
                                                }
                                            %>
                                            <button <%=formDisablerFlag%> class='btn btn-success' onclick='javascript:updateInvoice();'>Save</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            </div>
                        </section>

                        <input type='hidden' name='customerID' value='<%=invoice.getSalesConfirmationOrder().getCustomer().getId()%>'>
                        <input type="hidden" name="lineItemID" value="">   
                        <input type="hidden" name="target" value="SaveInvoice">    
                        <input type="hidden" name="source" value="">    
                        <input type="hidden" name="id" value="">    
                    </form>
                    <!-- end: page -->

                    <div id="modalAttachCN" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="attachCreditNoteForm" action="../PaymentManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Attach Credit Note to Invoice</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Credit Note Number</label>
                                        <div class="col-sm-8">
                                            <select class="form-control mb-md" name="creditNoteID">
                                                <%
                                                    for (int i = 0; i < creditNotes.size(); i++) {
                                                        out.print("<option value='" + creditNotes.get(i).getId() + "'>" + creditNotes.get(i).getCreditNoteNumber() + " (" + formatter.format(creditNotes.get(i).getCreditAmount()) + ")</option>");
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                    <input type="hidden" name="invoiceID" value="<%=invoice.getId()%>">
                                    <input type="hidden" name="target" value="AttachCreditNote">
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Attach Credit Note</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalAddPayment" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="addPaymentForm" action="../PaymentManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Add Payment to Invoice</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Amount <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="number" class="form-control" id="price" name="amount" min="0" step="0.01" size="4" title="CDA Currency Format - no dollar sign and no comma(s) - cents (.##) are optional" required/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Payment Date <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <input type="text" name="paymentDate" data-plugin-datepicker data-date-format="dd/mm/yyyy" class="form-control" placeholder="dd/mm/yyyy" required/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Payment Method</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="paymentMethod" class="form-control"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Payment Reference Number</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="paymentReferenceNumber" class="form-control"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Notes</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="notes"></textarea>
                                        </div>
                                    </div>

                                    <br>
                                    <input type="hidden" name="target" value="AddPayment">
                                    <input type="hidden" name="previousPage" value="invoice">
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Add Payment</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalEditForm" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editContactForm" action="../InvoiceManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Edit Contact</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Company <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="company" class="form-control" value="<%=invoice.getCustomerName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <textarea class="form-control" rows="3" name="address" required><%=invoice.getContactAddress()%></textarea>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Telephone <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="officeNo" class="form-control" value="<%=invoice.getContactOfficeNo()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Fasimile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="faxNo" class="form-control" value="<%=invoice.getContactFaxNo()%>"/>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="form-group mt-lg">
                                        <label class="col-sm-3 control-label">Name <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="name" class="form-control" value="<%=invoice.getContactName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Mobile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="mobileNo" class="form-control" value="<%=invoice.getContactMobileNo()%>"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Email <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="email" name="email" class="form-control" value="<%=invoice.getContactEmail()%>" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="UpdateInvoiceContact">    
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Save</button>
                                            <button class="btn btn-primary" onclick="javascript:addressBook()">Address Book</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalNotes" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editNotesForm" action="../InvoiceManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Notes</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Notes</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="notes"><%if (invoice.getNotes() != null) {
                                                    out.print(invoice.getNotes());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdateInvoiceNotes">    
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button <%=formDisablerFlag%> class="btn btn-success" type="submit">Save</button>
                                            <button <%=formDisablerFlag%> class="btn btn-default" type="reset">Clear</button>
                                            <button class="btn btn-default modal-dismiss">Close</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalRemarks" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editRemarksForm" action="../InvoiceManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Remarks</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Remarks</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="remarks"><%if (invoice.getRemarks() != null) {
                                                    out.print(invoice.getRemarks());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdateInvoiceRemarks">    
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button <%=formDisablerFlag%> class="btn btn-success" type="submit">Save</button>
                                            <button <%=formDisablerFlag%> class="btn btn-default" type="reset">Clear</button>
                                            <button class="btn btn-default modal-dismiss">Close</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>      

                    <div id="modalRemove" class="zoom-anim-dialog modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <header class="panel-heading">
                                <h2 class="panel-title">Are you sure?</h2>
                            </header>
                            <div class="panel-body" style="padding-top: 0px;">
                                <div class="modal-wrapper">
                                    <div class="modal-icon">
                                        <i class="fa fa-question-circle" style="top: 0px;"></i>
                                    </div>
                                    <div class="modal-text">
                                        <p>Are you sure that you want to void this Invoice?<br>All associated payment records will also be deleted.<br>Any attached credit notes will also be voided.<br>This action is not reversible!</p>
                                    </div>
                                </div>
                            </div>
                            <footer class="panel-footer">
                                <div class="row">
                                    <div class="col-md-12 text-right">
                                        <button class="btn btn-primary modal-confirm" onclick="javascript:voidInvoice();">Confirm</button>
                                        <button class="btn btn-default modal-dismiss">Cancel</button>
                                    </div>
                                </div>
                            </footer>
                        </section>
                    </div>

                </section>
            </div>
        </section>
        <jsp:include page="../jspIncludePages/foot.html" />
    </body>
</html>
<%}%>

