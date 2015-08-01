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
    DeliveryOrder deliveryOrder = (DeliveryOrder) (session.getAttribute("do"));
    String previousMgtPage = (String) session.getAttribute("previousManagementPage");
    if (previousMgtPage == null) {
        previousMgtPage = "";
    }
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
                function back() {
                    window.onbeforeunload = null;
                <% if (previousMgtPage.equals("soa")) {%>
                    window.location.href = "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=deliveryOrder.getSalesConfirmationOrder().getCustomer().getId()%>";
                <%} else {%>
                    window.location.href = "deliveryOrders.jsp";
                <%}%>
                }

                function back2() {
                    window.onbeforeunload = null;
                    window.location.href = "deliveryOrder.jsp";
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
                    window.location.href = "deliveryOrder.jsp?editingLineItem=" + lineItemID;
                }

                function saveEditLineItem(lineItemID) {
                    window.onbeforeunload = null;
                    doManagement.lineItemID.value = lineItemID;
                    doManagement.itemName.value = document.getElementById("itemName" + lineItemID).value;
                    doManagement.itemDescription.value = document.getElementById("itemDescription" + lineItemID).value;
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
                        <h2>Delivery Order: <%if (deliveryOrder != null && deliveryOrder.getDeliveryOrderNumber() != null) {
                                out.print(deliveryOrder.getDeliveryOrderNumber());
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
                                <li><span><a href= "../OrderManagementController?target=RetrieveSCO&id=<%=deliveryOrder.getSalesConfirmationOrder().getId()%>"><%=deliveryOrder.getSalesConfirmationOrder().getSalesConfirmationOrderNumber()%></a></span></li>
                                <li><span><a href= "deliveryOrders.jsp">Delivery Orders</a></span></li>
                                            <%} else if (previousMgtPage.equals("deliveryOrders")) {%>
                                <li><span><a href= "../DeliveryOrderManagementController?target=ListAllDO">Delivery Orders</a></span></li>  
                                            <%} else if (previousMgtPage.equals("soa")) {%>
                                <li><span><a href= "../StatementOfAccountManagementController?target=ListAllSOA">Statement of Accounts</a></span></li>
                                <li><span><a href= "../StatementOfAccountManagementController?target=RetrieveSOA&id=<%=deliveryOrder.getSalesConfirmationOrder().getCustomer().getId()%>"><%=deliveryOrder.getSalesConfirmationOrder().getCustomer().getCustomerName()%></a></span></span></li>
                                            <%}%>
                                <li><span>DO &nbsp;&nbsp</span></li>
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
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Delivery Order</h2>
                                                <%
                                                    if (deliveryOrder != null) {
                                                        out.print("<input type='text' " + formDisablerFlag + " class='form-control' id='doNumber' name='doNumber' value='" + deliveryOrder.getDeliveryOrderNumber() + "' style='max-width: 300px' required/>");
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
                                                                if (deliveryOrder != null && deliveryOrder.getCustomerPurchaseOrderNumber() != null && !deliveryOrder.getCustomerPurchaseOrderNumber().isEmpty()) {
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
                                                    <th id="cell-qty" class="text-center text-weight-semibold">Quantity</th>
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
                                                        <%if (!editingLineItem.equals("")) {
                                                                out.println("<input type='number' class='form-control' id='input_itemQty' min='0' name='itemQty' disabled/>");
                                                            } else {
                                                                out.println("<input type='number' class='form-control' id='input_itemQty' min='0' name='itemQty'/>");
                                                            }
                                                        %>
                                                    </td>
                                                    <td class="text-center">
                                                        <button class='btn btn-default btn-block' <%=formDisablerFlag%> onclick='javascript:addLineItemToExistingDO(<%=deliveryOrder.getId()%>)'>Add Item</button>
                                                    </td>
                                                </tr>

                                                <!-- loop line item page -->
                                                <%
                                                    if (deliveryOrder != null && deliveryOrder.getItems() != null) {
                                                        for (int i = 0; i < deliveryOrder.getItems().size(); i++) {
                                                            if (!editingLineItem.isEmpty() && editingLineItem.equals(deliveryOrder.getItems().get(i).getId() + "")) {
                                                                //Print editable fields
                                                %>
                                                <tr>
                                                    <td>
                                                        <input type='text' class='form-control' name='itemName' id='itemName<%=deliveryOrder.getItems().get(i).getId()%>' value='<%=deliveryOrder.getItems().get(i).getItemName()%>'/>
                                                    </td>
                                                    <td>
                                                        <textarea class='form-control' rows='5' name='itemDescription' id='itemDescription<%=deliveryOrder.getItems().get(i).getId()%>'><%=deliveryOrder.getItems().get(i).getItemDescription()%></textarea>
                                                    </td>
                                                    <td class="text-center">
                                                        <input type='number' class='form-control' id='itemQty<%=deliveryOrder.getItems().get(i).getId()%>' min='0' name='itemQty' value='<%=deliveryOrder.getItems().get(i).getItemQty()%>'/>
                                                    </td>
                                                    <% //Print buttons for current editing line item
                                                        out.print("<td class='text-center'><div class='btn-group'><button class='btn btn-default' type='button' onclick='javascript:saveEditLineItem(" + deliveryOrder.getItems().get(i).getId() + ")'>Save</button>&nbsp;");
                                                        out.print("<button class='btn btn-default' type='button' onclick='javascript:back2()' >Back</button></div></td>");
                                                    %>
                                                </tr> 
                                                <%
                                                            } else {
                                                                //Print normal text
                                                                out.print("<tr>");
                                                                out.print("<td class='text-weight-semibold text-dark'>" + deliveryOrder.getItems().get(i).getItemName() + "</td>");
                                                                out.print("<td>" + deliveryOrder.getItems().get(i).getItemDescription().replaceAll("\\r", "<br>") + "</td>");
                                                                out.print("<td class='text-center'>" + deliveryOrder.getItems().get(i).getItemQty() + "</td>");
                                                                out.print("<td class='text-center'><div class='btn-group'><button " + formDisablerFlag + " class='btn btn-default' type='button' onclick='javascript:editLineItem(" + deliveryOrder.getItems().get(i).getId() + ")'>Edit</button>&nbsp;");
                                                                out.print("<button " + formDisablerFlag + " class='btn btn-default' onclick='javascript:removeLineItem(" + deliveryOrder.getItems().get(i).getId() + ")'>Del</button></div></td>");
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
                                                        out.print("Remarks: ");
                                                        out.print(deliveryOrder.getRemarks().replaceAll("\\r", "<br>"));
                                                    }
                                                %>
                                            </div>
                                            <div class="col-sm-3"></div>
                                            <div class="col-sm-4">
                                                <table class="table h5 text-dark">
                                                    <tbody>

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
                                            <%
                                                if (deliveryOrder != null) {
                                                    out.print("<button type='button' class='modal-with-move-anim btn btn-danger' href='#modalRemove'>Delete</button>");
                                                    out.print("<button " + formDisablerFlag + " class='btn btn-success' onclick='javascript:updateDO();'>Save</button>");
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
                        <input type="hidden" name="source" value="">    
                        <input type="hidden" name="id" value="">    
                    </form>
                    <!-- end: page -->

                    <%if (deliveryOrder != null) {%>
                    <div id="modalEditForm" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="editContactForm" action="../DeliveryOrderManagementController" class="form-horizontal mb-lg">
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
                            <form name="editNotesForm" action="../DeliveryOrderManagementController" class="form-horizontal mb-lg">
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
                                        <p>Are you sure that you want to cancel this Delivery Order?<br> All associated Invoice/Payment records will also be cancelled together!</p>
                                    </div>
                                </div>
                            </div>
                            <footer class="panel-footer">
                                <div class="row">
                                    <div class="col-md-12 text-right">
                                        <button class="btn btn-primary modal-confirm" onclick="javascript:deleteDO();">Confirm</button>
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

