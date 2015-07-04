<%@page import="EntityManager.DeliveryOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Contact"%>
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
    } else {
        String editingLineItem = request.getParameter("editingLineItem");
        String formDisablerFlag = "";
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
                    window.location.href = "scoManagement_DO.jsp";
                }

                function back2(id) {
                    window.onbeforeunload = null;
                    var doNumber = document.getElementById("doNumber").value;
                    var doDate = document.getElementById("doDate").value;
                    var terms = document.getElementById("terms").value;
                    var status = document.getElementById("status").value;
                    window.location.href = "scoManagement_add.jsp?id=" + id + "&status=" + status + "&doNumber=" + doNumber + "&doDate=" + doDate + "&terms=" + terms;
                }

                function addLineItemToNewDO() {
                    window.onbeforeunload = null;
                    doManagement.target.value = "SaveSCO";
                    doManagement.source.value = "AddLineItemToNewSCO";
                    document.doManagement.action = "../OrderManagementController";
                    document.doManagement.submit();
                }

                function saveDO() {
                    window.onbeforeunload = null;
                    doManagement.target.value = "SaveSCO";
                    document.doManagement.action = "../OrderManagementController";
                    document.doManagement.submit();
                }

                function addLineItemToExistingDO(id) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.target.value = "UpdateSCO";
                    doManagement.source.value = "AddLineItemToExistingSCO";
                    document.doManagement.action = "../OrderManagementController";
                    document.doManagement.submit();
                }

                function updateDO(id) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.target.value = "UpdateSCO";
                    document.doManagement.action = "../OrderManagementController";
                    document.doManagement.submit();
                }

                function editLineItem(id, lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.lineItemID.value = lineItemID;
                    var doNumber = document.getElementById("doNumber").value;
                    var doDate = document.getElementById("doDate").value;
                    var terms = document.getElementById("terms").value;
                    var status = document.getElementById("status").value;
                    var poNumber = document.getElementById("poNumber").value;
                    window.location.href = "scoManagement_add.jsp?id=" + id + "&doNumber=" + doNumber + "&doDate=" + doDate + "&terms=" + terms + "&status=" + status + "&editingLineItem=" + lineItemID + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&poNumber=" + poNumber;
                }

                function saveEditLineItem(id, lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.lineItemID.value = lineItemID;
                    doManagement.itemName.value = document.getElementById("itemName" + lineItemID).value;
                    doManagement.itemDescription.value = document.getElementById("itemDescription" + lineItemID).value;
                    doManagement.itemUnitPrice.value = document.getElementById("itemUnitPrice" + lineItemID).value;
                    doManagement.itemQty.value = document.getElementById("itemQty" + lineItemID).value;
                    doManagement.target.value = "EditLineItem";
                    doManagement.doNumber.value = document.getElementById("doNumber").value;
                    document.doManagement.action = "../OrderManagementController?editingLineItem=" + lineItemID + "&doNumber=" + doNumber;
                    document.doManagement.submit();
                }

                function removeLineItemSubmit(id, lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.lineItemID.value = lineItemID;
                    doManagement.target.value = "RemoveLineItem";
                    document.doManagement.action = "../OrderManagementController";
                    document.doManagement.submit();
                }

                function removeLineItem(id, lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.lineItemID.value = lineItemID;
                    doManagement.target.value = "RemoveLineItem";
                    document.doManagement.action = "../OrderManagementController";
                    document.doManagement.submit();
                }

                function deleteDO(id) {
                    window.onbeforeunload = null;
                    doManagement.id.value = id;
                    doManagement.target.value = "DeleteDO";
                    document.doManagement.action = "../DeliveryOrderManagementController";
                    document.doManagement.submit();
                }

                function addressBook() {
                    window.onbeforeunload = null;
                    editContactForm.target.value = "ListAllCustomer";
                    document.editContactForm.action = "../OrderManagementController";
                    document.editContactForm.submit();
                }

                function listAllInvoices(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../OrderManagementController?target=RetrieveSCO&source=listAllInvoices&id=" + id;
                }

                function listAllPO(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../OrderManagementController?target=RetrieveSCO&source=listAllPO&id=" + id;
                }

                function listAllDO(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../OrderManagementController?target=RetrieveSCO&source=listAllDO&id=" + id;
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
                        <h2>Delivery Order</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">DO Management</a></span></li>
                                <li><span>DO &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <form name="doManagement" action="../OrderManagementController">
                        <section class="panel">
                            <div class="panel-body">
                                <div class="invoice">
                                    <header class="clearfix">
                                        <div class="row">
                                            <div class="col-sm-6 mt-md">
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Delivery No.</h2>
                                                <%
                                                    if (deliveryOrder != null) {
                                                        out.print("<input type='text' class='form-control' id='doNumber' name='doNumber' value='" + deliveryOrder.getDeliveryOrderNumber() + "' style='max-width: 300px' required/>");
                                                    }
                                                %>
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
                                                            <%
                                                                if (deliveryOrder != null) {
                                                                    out.print("<b>" + deliveryOrder.getCustomerName() + "</b>");
                                                                    String repl = deliveryOrder.getContactAddress().replaceAll("(\\r|\\n|\\r\\n)+", "<br>");
                                                                    out.print("<br>" + repl);
                                                                    out.print("<br>" + deliveryOrder.getContactOfficeNo());
                                                                    if (deliveryOrder.getContactFaxNo() != null && !deliveryOrder.getContactFaxNo().isEmpty()) {
                                                                        out.print("<br>" + deliveryOrder.getContactFaxNo());
                                                                    }
                                                                    out.print("<p class='h5 mb-xs text-dark text-weight-semibold'>Attention:</p>");
                                                                    out.print(deliveryOrder.getContactName() + " ");
                                                                    if (deliveryOrder.getContactMobileNo() != null && !deliveryOrder.getContactMobileNo().isEmpty()) {
                                                                        out.print("<br>" + deliveryOrder.getContactMobileNo());
                                                                    }
                                                                    if (deliveryOrder.getContactEmail() != null && !deliveryOrder.getContactEmail().isEmpty()) {
                                                                        out.print("<br>" + deliveryOrder.getContactEmail() + "<br>");
                                                                    }
                                                                    if (!formDisablerFlag.equals("disabled")) {
                                                                        out.print("<div class='text-right'><a href='#modalEditForm' class='modal-with-form'>edit</a></div>");
                                                                    }
                                                                    out.print("<br><br>");
                                                                }
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
                                                                if (deliveryOrder != null && deliveryOrder.getSalesConfirmationOrder().getSalesPerson().getName() != null) {
                                                                    out.print(deliveryOrder.getSalesConfirmationOrder().getSalesPerson().getName());
                                                                } else {
                                                                    out.print(staff.getName());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Date:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <%
                                                                if (deliveryOrder != null) {
                                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
                                                                    String date = DATE_FORMAT.format(deliveryOrder.getDeliveryOrderDate());
                                                                    out.print("<input " + formDisablerFlag + " id='doDate' name='doDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "' required>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='doDate' name='doDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' required placeholder='dd/mm/yyyy'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>


                                                    <p class="mb-none">
                                                        <span class="text-dark">PO Number:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <%
                                                                if (deliveryOrder != null) {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' value='" + deliveryOrder.getCustomerPurchaseOrderNumber() + "'>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' placeholder='PO Number'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>


                                                    <% if (deliveryOrder != null) {%>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Status: </span>
                                                        <span class="value" style="min-width: 110px">
                                                            <select <%=formDisablerFlag%> id="status" name="status" class="form-control input-sm" required>
                                                                <%
                                                                    if ((deliveryOrder.getStatus() != null && !deliveryOrder.getStatus().isEmpty())) {
                                                                        String selectedStatus = deliveryOrder.getStatus();

                                                                        if (selectedStatus.equals("Created")) {
                                                                            out.print("<option value='Created' selected>Created</option>");
                                                                            out.print("<option value='Shipped'>Shipped</option>");
                                                                            out.print("<option value='Delivered'>Delivered</option>");
                                                                        } else if (selectedStatus.equals("Shipped")) {
                                                                            out.print("<option value='Created'>Created</option>");
                                                                            out.print("<option value='Shipped' selected>Shipped</option>");
                                                                            out.print("<option value='Delivered'>Delivered</option>");
                                                                        } else if (selectedStatus.equals("Delivered")) {
                                                                            out.print("<option value='Created'>Created</option>");
                                                                            out.print("<option value='Shipped'>Shipped</option>");
                                                                            out.print("<option value='Delivered' selected>Delivered</option>");
                                                                        } else {
                                                                            out.print("<option value='Created'>Created</option>");
                                                                            out.print("<option value='Shipped'>Shipped</option>");
                                                                            out.print("<option value='Delivered'>Delivered</option>");
                                                                        }
                                                                    }
                                                                %>
                                                            </select>
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
                                                    <th id="cell-price" class="text-center text-weight-semibold">Unit Price</th>
                                                    <th id="cell-qty" class="text-center text-weight-semibold">Quantity</th>
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
                                                                out.println("<input type='text' class='form-control' name='itemDescription' disabled/>");
                                                            } else {
                                                                out.println("<input type='text' class='form-control' name='itemDescription'/>");
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
                                                            <input type="text" class="form-control" id="input_itemAmount" name="itemAmount" disabled/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <%
                                                            if (!editingLineItem.equals("")) {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToNewDO()' disabled>Add Item</button>");
                                                            } else {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToExistingDO(" + deliveryOrder.getId() + ")'>Add Item</button>");
                                                            }
                                                        %>
                                                    </td>
                                                </tr>

                                                <!-- loop line item page -->
                                                <%
                                                    if (deliveryOrder != null && deliveryOrder.getItems() != null) {
                                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                        for (int i = 0; i < deliveryOrder.getItems().size(); i++) {
                                                            if (!editingLineItem.isEmpty() && editingLineItem.equals(deliveryOrder.getItems().get(i).getId() + "")) {
                                                                //Print editable fields
                                                                double price = deliveryOrder.getItems().get(i).getItemUnitPrice();

                                                %>
                                                <tr>
                                                    <td>
                                                        <input type='text' class='form-control' name='itemName' id='itemName<%=deliveryOrder.getItems().get(i).getId()%>' value='<%=deliveryOrder.getItems().get(i).getItemName()%>'/>
                                                    </td>
                                                    <td>
                                                        <input type='text' class='form-control' name='itemDescription' id='itemDescription<%=deliveryOrder.getItems().get(i).getId()%>' value='<%=deliveryOrder.getItems().get(i).getItemDescription()%>'/>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type='number' class='form-control' id='itemUnitPrice<%=deliveryOrder.getItems().get(i).getId()%>' name='itemUnitPrice' min='0' step='any' value='<%=price%>'/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <input type='number' class='form-control' id='itemQty<%=deliveryOrder.getItems().get(i).getId()%>' min='0' name='itemQty' value='<%=deliveryOrder.getItems().get(i).getItemQty()%>'/>
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
                                                        out.print("<td class='text-center'><div class='btn-group'><button class='btn btn-default' type='button' onclick='javascript:saveEditLineItem(" + deliveryOrder.getId() + "," + deliveryOrder.getItems().get(i).getId() + ")'>Save</button>&nbsp;");
                                                        out.print("<button class='btn btn-default' type='button' onclick='javascript:back2(" + deliveryOrder.getId() + ")' >Back</button></div></td>");
                                                    %>
                                                </tr> 
                                                <%
                                                            } else {
                                                                //Print normal text
                                                                double price = 0;
                                                                out.print("<tr>");
                                                                out.print("<td class='text-weight-semibold text-dark'>" + deliveryOrder.getItems().get(i).getItemName() + "</td>");
                                                                out.print("<td>" + deliveryOrder.getItems().get(i).getItemDescription() + "</td>");
                                                                price = deliveryOrder.getItems().get(i).getItemUnitPrice();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'>" + deliveryOrder.getItems().get(i).getItemQty() + "</td>");
                                                                price = deliveryOrder.getItems().get(i).getItemUnitPrice() * deliveryOrder.getItems().get(i).getItemQty();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'><div class='btn-group'><button " + formDisablerFlag + " class='btn btn-default' type='button' onclick='javascript:editLineItem(" + deliveryOrder.getId() + "," + deliveryOrder.getItems().get(i).getId() + ")'>Edit</button>&nbsp;");
                                                                out.print("<button " + formDisablerFlag + " class='btn btn-default' onclick='javascript:removeLineItem(" + deliveryOrder.getId() + "," + deliveryOrder.getItems().get(i).getId() + ")'>Del</button></div></td>");
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
                                                Terms & Conditions
                                                <ul>
                                                    <li>All Goods Delivered Are Non Returnable / Refundable</li>
                                                </ul>
                                                <%
                                                    if (deliveryOrder != null && deliveryOrder.getRemarks() != null && !deliveryOrder.getRemarks().isEmpty()) {
                                                        out.print("Remarks:");
                                                        String repl = deliveryOrder.getRemarks().replaceAll("(\\r|\\n|\\r\\n)+", "<br>");
                                                        out.print("Remarks: " + deliveryOrder.getRemarks());
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
                                                                    if (deliveryOrder == null) {
                                                                        out.print("<span id='output_subtotal'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = deliveryOrder.getTotalPrice() / (deliveryOrder.getTaxRate() / 100 + 1);
                                                                        out.print("<span id='output_subtotal'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + (deliveryOrder.getTotalPrice() / (deliveryOrder.getTaxRate() / 100 + 1)) + "' id='subtotal'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2">
                                                                <%
                                                                    if (deliveryOrder == null) {
                                                                        out.print("7.0% GST");
                                                                    } else {
                                                                        out.print("" + deliveryOrder.getTaxRate() + "% GST");
                                                                    }
                                                                %>
                                                            </td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (deliveryOrder == null) {
                                                                        out.print("<span id='output_gst'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = deliveryOrder.getTotalTax();
                                                                        out.print("<span id='output_gst'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + deliveryOrder.getTotalTax() + "' id='gst'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                        <tr class="h4">
                                                            <td colspan="2">Total (SGD)</td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (deliveryOrder == null) {
                                                                        out.print("<span id='output_totalPrice'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = deliveryOrder.getTotalPrice();
                                                                        out.print("<span id='output_totalPrice'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + deliveryOrder.getTotalPrice() + "' id='totalPrice'>");
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
                                                if (deliveryOrder != null) {
                                                    if (deliveryOrder.getItems().size() > 0) {
                                                        out.print("<a href='../OrderManagementController?target=PrintPDF&id=" + deliveryOrder.getId() + "' target='_blank' class='btn btn-default'><i class='fa fa-print'></i> Print PDF</a>");
                                                    }
                                                    if (deliveryOrder.getNotes() != null && !deliveryOrder.getNotes().isEmpty()) {
                                                        out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalNotes'><i class='fa fa-exclamation'></i> Notes</button>");
                                                    } else {
                                                        out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalNotes'>Notes</button>");
                                                    }
                                                    out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalRemarks' data-toggle='tooltip' data-placement='top' title='*Remarks will be reflected in the DO'>Remarks</button>");
                                                }
                                            %> 
                                        </div>
                                    </div>

                                    <div class="col-sm-6 text-right mt-md mb-md">
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                            <%              if (deliveryOrder != null) {
                                                    out.print("<button type='button' class='modal-with-move-anim btn btn-danger' href='#modalRemove'>Delete</button>");
                                                    if (deliveryOrder.getItems().size() > 0) {
                                                        out.print("<button " + formDisablerFlag + " class='btn btn-primary' onclick='javascript:generateInvoice()'>Generate Invoice</button>");
                                                    }
                                                    out.print("<button " + formDisablerFlag + " class='btn btn-success' onclick='javascript:updateDO(" + deliveryOrder.getId() + ")'>Save</button>");
                                                } else {
                                                    out.print("<button " + formDisablerFlag + " class='btn btn-success' type='submit'>Save</button>");
                                                }
                                            %>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>

                        <%
                            if (deliveryOrder != null) {
                                out.print("<input type='hidden' name='customerID' value='" + deliveryOrder.getSalesConfirmationOrder().getCustomer().getId() + "'>");
                            }
                        %>
                        <input type="hidden" name="lineItemID" value="">   
                        <input type="hidden" name="target" value="SaveDO">    
                        <input type="hidden" name="source" value="">    
                        <input type="hidden" name="id" value="">    
                    </form>
                    <!-- end: page -->

                    <%if (deliveryOrder != null) {%>
                    <div id="modalEditForm" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editContactForm" action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Edit Contact</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Company <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="company" class="form-control" value="<%=deliveryOrder.getCustomerName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <textarea class="form-control" rows="3" name="address" required><%=deliveryOrder.getContactAddress()%></textarea>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Telephone <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="officeNo" class="form-control" value="<%=deliveryOrder.getContactOfficeNo()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Fasimile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="faxNo" class="form-control" value="<%=deliveryOrder.getContactFaxNo()%>"/>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="form-group mt-lg">
                                        <label class="col-sm-3 control-label">Name <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="name" class="form-control" value="<%=deliveryOrder.getContactName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Mobile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="mobileNo" class="form-control" value="<%=deliveryOrder.getContactMobileNo()%>"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Email <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="email" name="email" class="form-control" value="<%=deliveryOrder.getContactEmail()%>" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="UpdateDOContact">    
                                    <input type="hidden" name="id" value="<%=deliveryOrder.getId()%>">  
                                    <input type="hidden" name="source" value="addressBook"> 
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Save</button>
                                            <button class="btn btn-primary" onclick="javascript:addressBook(<%=deliveryOrder.getId()%>)">Address Book</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalNotes" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editNotesForm" action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Notes</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Notes</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="notes"><%if (deliveryOrder.getNotes() != null) {
                                                    out.print(deliveryOrder.getNotes());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdateDONotes">    
                                    <input type="hidden" name="id" value="<%=deliveryOrder.getId()%>">  
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Save</button>
                                            <button class="btn btn-default" type="reset">Clear</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalRemarks" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editRemarksForm" action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Remarks</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Remarks</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="remarks"><%if (deliveryOrder.getRemarks() != null) {
                                                    out.print(deliveryOrder.getRemarks());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdateDORemarks">    
                                    <input type="hidden" name="id" value="<%=deliveryOrder.getId()%>">  
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Save</button>
                                            <button class="btn btn-default" type="reset">Clear</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
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
                            <div class="panel-body">
                                <div class="modal-wrapper">
                                    <div class="modal-icon">
                                        <i class="fa fa-question-circle"></i>
                                    </div>
                                    <div class="modal-text">
                                        <p>Are you sure that you want to delete this Delivery Order?<br> All associated Invoice/Payment records will also be deleted together!</p>
                                    </div>
                                </div>
                            </div>
                            <footer class="panel-footer">
                                <div class="row">
                                    <div class="col-md-12 text-right">
                                        <button class="btn btn-primary modal-confirm" onclick="deleteDO(<%=deliveryOrder.getId()%>)">Confirm</button>
                                        <button class="btn btn-default modal-dismiss">Cancel</button>
                                    </div>
                                </div>
                            </footer>
                        </section>
                    </div>
                    <%}%>
                </section>
            </div>
        </section>
        <jsp:include page="../jspIncludePages/foot.html" />
    </body>
</html>
<%}%>

