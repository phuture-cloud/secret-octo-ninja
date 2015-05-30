<%@page import="EntityManager.SalesConfirmationOrder"%>
<%@page import="EntityManager.Contact"%>
<%@page import="EntityManager.Customer"%>
<%@page import="EntityManager.Staff"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
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
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <section class="body">
            <script>
                $(document).ready(function () {
                    $('#input_itemQty').change(function () {
                        var itemUnitPrice = parseFloat($('#input_itemUnitPrice').val());
                        var itemQty = parseInt($('#input_itemQty').val());
                        var itemAmount = itemUnitPrice * itemQty;
                        $('#input_itemAmount').val(itemAmount);
                    });
                });

                function back() {
                    window.location.href = "../OrderManagementController?target=ListAllSCO";
                }

                function getCustomerContacts() {
                    window.onbeforeunload = null;
                    var customerID = document.getElementById("customerList").value;
                    var scoNumber = document.getElementById("scoNumber").value;
                    if (customerID !== "") {
                        window.location.href = "../OrderManagementController?target=ListCustomerContacts&id=" + customerID + "&scoNumber=" + scoNumber;
                    }
                }

                function getCustomerContact() {
                    window.onbeforeunload = null;
                    var customerID = document.getElementById("customerList").value;
                    var scoNumber = document.getElementById("scoNumber").value;
                    if (customerID !== "") {
                        var contactID = document.getElementById("customerContactid").value;
                        window.location.href = "scoManagement_add.jsp?selectedCustomerID=" + customerID + "&selectedContactID=" + contactID + "&scoNumber=" + scoNumber;
                    }
                }

                function addLineItemToNewSCO() {
                    window.onbeforeunload = null;
                    scoManagement.target.value = "AddLineItemToNewSCO";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function addLineItemToExistingSCO() {
                    window.onbeforeunload = null;
                }
                function deleteSCO(id) {
                    window.onbeforeunload = null;
                    scoManagement.id.value = id;
                    scoManagement.target.value = "DeleteSCO";
                    document.scoManagement.action = "../OrderManagementController";
                    document.scoManagement.submit();
                }

                function saveSCO() {
                }

                window.onbeforeunload = function () {
                    return 'There are unsaved changes to this page. If you continue, you will lose them';
                };
            </script>
            <jsp:include page="../header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Create Sales Confirmation Order</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li>
                                    <a href="workspace.jsp">
                                        <i class="fa fa-home"></i>
                                    </a>
                                </li>
                                <li><span><a href= "../OrderManagementController?target=ListAllSCO">Sales Confirmation Order</a></span></li>
                                <li><span>New Sales Confirmation Order &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <form name="scoManagement">
                        <section class="panel">
                            <div class="panel-body">
                                <div class="invoice">
                                    <header class="clearfix">
                                        <div class="row">
                                            <div class="col-sm-6 mt-md">
                                                <h2 class="h2 mt-none mb-sm text-dark text-weight-bold">Sales Confirmation Order</h2>
                                                <%
                                                    String scoNumber = request.getParameter("scoNumber");
                                                    if (scoNumber != null && !scoNumber.isEmpty()) {
                                                        out.print("<input type='text' class='form-control' id='scoNumber' name='scoNumber' value='" + scoNumber + "' style='max-width: 300px' required/>");
                                                    } else if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                        out.print("<input type='text' class='form-control' id='scoNumber' name='scoNumber' value='" + sco.getSalesConfirmationOrderNumber() + "' style='max-width: 300px' required/>");
                                                    } else {
                                                        out.print("<input type='text' class='form-control' id='scoNumber' name='scoNumber' placeholder='Enter SCO number' style='max-width: 300px' required/>");
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
                                                        <div class="col-md-6">
                                                            <%
                                                                if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                                    out.print(sco.getCustomerName());
                                                                    out.print("<br>" + sco.getContactName());
                                                                    out.print("<br>" + sco.getContactAddress());
                                                                    out.print("<br>" + sco.getContactOfficeNo());
                                                                    out.print("<br>" + sco.getContactFaxNo());
                                                                    out.print("<br>" + sco.getContactMobileNo());
                                                                } else {
                                                            %>
                                                            <select id="customerList" name="customerID" data-plugin-selectTwo class="form-control populate" onchange="javascript:getCustomerContacts()" equired>
                                                                <option value="">Select a customer</option>
                                                                <%
                                                                    String selectedCustomerID = request.getParameter("selectedCustomerID");
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
                                                        <div class="col-md-6">
                                                            <select id="customerContactid" name="contactID" data-plugin-selectTwo class="form-control populate"  onchange="javascript:getCustomerContact()" required>
                                                                <option value="">Select a contact</option>
                                                                <%
                                                                    String selectedContactID = request.getParameter("selectedContactID");
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
                                                                    out.println("Address: " + contact.getAddress());
                                                                    out.println("<br/>Telephone: " + contact.getOfficeNo());
                                                                    out.println("<br/>Fasimile: " + contact.getFaxNo());
                                                                    out.println("<br/>Mobile: " + contact.getMobileNo() + "<br/><br/>");
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
                                                        <span class="text-dark">Salesperson </span>
                                                        <span class="value" style="min-width: 100px; font-size: 13pt;">
                                                            <%=staff.getName()%>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Date:</span>
                                                        <span class="value" style="min-width: 100px">
                                                            <%
                                                                String selectedDate = request.getParameter("selectedDate");
                                                                if (selectedDate != null && !selectedDate.isEmpty()) {
                                                                    out.print("<input name='date' type='text' data-plugin-datepicker class='form-control' value='" + selectedDate + "' required>");
                                                                } else if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                                    out.print("<input name='date' type='text' data-plugin-datepicker class='form-control' value='" + sco.getSalesConfirmationOrderDate() + "' required>");
                                                                } else {
                                                                    out.print("<input name='date' type='text' data-plugin-datepicker class='form-control' required placeholder='dd/mm/yyyy'>");
                                                                }
                                                            %>
                                                        </span>
                                                    </p>
                                                    <p class="mb-none">
                                                        <span class="text-dark">Terms:</span>
                                                        <span class="value" style="min-width: 100px">
                                                            <select name="terms" class="form-control input-sm mb-md" required>
                                                                <%
                                                                    String selectedTerms = request.getParameter("selectedTerms");
                                                                    if (selectedTerms != null && !selectedTerms.isEmpty()) {
                                                                        if (selectedTerms.equals("0")) {
                                                                            out.print("<option value='0' selected>COD</option>");
                                                                        } else if (selectedTerms.equals("14")) {
                                                                            out.print("<option value='14' selected>14 Days</option>");
                                                                        } else if (selectedTerms.equals("30")) {
                                                                            out.print("<option value='30' selected>30 Days</option>");
                                                                        }
                                                                    } else if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                                        if (sco.getTerms() == 0) {
                                                                            out.print("<option value='0' selected>COD</option>");
                                                                        } else if (sco.getTerms() == 14) {
                                                                            out.print("<option value='14' selected>14 Days</option>");
                                                                        } else if (sco.getTerms() == 30) {
                                                                            out.print("<option value='30' selected>30 Days</option>");
                                                                        }
                                                                    }

                                                                    out.print("<option value=''>Select</option>");
                                                                    out.print("<option value='0'>COD</option>");
                                                                    out.print("<option value='14'>14 Days</option>");
                                                                    out.print("<option value='30'>30 Days</option>");
                                                                %>
                                                            </select>
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
                                                        <input type="text" class="form-control" name="itemName" required/>
                                                    </td>
                                                    <td>
                                                        <input type="text" class="form-control" name="itemDescription" required/>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type="number" class="form-control" id="input_itemUnitPrice" name="itemUnitPrice" step="any" required/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <input type="number" class="form-control" id="input_itemQty" name="itemQty" required/>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="input-group">
                                                            <span class="input-group-addon">
                                                                <i class="fa fa-dollar"></i>
                                                            </span>
                                                            <input type="number" class="form-control" id="input_itemAmount" name="itemAmount" step="any" disabled/>
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <%                                                            if (scoID == null || scoID.isEmpty()) {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToNewSCO()'>Add Item</button>");
                                                            } else {
                                                                out.print("<button class='btn btn-default btn-block' onclick='javascript:addLineItemToExistingSCO()'>Add Item</button>");
                                                            }%>
                                                    </td>
                                                </tr>

                                                <!-- loop line item page -->
                                                <%
                                                    if (sco != null && scoID != null && !scoID.isEmpty()) {
                                                        for (int i = 0; i < sco.getItems().size(); i++) {
                                                            out.print("<tr>");
                                                            out.print("<td class='text-weight-semibold text-dark'>" + sco.getItems().get(i).getItemName() + "</td>");
                                                            out.print("<td>" + sco.getItems().get(i).getItemDescription() + "</td>");
                                                            out.print("<td class='text-center'>" + sco.getItems().get(i).getItemUnitPrice() + "</td>");
                                                            out.print("<td class='text-center'>" + sco.getItems().get(i).getItemQty() + "</td>");
                                                            out.print("<td class='text-center'>" + (sco.getItems().get(i).getItemUnitPrice() * sco.getItems().get(i).getItemQty()) + "</td>");
                                                            out.print("<td><div class='btn-group' role='group' aria-label='...'>");
                                                            out.print("<button class='btn btn-default' onclick=''>Edit</button>");
                                                            out.print("<button class='btn btn-default' onclick=''>Remove</button>");
                                                            out.print("</div></td>");
                                                            out.print("</tr>");
                                                        }
                                                    }
                                                %>
                                                <!-- end loop line item page -->

                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="invoice-summary">
                                        <div class="row">
                                            <div class="col-sm-8">
                                                <p>Terms & Conditions</p>
                                                <ul>
                                                    <li>Acceptance of this Sales Order constitutes a contract between the buyer & Phuture International Pte Ltd <br>whereby buyer will adhere to conditions stated on this Sales Order</li>
                                                    <li>Buyer shall be liable for at least 50% of total sales amount if buyer opt to cancel the order</li>
                                                </ul>
                                            </div>
                                            <div class="col-sm-4">
                                                <table class="table h5 text-dark">
                                                    <tbody>
                                                        <tr class="b-top-none">
                                                            <td colspan="2">Subtotal</td>
                                                            <td class="text-left">$<%out.print((sco.getTotalPrice() / 107) * 100);%></td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="2">7% GST</td>
                                                            <td class="text-left">$<%out.print((sco.getTotalPrice() / 107) * 7);%></td>
                                                        </tr>
                                                        <tr class="h4">
                                                            <td colspan="2">Total (SGD)</td>
                                                            <td class="text-left">$<%=sco.getTotalPrice()%></td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="text-right mr-lg">
                                    <a href="#" target="_blank" class="btn btn-default"><i class="fa fa-print"></i> Print PDF</a>
                                    <button type="button" class="modal-with-move-anim btn btn-danger"  href="#modalRemove">Delete</button>
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
                                                        <p>Are you sure that you want to delete this Sales Confirmation Order?</p>
                                                    </div>
                                                </div>
                                            </div>
                                            <footer class="panel-footer">
                                                <div class="row">
                                                    <div class="col-md-12 text-right">
                                                        <button class="btn btn-primary modal-confirm" onclick="deleteSCO(<%=scoID%>)">Confirm</button>
                                                        <button class="btn btn-default modal-dismiss">Cancel</button>
                                                    </div>
                                                </div>
                                            </footer>
                                        </section>
                                    </div>
                                    <button class="btn btn-primary" onclick="javascript:generatePO()">Generate PO</button>
                                    <button class="btn btn-primary" onclick="javascript:generateDO()">Generate DO</button>
                                    <button class="btn btn-success" onclick="javascript:saveSCO()">Save</button>
                                </div>
                            </div>

                        </section>
                        <input type="hidden" name="salesStaffID" value="<%=staff.getId()%>">    
                        <input type="hidden" name="target" value="">    
                        <input type="hidden" name="id" value="">    
                    </form>
                    <!-- end: page -->

                </section>
            </div>
        </section>
        <jsp:include page="../foot.html" />

    </body>
</html>
<%
    }
%>