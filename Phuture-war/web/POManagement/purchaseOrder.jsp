<%@page import="EntityManager.PurchaseOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.Contact"%>
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
        response.sendRedirect("purchaseOrders.jsp?errMsg=An error has occured.");
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
                    window.location.href = "purchaseOrders.jsp";
                }

                function back2() {
                    window.onbeforeunload = null;
                    window.location.href = "purchaseOrder.jsp";
                }

                function addLineItemToExistingPO() {
                    window.onbeforeunload = null;
                    poManagement.target.value = "UpdatePO";
                    poManagement.source.value = "AddLineItemToExistingPO";
                    document.poManagement.action = "../PurchaseOrderManagementController";
                    document.poManagement.submit();
                }

                function updatePO() {
                    window.onbeforeunload = null;
                    poManagement.target.value = "UpdatePO";
                    document.poManagement.action = "../PurchaseOrderManagementController";
                    document.poManagement.submit();
                }

                function editLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    poManagement.lineItemID.value = lineItemID;
                    window.location.href = "purchaseOrder.jsp?editingLineItem=" + lineItemID;
                }

                function saveEditLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    poManagement.lineItemID.value = lineItemID;
                    poManagement.itemName.value = document.getElementById("itemName" + lineItemID).value;
                    poManagement.itemDescription.value = document.getElementById("itemDescription" + lineItemID).value;
                    poManagement.itemUnitPrice.value = document.getElementById("itemUnitPrice" + lineItemID).value;
                    poManagement.itemQty.value = document.getElementById("itemQty" + lineItemID).value;
                    poManagement.target.value = "EditLineItem";
                    document.poManagement.action = "../PurchaseOrderManagementController?editingLineItem=" + lineItemID;
                    document.poManagement.submit();
                }

                function removeLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    poManagement.lineItemID.value = lineItemID;
                    poManagement.target.value = "RemoveLineItem";
                    document.poManagement.action = "../PurchaseOrderManagementController";
                    document.poManagement.submit();
                }

                function deletePO() {
                    window.onbeforeunload = null;
                    window.location.href = "../PurchaseOrderManagementController?target=DeletePO";
                }

                function addressBook() {
                    window.onbeforeunload = null;
                    editContactForm.target.value = "ListAllCustomer";
                    document.editContactForm.action = "../OrderManagementController";
                    document.editContactForm.submit();
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
                        <h2>Purchase Order: <%if (purchaseOrder != null && purchaseOrder.getPurchaseOrderNumber() != null) {
                                out.print(purchaseOrder.getPurchaseOrderNumber());
                            }%></h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=purchaseOrder.getSalesConfirmationOrder().getId()%>"><%=purchaseOrder.getSalesConfirmationOrder().getSalesPerson().getStaffPrefix()%><%=purchaseOrder.getSalesConfirmationOrder().getSalesConfirmationOrderNumber()%></a></span></li>
                                <li><span><a href= "purchaseOrders.jsp">POs</a></span></li>
                                <li><span>PO &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <form name="poManagement" action="../PurchaseOrderManagementController">
                        <section class="panel">
                            <div class="panel-body">
                                <div class="invoice">
                                    <header class="clearfix">
                                        <div class="row">
                                            <div class="col-sm-6 mt-md">
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Purchase Order</h2>
                                                <input type='text' <%=formDisablerFlag%> class='form-control' id='poNumber' name='poNumber' value='<%=purchaseOrder.getPurchaseOrderNumber()%>' style='max-width: 300px' required/>
                                                <br/>
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
                                                                if (purchaseOrder != null) {
                                                                    String s_coyname = purchaseOrder.getCompanyName();
                                                                    String s_name = purchaseOrder.getSupplierName();
                                                                    String s_email = purchaseOrder.getSupplierEmail();
                                                                    String s_officeNo = purchaseOrder.getSupplierOfficeNo();
                                                                    String s_mobileNo = purchaseOrder.getSupplierMobileNo();
                                                                    String s_faxNo = purchaseOrder.getSupplierFaxNo();
                                                                    String s_address = purchaseOrder.getSupplierAddress();

                                                                    if (s_name == null) {
                                                                        s_name = "";
                                                                    }
                                                                    if (s_email == null) {
                                                                        s_email = "";
                                                                    }
                                                                    if (s_officeNo == null) {
                                                                        s_officeNo = "";
                                                                    }
                                                                    if (s_mobileNo == null) {
                                                                        s_mobileNo = "";
                                                                    }
                                                                    if (s_faxNo == null) {
                                                                        s_faxNo = "";
                                                                    }
                                                                    if (s_address == null) {
                                                                        s_address = "";
                                                                    }

                                                                    out.print("<input type='text' " + formDisablerFlag + " class='form-control' name='companyName' value='" + s_coyname + "' placeholder='Company'/>");
                                                                    out.print("<input type='text' " + formDisablerFlag + " class='form-control' name='supplierName' value='" + s_name + "' placeholder='Supplier Name'/>");
                                                                    out.print("<input type='text' " + formDisablerFlag + " class='form-control' name='supplierEmail' value='" + s_email + "' placeholder='Supplier Email' />");
                                                                    out.print("<input type='text' " + formDisablerFlag + " class='form-control' name='supplierOfficeNo' value='" + s_officeNo + "' placeholder='Supplier Office No' />");
                                                                    out.print("<input type='text' " + formDisablerFlag + " class='form-control' name='supplierMobileNo' value='" + s_mobileNo + "' placeholder='Supplier Mobile No' />");
                                                                    out.print("<input type='text' " + formDisablerFlag + " class='form-control' name='supplierFaxNo' value='" + s_faxNo + "' placeholder='Supplier Fax No' />");
                                                                    if (!s_address.equals("")) {
                                                                        out.print("<textarea " + formDisablerFlag + "  placeholder='Supplier Address' class='form-control' rows='3' name='supplierAddress'>" + s_address + " </textarea>");
                                                                    } else {
                                                                        out.print("<textarea " + formDisablerFlag + "  placeholder='Supplier Address' class='form-control' rows='3' name='supplierAddress'></textarea>");
                                                                    }
                                                                    out.print("<br>");
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
                                                        <span class="value" style="min-width: 200px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (purchaseOrder != null && purchaseOrder.getSalesConfirmationOrder().getSalesPerson().getName() != null) {
                                                                    out.print(purchaseOrder.getSalesConfirmationOrder().getSalesPerson().getName());
                                                                } else {
                                                                    out.print(staff.getName());
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Date:</span>
                                                        <span class="value" style="min-width: 200px">
                                                            <%
                                                                if (purchaseOrder.getPurchaseOrderDate() != null) {
                                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
                                                                    String date = DATE_FORMAT.format(purchaseOrder.getPurchaseOrderDate());
                                                                    out.print("<input " + formDisablerFlag + " id='poDate' name='poDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "' required>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='poDate' name='poDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' required placeholder='dd/mm/yyyy'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Status: </span>
                                                        <span class="value" style="min-width: 200px">
                                                            <select <%=formDisablerFlag%> id="status" name="status" class="form-control input-sm" required>
                                                                <%
                                                                    if ((purchaseOrder.getStatus() != null && !purchaseOrder.getStatus().isEmpty())) {
                                                                        String selectedStatus = purchaseOrder.getStatus();

                                                                        if (selectedStatus.equals("Created")) {
                                                                            out.print("<option value='Pending' selected>Pending</option>");
                                                                            out.print("<option value='Completed'>Completed</option>");
                                                                        } else if (selectedStatus.equals("Completed")) {
                                                                            out.print("<option value='Pending'>Pending</option>");
                                                                            out.print("<option value='Completed' selected>Completed</option>");
                                                                        } else {
                                                                            out.print("<option value='Pending'>Pending</option>");
                                                                            out.print("<option value='Completed'>Completed</option>");
                                                                        }
                                                                    }
                                                                %>
                                                            </select>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Terms: </span>
                                                        <span class="value" style="min-width: 200px; font-size: 10.5pt; text-align: left;">
                                                            <%if (purchaseOrder.getTerms() != null && !purchaseOrder.getTerms().isEmpty()) {%>
                                                            <input type='text' <%=formDisablerFlag%> class='form-control' name='terms' value='<%=purchaseOrder.getTerms()%>'>
                                                            <%} else {%>
                                                            <input type='text' <%=formDisablerFlag%> class='form-control' name='terms' placeholder='eg TT in advance'>
                                                            <%}%>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Delivery Date: </span>
                                                        <span class="value" style="min-width: 200px; font-size: 10.5pt; text-align: left;">
                                                            <%if (purchaseOrder.getTerms() != null && !purchaseOrder.getTerms().isEmpty()) {%>
                                                            <input type='text' <%=formDisablerFlag%> class='form-control' name='deliveryDate' value='<%=purchaseOrder.getDeliveryDate()%>'>
                                                            <%} else {%>
                                                            <input type='text' <%=formDisablerFlag%> class='form-control' name='deliveryDate' placeholder='eg TT in advance'>
                                                            <%}%>
                                                        </span>
                                                    </p>

                                                    <p class="mb-none">
                                                        <span class="text-dark">Currency: </span>
                                                        <span class="value" style="min-width: 200px; font-size: 10.5pt; text-align: left;">
                                                            <%if (purchaseOrder.getCurrency() != null && !purchaseOrder.getCurrency().isEmpty()) {%>
                                                            <input type='text' <%=formDisablerFlag%> class='form-control' name='currency' value='<%=purchaseOrder.getCurrency()%>'>
                                                            <%} else {%>
                                                            <input type='text' <%=formDisablerFlag%> class='form-control' name='currency' placeholder='eg RMB'>
                                                            <%}%>
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
                                                        <%
                                                            if (!editingLineItem.equals("")) {
                                                                out.println("<input type='text' class='form-control' name='itemName' disabled/>");
                                                            } else {
                                                                out.println("<input type='text' class='form-control' name='itemName'/>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td>
                                                        <%
                                                            if (!editingLineItem.equals("")) {
                                                                out.println("<textarea class='form-control' rows='1' name='itemDescription' disabled></textarea>");
                                                            } else {
                                                                out.println("<textarea class='form-control' rows='1' name='itemDescription'></textarea>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <%
                                                                if (!editingLineItem.equals("")) {
                                                                    out.println("<input type='number' class='form-control' id='input_itemUnitPrice' name='itemUnitPrice' min='0' step='any' disabled/>");
                                                                } else {
                                                                    out.println("<input type='number' class='form-control' id='input_itemUnitPrice' name='itemUnitPrice' min='0' step='any'/>");
                                                                }
                                                            %>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <%
                                                            if (!editingLineItem.equals("")) {
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
                                                        <button class='btn btn-default btn-block' <%=formDisablerFlag%> onclick='javascript:addLineItemToExistingPO(addLineItemToExistingPO<%=purchaseOrder.getId()%>)'>Add Item</button>
                                                    </td>
                                                </tr>

                                                <!-- loop line item page -->
                                                <%
                                                    if (purchaseOrder != null && purchaseOrder.getItems() != null) {
                                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                        for (int i = 0; i < purchaseOrder.getItems().size(); i++) {
                                                            if (!editingLineItem.isEmpty() && editingLineItem.equals(purchaseOrder.getItems().get(i).getId() + "")) {
                                                                //Print editable fields
                                                                double price = purchaseOrder.getItems().get(i).getItemUnitPrice();
                                                %>
                                                <tr>
                                                    <td>
                                                        <input type='text' class='form-control' name='itemName' id='itemName<%=purchaseOrder.getItems().get(i).getId()%>' value='<%=purchaseOrder.getItems().get(i).getItemName()%>'/>
                                                    </td>
                                                    <td>
                                                        <textarea class='form-control' rows='5' name='itemDescription' id='itemDescription<%=purchaseOrder.getItems().get(i).getId()%>'><%=purchaseOrder.getItems().get(i).getItemDescription()%></textarea>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type='number' class='form-control' id='itemUnitPrice<%=purchaseOrder.getItems().get(i).getId()%>' name='itemUnitPrice' min='0' step='any' value='<%=price%>'/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <input type='number' class='form-control' id='itemQty<%=purchaseOrder.getItems().get(i).getId()%>' min='0' name='itemQty' value='<%=purchaseOrder.getItems().get(i).getItemQty()%>'/>
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
                                                        out.print("<td class='text-center'><div class='btn-group'><button class='btn btn-default' type='button' onclick='javascript:saveEditLineItem(" + purchaseOrder.getItems().get(i).getId() + ")'>Save</button>&nbsp;");
                                                        out.print("<button class='btn btn-default' type='button' onclick='javascript:back2()' >Back</button></div></td>");
                                                    %>
                                                </tr> 
                                                <%
                                                            } else {
                                                                //Print normal text
                                                                double price = 0;
                                                                out.print("<tr>");
                                                                out.print("<td class='text-weight-semibold text-dark'>" + purchaseOrder.getItems().get(i).getItemName() + "</td>");
                                                                out.print("<td>" + purchaseOrder.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");
                                                                price = purchaseOrder.getItems().get(i).getItemUnitPrice();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'>" + purchaseOrder.getItems().get(i).getItemQty() + "</td>");
                                                                price = purchaseOrder.getItems().get(i).getItemUnitPrice() * purchaseOrder.getItems().get(i).getItemQty();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'><div class='btn-group'><button " + formDisablerFlag + " class='btn btn-default' type='button' onclick='javascript:editLineItem(" + purchaseOrder.getItems().get(i).getId() + ")'>Edit</button>&nbsp;");
                                                                out.print("<button " + formDisablerFlag + " class='btn btn-default' onclick='javascript:removeLineItem(" + purchaseOrder.getItems().get(i).getId() + ")'>Del</button></div></td>");
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
                                                    if (purchaseOrder.getRemarks() != null && !purchaseOrder.getRemarks().isEmpty()) {
                                                        out.print("Remarks: ");
                                                        out.print(purchaseOrder.getRemarks().replaceAll("\\r", "<br>"));
                                                    }
                                                %>
                                            </div>
                                            <div class="col-sm-3"></div>
                                            <div class="col-sm-4">
                                                <table class="table h5 text-dark">
                                                    <tbody>
                                                        <tr class="h4">
                                                            <td colspan="2">Total (SGD)</td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (purchaseOrder == null) {
                                                                        out.print("<span id='output_totalPrice'>$0.00</span>");
                                                                    } else {
                                                                        double formatedPrice = 0;
                                                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                                        formatedPrice = purchaseOrder.getTotalPrice();
                                                                        out.print("<span id='output_totalPrice'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + purchaseOrder.getTotalPrice() + "' id='totalPrice'>");
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
                                                if (purchaseOrder != null) {
                                                    if (purchaseOrder.getItems().size() > 0) {
                                                        out.print("<a href='purchaseOrder-print.jsp' target='_blank' class='btn btn-default'><i class='fa fa-print'></i> Print PDF</a>");
                                                    }
                                                    if (purchaseOrder.getNotes() != null && !purchaseOrder.getNotes().isEmpty()) {
                                                        out.print("<button type='button' class='btn btn-info modal-with-form' href='#modalNotes'>Notes</button>");
                                                    } else {
                                                        out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalNotes'>Notes</button>");
                                                    }
                                                    out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalRemarks' data-toggle='tooltip' data-placement='top' title='*Remarks will be reflected in the PO'>Remarks</button>");
                                                }
                                            %> 
                                        </div>
                                    </div>

                                    <div class="col-sm-6 text-right mt-md mb-md">
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                            <%
                                                if (purchaseOrder != null) {
                                                    out.print("<button type='button' class='modal-with-move-anim btn btn-danger' href='#modalRemove'>Delete</button>");
                                                    out.print("<button " + formDisablerFlag + " class='btn btn-success' onclick='javascript:updatePO();'>Save</button>");
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
                            if (purchaseOrder != null) {
                                out.print("<input type='hidden' name='customerID' value='" + purchaseOrder.getSalesConfirmationOrder().getCustomer().getId() + "'>");
                            }
                        %>
                        <input type="hidden" name="lineItemID" value="">   
                        <input type="hidden" name="target" value="SavePO">    
                        <input type="hidden" name="source" value="">    
                        <input type="hidden" name="id" value="">    
                    </form>
                    <!-- end: page -->

                    <%if (purchaseOrder != null) {%>
                    <div id="modalEditForm" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editContactForm" action="../PurchaseOrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Edit Contact</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Company <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="company" class="form-control" value="<%=purchaseOrder.getSupplierName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <textarea class="form-control" rows="3" name="address" required><%=purchaseOrder.getSupplierAddress()%></textarea>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Telephone <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="officeNo" class="form-control" value="<%=purchaseOrder.getSupplierOfficeNo()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Fasimile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="faxNo" class="form-control" value="<%=purchaseOrder.getSupplierFaxNo()%>"/>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="form-group mt-lg">
                                        <label class="col-sm-3 control-label">Name <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="name" class="form-control" value="<%=purchaseOrder.getSupplierName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Mobile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="mobileNo" class="form-control" value="<%=purchaseOrder.getSupplierMobileNo()%>"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Email <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="email" name="email" class="form-control" value="<%=purchaseOrder.getSupplierEmail()%>" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="UpdatePOContact">    
                                    <input type="hidden" name="id" value="<%=purchaseOrder.getId()%>">  
                                    <input type="hidden" name="source" value="addressBook"> 
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Save</button>
                                            <button class="btn btn-primary" onclick="javascript:addressBook(<%=purchaseOrder.getId()%>)">Address Book</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalRemarks" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editRemarksForm" action="../PurchaseOrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Remarks</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Remarks</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="remarks"><%if (purchaseOrder.getRemarks() != null) {
                                                    out.print(purchaseOrder.getRemarks());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdatePORemarks">    
                                    <input type="hidden" name="id" value="<%=purchaseOrder.getId()%>">  
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

                    <div id="modalNotes" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editNotesForm" action="../PurchaseOrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Notes</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Notes</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="notes"><%if (purchaseOrder.getNotes() != null) {
                                                    out.print(purchaseOrder.getNotes());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdatePONotes">    
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
                            <div class="panel-body" style="padding-top: 0px;">
                                <div class="modal-wrapper">
                                    <div class="modal-icon">
                                        <i class="fa fa-question-circle" style="top: 0px;"></i>
                                    </div>
                                    <div class="modal-text">
                                        <p>Are you sure that you want to delete this Purchase Order?<br/>This action cannot be reversed!</p>
                                    </div>
                                </div>
                            </div>
                            <footer class="panel-footer">
                                <div class="row">
                                    <div class="col-md-12 text-right">
                                        <button class="btn btn-primary modal-confirm" onclick="javascript:deletePO();">Confirm</button>
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

