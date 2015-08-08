-<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Contact"%>
<%@page import="EntityManager.Customer"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    String previousMgtPage = (String) session.getAttribute("previousManagementPage");
    if (previousMgtPage == null) {
        previousMgtPage = "";
    }
    List<Customer> customers = (List<Customer>) (session.getAttribute("customers"));
    List<Contact> contacts = (List<Contact>) (session.getAttribute("contacts"));
    SalesConfirmationOrder sco = (SalesConfirmationOrder) (session.getAttribute("sco"));
    Contact contact = null;
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else {
        String scoID = request.getParameter("id");
        String scoDate = request.getParameter("scoDate");
        String terms = request.getParameter("terms");
        String status = request.getParameter("status");
        String estimatedDeliveryDate = request.getParameter("estimatedDeliveryDate");
        String poNumber = request.getParameter("poNumber");
        String selectedCustomerID = request.getParameter("selectedCustomerID");
        String selectedContactID = request.getParameter("selectedContactID");
        String editingLineItem = request.getParameter("editingLineItem");
        String formDisablerFlag = "";
        if (sco != null && sco.getStatus().equals("Voided")) {
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
                    window.location.href = "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=sco.getCustomer().getId()%>";
                <%} else {%>
                    window.location.href = "../OrderManagementController?target=ListAllSCO";
                <%}%>
                }

                function back2(id) {
                    window.onbeforeunload = null;
                    var scoDate = document.getElementById("scoDate").value;
                    var terms = document.getElementById("terms").value;
                    var status = document.getElementById("status").value;
                    window.location.href = "scoManagement_add.jsp?id=" + id + "&status=" + status + "&scoDate=" + scoDate + "&terms=" + terms;
                }

                function getCustomerContacts() {
                    window.onbeforeunload = null;
                    var customerID = document.getElementById("customerList").value;
                    var scoDate = document.getElementById("scoDate").value;
                    var terms = document.getElementById("terms").value;
                    var estimatedDeliveryDate = document.getElementById("estimatedDeliveryDate").value;
                    var poNumber = document.getElementById("poNumber").value;
                    if (customerID !== "") {
                        window.location.href = "../OrderManagementController?target=ListCustomerContacts&customerID=" + customerID + "&scoDate=" + scoDate + "&terms=" + terms + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&poNumber=" + poNumber;
                    }
                }

                function selectCustomerContact() {
                    window.onbeforeunload = null;
                    var customerID = document.getElementById("customerList").value;
                    var scoDate = document.getElementById("scoDate").value;
                    var terms = document.getElementById("terms").value;
                    var estimatedDeliveryDate = document.getElementById("estimatedDeliveryDate").value;
                    var poNumber = document.getElementById("poNumber").value;
                    if (customerID !== "") {
                        var contactID = document.getElementById("customerContactid").value;
                        window.location.href = "scoManagement_add.jsp?selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&scoDate=" + scoDate + "&terms=" + terms + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&poNumber=" + poNumber;
                    }
                }

                function addLineItemToNewSCO() {
                    window.onbeforeunload = null;
                    scoManagement.target.value = "SaveSCO";
                    scoManagement.source.value = "AddLineItemToNewSCO";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function saveSCO() {
                    window.onbeforeunload = null;
                    scoManagement.target.value = "SaveSCO";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function addLineItemToExistingSCO(id) {
                    window.onbeforeunload = null;
                    scoManagement.id.value = id;
                    scoManagement.target.value = "UpdateSCO";
                    scoManagement.source.value = "AddLineItemToExistingSCO";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function updateSCO(id) {
                    window.onbeforeunload = null;
                    scoManagement.id.value = id;
                    scoManagement.target.value = "UpdateSCO";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function editLineItem(id, lineItemID) {
                    window.onbeforeunload = null;
                    scoManagement.id.value = id;
                    scoManagement.lineItemID.value = lineItemID;
                    var scoDate = document.getElementById("scoDate").value;
                    var terms = document.getElementById("terms").value;
                    var status = document.getElementById("status").value;
                    var estimatedDeliveryDate = document.getElementById("estimatedDeliveryDate").value;
                    var poNumber = document.getElementById("poNumber").value;
                    window.location.href = "scoManagement_add.jsp?id=" + id + "&scoDate=" + scoDate + "&terms=" + terms + "&status=" + status + "&editingLineItem=" + lineItemID + "&estimatedDeliveryDate=" + estimatedDeliveryDate + "&poNumber=" + poNumber;
                }

                function saveEditLineItem(id, lineItemID) {
                    window.onbeforeunload = null;
                    scoManagement.id.value = id;
                    scoManagement.lineItemID.value = lineItemID;
                    scoManagement.itemName.value = document.getElementById("itemName" + lineItemID).value;
                    scoManagement.itemDescription.value = document.getElementById("itemDescription" + lineItemID).value;
                    scoManagement.itemUnitPrice.value = document.getElementById("itemUnitPrice" + lineItemID).value;
                    scoManagement.itemQty.value = document.getElementById("itemQty" + lineItemID).value;
                    scoManagement.target.value = "EditLineItem";
                    document.scoManagement.action = "../OrderManagementController?editingLineItem=" + lineItemID;
                    document.scoManagement.submit();
                }

                function removeLineItem(id, lineItemID) {
                    window.onbeforeunload = null;
                    scoManagement.id.value = id;
                    scoManagement.lineItemID.value = lineItemID;
                    scoManagement.target.value = "RemoveLineItem";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function voidSCO(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../OrderManagementController?target=VoidSCO&id=" + id;
                }

                function addressBook() {
                    window.onbeforeunload = null;
                    editContactForm.target.value = "ListAllCustomer";
                    document.editContactForm.action = "../OrderManagementController";
                    document.editContactForm.submit();
                }

                function listAllPO(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../PurchaseOrderManagementController?target=ListPoTiedToSCO&id=" + id;
                }

                function listAllDO(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../DeliveryOrderManagementController?target=ListDoTiedToSCO&id=" + id;
                }

                function listAllInvoice(id) {
                    window.onbeforeunload = null;
                    window.location.href = "../InvoiceManagementController?target=ListInvoiceTiedToSCO&id=" + id;
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
                        <h2>Sales Confirmation Order</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="../AccountManagement/workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <%if (previousMgtPage.equals("sco")) {%>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">SCO Management</a></span></li>
                                            <%} else if (previousMgtPage.equals("soa")) {%>
                                <li><span><a href= "../StatementOfAccountManagementController?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span><a href= "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=sco.getCustomer().getId()%>"><%=sco.getCustomer().getCustomerName()%></a></span></span></li>
                                            <%}%>
                                <li><span>SCO &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <form name="scoManagement" action="../OrderManagementController">
                        <section class="panel">
                            <div class="panel-body">
                                <div class="invoice">
                                    <header class="clearfix">
                                        <div class="row">
                                            <div class="col-sm-6 mt-md">
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">SCO No.
                                                    <%
                                                        if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                            out.print(sco.getSalesPerson().getStaffPrefix() + sco.getSalesConfirmationOrderNumber());
                                                        }
                                                    %>
                                                </h2><br>
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
                                                                if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                                    out.print("<b>" + sco.getCustomerName() + "</b>");
                                                                    String repl = sco.getContactAddress().replaceAll("\\r", "<br>");
                                                                    out.print("<br>" + repl);
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
                                                                        out.print("<br>" + sco.getContactEmail() + "<br>");
                                                                    }
                                                                    if (!formDisablerFlag.equals("disabled")) {
                                                                        out.print("<div class='text-right'><a href='#modalEditForm' class='modal-with-form'>edit</a></div>");
                                                                    }
                                                                    out.print("<br><br>");
                                                                } else {
                                                            %>
                                                            <select id="customerList" name="customerID" data-plugin-selectTwo class="form-control populate" onchange="javascript:getCustomerContacts()" required>
                                                                <option value="">Select a customer</option>
                                                                <%
                                                                    if (customers != null && customers.size() > 0) {
                                                                        for (int i = 0; i < customers.size(); i++) {
                                                                            if (selectedCustomerID != null && selectedCustomerID.equals(customers.get(i).getId().toString())) {
                                                                                out.print("<option value='" + customers.get(i).getId() + "' selected>" + customers.get(i).getCustomerName() + "</option>");
                                                                            } else {
                                                                                out.print("<option value='" + customers.get(i).getId() + "'>" + customers.get(i).getCustomerName() + "</option>");
                                                                            }
                                                                        }
                                                                    }
                                                                %>
                                                            </select>
                                                            <%}%>
                                                        </div>

                                                        <%
                                                            if (sco == null || scoID == null || scoID.isEmpty()) {
                                                        %>
                                                        <br/><br/>
                                                        <div class="col-md-6" style="padding-left: 0px;">
                                                            <select id="customerContactid" name="contactID" data-plugin-selectTwo class="form-control populate"  onchange="javascript:selectCustomerContact()" required>
                                                                <option value="">Select a contact</option>
                                                                <%
                                                                    if (contacts != null && contacts.size() > 0) {
                                                                        for (int i = 0; i < contacts.size(); i++) {
                                                                            if (selectedContactID != null && selectedContactID.equals(contacts.get(i).getId().toString())) {
                                                                                contact = contacts.get(i);
                                                                                out.print("<option value='" + contacts.get(i).getId() + "' selected>" + contacts.get(i).getName() + "</option>");
                                                                            } else {
                                                                                out.print("<option value='" + contacts.get(i).getId() + "'>" + contacts.get(i).getName() + "</option>");
                                                                            }
                                                                        }
                                                                    }
                                                                %>
                                                            </select>
                                                        </div>
                                                        <div class="col-md-8" style="padding-top: 4px;">
                                                            <%
                                                                if (contact != null) {
                                                                    String repl = contact.getAddress().replaceAll("\\r", "<br>");
                                                                    out.println(repl);
                                                                    out.println("<br/>" + contact.getOfficeNo());
                                                                    out.println("<br/>" + contact.getFaxNo());
                                                                    out.println("<br/>" + contact.getMobileNo() + "<br/><br/>");
                                                                }
                                                            %>
                                                        </div>
                                                        <%}%>
                                                    </address>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="bill-data text-right">
                                                    <p class="mb-none">
                                                        <span class="text-dark">Salesperson: </span>
                                                        <span class="value" style="min-width: 110px; font-size: 10.5pt; text-align: left;">
                                                            <%
                                                                if (sco != null && scoID != null && !scoID.isEmpty() && sco.getSalesPerson() != null) {
                                                                    out.print(sco.getSalesPerson().getName());
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
                                                                if (scoDate != null && !scoDate.isEmpty()) {
                                                                    out.print("<input " + formDisablerFlag + " id='scoDate' name='scoDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + scoDate + "' required>");
                                                                } else if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
                                                                    String date = DATE_FORMAT.format(sco.getSalesConfirmationOrderDate());
                                                                    out.print("<input " + formDisablerFlag + " id='scoDate' name='scoDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' value='" + date + "' required>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='scoDate' name='scoDate' type='text' data-date-format='dd/mm/yyyy' data-plugin-datepicker class='form-control' required placeholder='dd/mm/yyyy'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Terms:</span>
                                                        <span class="value" style="min-width: 110px">
                                                            <select <%=formDisablerFlag%> id="terms" name="terms" class="form-control input-sm" required>
                                                                <%
                                                                    out.print("<option value=''>Select</option>");
                                                                    if (terms != null && !terms.isEmpty()) {
                                                                        if (terms.equals("0")) {
                                                                            out.print("<option value='0' selected>COD</option>");
                                                                            out.print("<option value='14'>14 Days</option>");
                                                                            out.print("<option value='30'>30 Days</option>");
                                                                        } else if (terms.equals("14")) {
                                                                            out.print("<option value='0'>COD</option>");
                                                                            out.print("<option value='14' selected>14 Days</op  tion>");
                                                                            out.print("<option value='30'>30 Days</option>");
                                                                        } else if (terms.equals("30")) {
                                                                            out.print("<option value='0'>COD</option>");
                                                                            out.print("<option value='14'>14 Days</option>");
                                                                            out.print("<option value='30' selected>30 Days</option>");
                                                                        }
                                                                    } else if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                                        if (sco.getTerms() == 0) {
                                                                            out.print("<option value='0' selected>COD</option>");
                                                                            out.print("<option value='14'>14 Days</option>");
                                                                            out.print("<option value='30'>30 Days</option>");
                                                                        } else if (sco.getTerms() == 14) {
                                                                            out.print("<option value='0'>COD</option>");
                                                                            out.print("<option value='14' selected>14 Days</option>");
                                                                            out.print("<option value='30'>30 Days</option>");
                                                                        } else if (sco.getTerms() == 30) {
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
                                                                if (poNumber != null && !poNumber.isEmpty()) {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' value='" + poNumber + "'>");
                                                                } else if (sco != null && scoID != null && !scoID.isEmpty() && !sco.getCustomerPurchaseOrderNumber().isEmpty()) {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' value='" + sco.getCustomerPurchaseOrderNumber() + "'>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='poNumber' name='poNumber' type='text' class='form-control' placeholder='PO Number'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>


                                                    <p class="mb-none">
                                                        <span class="text-dark">Estimated Delivery Date:</span>
                                                        <span class="value" style="min-width: 200px">
                                                            <%
                                                                if (estimatedDeliveryDate != null && !estimatedDeliveryDate.isEmpty()) {
                                                                    out.print("<input " + formDisablerFlag + " id='estimatedDeliveryDate' name='estimatedDeliveryDate' type='text' class='form-control' value='" + estimatedDeliveryDate + "'>");
                                                                } else if (sco != null && scoID != null && !scoID.isEmpty() && !sco.getEstimatedDeliveryDate().isEmpty()) {
                                                                    out.print("<input " + formDisablerFlag + " id='estimatedDeliveryDate' name='estimatedDeliveryDate' type='text' class='form-control' value='" + sco.getEstimatedDeliveryDate() + "'>");
                                                                } else {
                                                                    out.print("<input " + formDisablerFlag + " id='estimatedDeliveryDate' name='estimatedDeliveryDate' type='text' class='form-control' placeholder='Estimated date'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>

                                                    <% if (sco != null && scoID != null && !scoID.isEmpty()) {%>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Status: </span>
                                                        <span class="value" style="min-width: 110px">
                                                            <select <%=formDisablerFlag%> id="status" name="status" class="form-control input-sm" required>
                                                                <%
                                                                    if ((sco.getStatus() != null && !sco.getStatus().isEmpty()) || status != null && status != "") {
                                                                        String selectedStatus;
                                                                        if (status != null && !status.isEmpty()) {
                                                                            //Get from request (haven't saved to SCO)
                                                                            selectedStatus = status;
                                                                        } else {
                                                                            //Get from SCO
                                                                            selectedStatus = sco.getStatus();
                                                                        }

                                                                        if (selectedStatus.equals("Unfulfilled")) {
                                                                            out.print("<option value='Unfulfilled' selected>Unfulfilled</option>");
                                                                            out.print("<option value='Fulfilled'>Fulfilled</option>");
                                                                            out.print("<option value='Completed'>Completed</option>");
                                                                            out.print("<option value='Write-Off'>Write-Off</option>");
                                                                        } else if (selectedStatus.equals("Fulfilled")) {
                                                                            out.print("<option value='Unfulfilled'>Unfulfilled</option>");
                                                                            out.print("<option value='Fulfilled' selected>Fulfilled</option>");
                                                                            out.print("<option value='Completed'>Completed</option>");
                                                                            out.print("<option value='Write-Off'>Write-Off</option>");
                                                                        } else if (selectedStatus.equals("Completed")) {
                                                                            out.print("<option value='Unfulfilled'>Unfulfilled</option>");
                                                                            out.print("<option value='Fulfilled'>Fulfilled</option>");
                                                                            out.print("<option value='Completed' selected>Completed</option>");
                                                                            out.print("<option value='Write-Off'>Write-Off</option>");
                                                                        } else if (selectedStatus.equals("Write-Off")) {
                                                                            out.print("<option value='Unfulfilled'>Unfulfilled</option>");
                                                                            out.print("<option value='Fulfilled'>Fulfilled</option>");
                                                                            out.print("<option value='Completed'>Completed</option>");
                                                                            out.print("<option value='Write-Off' selected>Write-Off</option>");
                                                                        } else if (selectedStatus.equals("Voided")) {
                                                                            out.print("<option value='Voided' selected>Voided</option>");
                                                                        } else {
                                                                            out.print("<option value='Unfulfilled'>Unfulfilled</option>");
                                                                            out.print("<option value='Fulfilled'>Fulfilled</option>");
                                                                            out.print("<option value='Completed'>Completed</option>");
                                                                            out.print("<option value='Write-Off'>Write-Off</option>");
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
                                                        <%
                                                            if (scoID == null && (customers == null || selectedContactID == null || selectedContactID.equals("")) || !editingLineItem.equals("")) {
                                                                out.println("<input type='text' class='form-control' name='itemName' disabled/>");
                                                            } else {
                                                                out.println("<input type='text' class='form-control' name='itemName'/>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td>
                                                        <%
                                                            if (scoID == null && (customers == null || selectedContactID == null || selectedContactID.equals("")) || !editingLineItem.equals("")) {
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
                                                                if (scoID == null && (customers == null || selectedContactID == null || selectedContactID.equals("")) || !editingLineItem.equals("")) {
                                                                    out.println("<input type='number' class='form-control' id='input_itemUnitPrice' name='itemUnitPrice' min='0' step='any' disabled/>");
                                                                } else {
                                                                    out.println("<input type='number' class='form-control' id='input_itemUnitPrice' name='itemUnitPrice' min='0' step='any'/>");
                                                                }
                                                            %>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <%
                                                            if (scoID == null && (customers == null || selectedContactID == null || selectedContactID.equals("")) || !editingLineItem.equals("")) {
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
                                                            if (scoID == null && (customers == null || selectedContactID == null || selectedContactID.equals("")) || !editingLineItem.equals("")) {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToNewSCO()' disabled>Add Item</button>");
                                                            } else if (scoID == null || scoID.isEmpty() || sco == null) {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToNewSCO()'>Add Item</button>");
                                                            } else {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToExistingSCO(" + scoID + ")'>Add Item</button>");
                                                            }
                                                        %>
                                                    </td>
                                                </tr>

                                                <!-- loop line item page -->
                                                <%
                                                    if (sco != null && scoID != null && sco.getItems() != null && !scoID.isEmpty()) {
                                                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                        for (int i = 0; i < sco.getItems().size(); i++) {
                                                            if (!editingLineItem.isEmpty() && editingLineItem.equals(sco.getItems().get(i).getId() + "")) {
                                                                //Print editable fields
                                                                double price = sco.getItems().get(i).getItemUnitPrice();
                                                %>
                                                <tr>
                                                    <td>
                                                        <input type='text' class='form-control' name='itemName' id='itemName<%=sco.getItems().get(i).getId()%>' value='<%=sco.getItems().get(i).getItemName()%>'/>
                                                    </td>
                                                    <td>              
                                                        <textarea class='form-control' rows='5' name='itemDescription' id='itemDescription<%=sco.getItems().get(i).getId()%>'><%=sco.getItems().get(i).getItemDescription()%></textarea>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type='number' class='form-control' id='itemUnitPrice<%=sco.getItems().get(i).getId()%>' name='itemUnitPrice' min='0' step='any' value='<%=price%>'/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <input type='number' class='form-control' id='itemQty<%=sco.getItems().get(i).getId()%>' min='0' name='itemQty' value='<%=sco.getItems().get(i).getItemQty()%>'/>
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
                                                        out.print("<td class='text-center'><div class='btn-group'><button class='btn btn-default' type='button' onclick='javascript:saveEditLineItem(" + sco.getId() + "," + sco.getItems().get(i).getId() + ")'>Save</button>&nbsp;");
                                                        out.print("<button class='btn btn-default' type='button' onclick='javascript:back2(" + sco.getId() + ")' >Back</button></div></td>");
                                                    %>
                                                </tr> 
                                                <%
                                                            } else {
                                                                //Print normal text
                                                                double price = 0;
                                                                out.print("<tr>");
                                                                out.print("<td class='text-weight-semibold text-dark'>" + sco.getItems().get(i).getItemName() + "</td>");
                                                                out.print("<td>" + sco.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");
                                                                price = sco.getItems().get(i).getItemUnitPrice();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'>" + sco.getItems().get(i).getItemQty() + "</td>");
                                                                price = sco.getItems().get(i).getItemUnitPrice() * sco.getItems().get(i).getItemQty();
                                                                out.print("<td class='text-center'>" + formatter.format(price) + "</td>");
                                                                out.print("<td class='text-center'><div class='btn-group'><button " + formDisablerFlag + " class='btn btn-default' type='button' onclick='javascript:editLineItem(" + sco.getId() + "," + sco.getItems().get(i).getId() + ")'>Edit</button>&nbsp;");
                                                                out.print("<button " + formDisablerFlag + " class='btn btn-default' onclick='javascript:removeLineItem(" + sco.getId() + "," + sco.getItems().get(i).getId() + ")'>Del</button></div></td>");
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
                                                    if (sco != null && scoID != null && !scoID.isEmpty() && sco.getRemarks() != null && !sco.getRemarks().isEmpty()) {
                                                        out.print("REMARKS: ");
                                                        out.print(sco.getRemarks().replaceAll("\\r", "<br>"));
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
                                                                    if (scoID == null || scoID.isEmpty() || sco == null) {
                                                                        out.print("<span id='output_subtotal'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = sco.getTotalPrice() / (sco.getTaxRate() / 100 + 1);
                                                                        out.print("<span id='output_subtotal'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + (sco.getTotalPrice() / (sco.getTaxRate() / 100 + 1)) + "' id='subtotal'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2">
                                                                <%
                                                                    if (scoID == null || scoID.isEmpty() || sco == null) {
                                                                        out.print("7.0% GST");
                                                                    } else {
                                                                        out.print("" + sco.getTaxRate() + "% GST");
                                                                    }
                                                                %>
                                                            </td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (scoID == null || scoID.isEmpty() || sco == null) {
                                                                        out.print("<span id='output_gst'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = sco.getTotalTax();
                                                                        out.print("<span id='output_gst'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + sco.getTotalTax() + "' id='gst'>");
                                                                    }
                                                                %>
                                                            </td>
                                                        </tr>
                                                        <tr class="h4">
                                                            <td colspan="2">Total (SGD)</td>
                                                            <td class="text-left">
                                                                <%
                                                                    if (scoID == null || scoID.isEmpty() || sco == null) {
                                                                        out.print("<span id='output_totalPrice'>$0.00</span>");
                                                                    } else {
                                                                        formatedPrice = sco.getTotalPrice();
                                                                        out.print("<span id='output_totalPrice'>" + formatter.format(formatedPrice) + "</span>");
                                                                        out.print("<input type='hidden' value='" + sco.getTotalPrice() + "' id='totalPrice'>");
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
                                                if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                    if (sco.getItems().size() > 0) {
                                                        out.print("<a href='../OrderManagementController?target=PrintPDF&id=" + scoID + "' target='_blank' class='btn btn-default'><i class='fa fa-print'></i> Print PDF</a>");
                                                    }
                                                    if (sco.getNotes() != null && !sco.getNotes().isEmpty()) {
                                                        out.print("<button type='button' class='btn btn-info modal-with-form' href='#modalNotes'>Notes</button>");
                                                    } else {
                                                        out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalNotes'>Notes</button>");
                                                    }
                                                    out.print("<button type='button' class='btn btn-default modal-with-form' href='#modalRemarks' data-toggle='tooltip' data-placement='top' title='*Remarks will be reflected in the SCO'>Remarks</button>");
                                                }
                                            %> 
                                        </div>
                                        &nbsp;
                                        <div class="btn-group">
                                            <%
                                                if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                    if (sco.getItems().size() > 0) {
                                                        if (sco.getPurchaseOrders().size() > 0) {
                                                            out.print("<button type='button' class='btn btn-default' onclick='javascript:listAllPO(" + sco.getId() + ")'>Purchase Orders <span class='badge' style='background-color:#0088CC'>" + sco.getNumOfPurchaseOrders() + "</span></button>");
                                                        }
                                                        if (sco.getDeliveryOrders().size() > 0) {
                                                            out.print("<button type='button' class='btn btn-default' onclick='javascript:listAllDO(" + sco.getId() + ")'>Delivery Orders <span class='badge' style='background-color:#0088CC'>" + sco.getNumOfDeliveryOrders() + "</span></button>");
                                                        }
                                                        if (sco.getInvoices().size() > 0) {
                                                            out.print("<button type='button' class='btn btn-default' onclick='javascript:listAllInvoice(" + sco.getId() + ")'>Invoices <span class='badge' style='background-color:#0088CC'>" + sco.getNumOfInvoices() + "</span></button>");
                                                        }
                                                    }
                                                }
                                            %>         
                                        </div>
                                    </div>

                                    <div class="col-sm-6 text-right mt-md mb-md">
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-default" onclick="javascript:back()">Back</button>
                                            <% if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                    if (!sco.getStatus().equals("Voided")) {
                                                        out.print("<button type='button' class='modal-with-move-anim btn btn-danger' href='#modalRemove'>Void SCO</button>");
                                                    }
                                                    if (sco.getItems().size() > 0) {
                                                        out.print("<button " + formDisablerFlag + " class='btn btn-primary modal-with-form' href='#modalGeneratePO'>Generate PO</button>");
                                                        out.print("<button " + formDisablerFlag + " class='btn btn-primary modal-with-form' href='#modalGenerateDO' >Generate DO</button>");
                                                        out.print("<button " + formDisablerFlag + " class='btn btn-primary modal-with-form' href='#modalGenerateInvoice' >Generate Invoice</button>");
                                                    }
                                                    out.print("<button " + formDisablerFlag + " class='btn btn-success' onclick='javascript:updateSCO(" + scoID + ")'>Save</button>");
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
                            if (sco != null && scoID != null && !scoID.isEmpty()) {
                                out.print("<input type='hidden' name='customerID' value='" + sco.getCustomer().getId() + "'>");
                            }
                        %>
                        <input type="hidden" name="lineItemID" value="">   
                        <input type="hidden" name="target" value="SaveSCO">    
                        <input type="hidden" name="source" value="">    
                        <input type="hidden" name="id" value="">    
                    </form>
                    <!-- end: page -->

                    <%if (sco != null && scoID != null && !scoID.isEmpty()) {%>
                    <div id="modalGenerateInvoice" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Generate Invoice</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Date <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <input type="text" name="invoiceDate" data-plugin-datepicker data-date-format="dd/mm/yyyy" class="form-control" placeholder="dd/mm/yyyy" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="GenerateInvoice">    
                                    <input type="hidden" name="id" value="<%=scoID%>">  
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Generate</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalGeneratePO" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Generate Purchase Order</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">PO No <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="poNumber" class="form-control" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Date <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <input type="text" name="poDate" data-plugin-datepicker data-date-format="dd/mm/yyyy" class="form-control" placeholder="dd/mm/yyyy" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="GeneratePO">    
                                    <input type="hidden" name="id" value="<%=scoID%>">  
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Generate</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                    <div id="modalGenerateDO" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Generate Delivery Order</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Date <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <input type="text" name="doDate" data-plugin-datepicker data-date-format="dd/mm/yyyy" class="form-control" placeholder="dd/mm/yyyy" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="GenerateDO">    
                                    <input type="hidden" name="id" value="<%=scoID%>">  
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Generate</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

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
                                            <input type="text" name="company" class="form-control" value="<%=sco.getCustomerName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-md-3 control-label">Address <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <textarea class="form-control" rows="3" name="address" required><%=sco.getContactAddress()%></textarea>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Telephone <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="officeNo" class="form-control" value="<%=sco.getContactOfficeNo()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Fasimile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="faxNo" class="form-control" value="<%=sco.getContactFaxNo()%>"/>
                                        </div>
                                    </div>
                                    <hr>
                                    <div class="form-group mt-lg">
                                        <label class="col-sm-3 control-label">Name <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="text" name="name" class="form-control" value="<%=sco.getContactName()%>" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Mobile</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="mobileNo" class="form-control" value="<%=sco.getContactMobileNo()%>"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Email <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="email" name="email" class="form-control" value="<%=sco.getContactEmail()%>" required/>
                                        </div>
                                    </div>
                                    <br>
                                    <input type="hidden" name="target" value="UpdateSCOContact">    
                                    <input type="hidden" name="id" value="<%=scoID%>">  
                                    <input type="hidden" name="source" value="addressBook"> 
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Save</button>
                                            <button class="btn btn-primary" onclick="javascript:addressBook();">Address Book</button>
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
                                            <textarea class="form-control" rows="5" name="notes"><%if (sco.getNotes() != null) {
                                                    out.print(sco.getNotes());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdateSCONotes">    
                                    <input type="hidden" name="id" value="<%=scoID%>">  
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
                            <form name="editRemarksForm" action="../OrderManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Remarks</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Remarks</label>
                                        <div class="col-sm-9">
                                            <textarea class="form-control" rows="5" name="remarks"><%if (sco.getRemarks() != null) {
                                                    out.print(sco.getRemarks());
                                                }%></textarea>
                                        </div>
                                    </div>
                                    <input type="hidden" name="target" value="UpdateSCORemarks">    
                                    <input type="hidden" name="id" value="<%=scoID%>">  
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button <%=formDisablerFlag%>  class="btn btn-success" type="submit">Save</button>
                                            <button <%=formDisablerFlag%>  class="btn btn-default" type="reset">Clear</button>
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
                                        <p>Are you sure that you want to void this Sales Confirmation Order?<br>All associated DO, Invoice, Credit Notes will be voided.<br>All associated Purchase Orders & Payment records will also be deleted.<br>This action cannot be reversed!</p>
                                    </div>
                                </div>
                            </div>
                            <footer class="panel-footer">
                                <div class="row">
                                    <div class="col-md-12 text-right">
                                        <button class="btn btn-primary modal-confirm" onclick="voidSCO(<%=scoID%>)">Confirm</button>
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

