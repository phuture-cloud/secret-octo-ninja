<%@page import="EntityManager.Contact"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="EntityManager.CreditNote"%>
<%@page import="java.util.List"%>
<%@page import="EntityManager.Staff"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Staff staff = (Staff) (session.getAttribute("staff"));
    List<CreditNote> creditNotes = (List<CreditNote>) (session.getAttribute("listOfCreditNotes"));
    String customerID = request.getParameter("id");
    String customerName = request.getParameter("name");
    if (session.isNew()) {
        response.sendRedirect("../index.jsp?errMsg=Invalid Request. Please login.");
    } else if (staff == null) {
        response.sendRedirect("../index.jsp?errMsg=Session Expired.");
    } else if (customerID == null || customerName == null) {
        response.sendRedirect("../CustomerManagement/customerManagement.jsp?errMsg=An error has occured.");
    } else {
%>
<!doctype html>
<html class="fixed">
    <head>
        <jsp:include page="../jspIncludePages/head.html" />
    </head>
    <body onload="alertFunc()">
        <jsp:include page="../displayNotification.jsp" />
        <script>
            function viewCreditNote(id) {
                window.location.href = "../PurchaseOrderManagementController?target=RetrievePO&id=" + id;
            }

            function back() {
                window.location.href = "../CustomerManagementController?target=ListAllCustomer";
            }
        </script>

        <section class="body">
            <jsp:include page="../jspIncludePages/header.jsp" />

            <div class="inner-wrapper">
                <jsp:include page="../jspIncludePages/sidebar.jsp" />
                <section role="main" class="content-body">
                    <header class="page-header">
                        <h2>Credit Notes</h2>
                        <div class="right-wrapper pull-right">
                            <ol class="breadcrumbs">
                                <li><a href="../AccountManagement/workspace.jsp"><i class="fa fa-home"></i></a></li>
                                <li><span><a href= "../CustomerManagementController?target=ListAllCustomer">Customer Management</a></span></li>
                                <li><span>Credit Notes &nbsp;&nbsp</span></li>
                            </ol>
                        </div>
                    </header>

                    <!-- start: page -->
                    <section class="panel">
                        <header class="panel-heading">
                            <h2 class="panel-title"><%=customerName%> - Credit Notes</h2>
                        </header>
                        <div class="panel-body">
                            <button type='button' class='btn btn-primary modal-with-form' href='#modalGenerateCN'>Generate Credit Note</button>
                            <input class="btn btn-default" type="button" value="Back" onclick="back()"  />
                            <br><br>

                            <form name="creditNotesForm">
                                <table class="table table-bordered table-striped mb-none" id="datatable-default">
                                    <thead>
                                        <tr>
                                            <th>Credit Note No.</th>
                                            <th>Credit Note Date</th>
                                            <th>Amount</th>
                                            <th>Applied To</th>
                                            <th>Date Applied</th>
                                            <th style="width: 400px;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            if (creditNotes != null) {
                                                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                                for (int i = 0; i < creditNotes.size(); i++) {
                                        %>
                                        <tr>        
                                            <td><%=creditNotes.get(i).getCreditNoteNumber()%></td>
                                            <td><%=DATE_FORMAT.format(creditNotes.get(i).getDateIssued())%></td>
                                            <td><%=formatter.format(creditNotes.get(i).getCreditAmount())%></td>
                                            <td>
                                                <%
                                                    if (creditNotes.get(i).getAppliedToInvoice() != null && creditNotes.get(i).getAppliedToInvoice().getInvoiceNumber() != null) {
                                                        out.print(creditNotes.get(i).getAppliedToInvoice().getInvoiceNumber());
                                                    }
                                                %>
                                            </td>
                                            <td>
                                                <%
                                                    DATE_FORMAT = new SimpleDateFormat("d MMM yyyy");
                                                    if (creditNotes.get(i).getDateUsed() != null) {
                                                        out.print(creditNotes.get(i).getDateUsed());
                                                    }
                                                %>
                                            </td>
                                            <td><button type="button" class="btn btn-default btn-block" onclick="javascript:viewCreditNote('<%=creditNotes.get(i).getId()%>')">Update</button></td>
                                        </tr>
                                        <%
                                                }
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </form>
                        </div>
                    </section>


                    <div id="modalGenerateCN" class="modal-block modal-block-primary mfp-hide">
                        <section class="panel">
                            <form name="addPaymentForm" action="../PaymentManagementController" class="form-horizontal mb-lg">
                                <header class="panel-heading">
                                    <h2 class="panel-title">Generate Credit Note</h2>
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Amount <span class="required">*</span></label>
                                        <div class="col-sm-9">
                                            <input type="number" class="form-control" id="price" name="amount" min="0" step="0.01" size="4" title="CDA Currency Format - no dollar sign and no comma(s) - cents (.##) are optional" required/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Credit Note <br>Issue Date <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <input type="text" name="creditNoteDate" data-plugin-datepicker data-date-format="dd/mm/yyyy" class="form-control" placeholder="dd/mm/yyyy" required/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-3 control-label">Select a contact <span class="required">*</span></label>
                                        <div class="col-md-9">
                                            <select name="contactID" data-plugin-selectTwo class="form-control populate" required>
                                                <option value=""></option>
                                                <%
                                                    List<Contact> contacts = (List<Contact>) (session.getAttribute("contacts"));
                                                    if (contacts != null && contacts.size() > 0) {
                                                        for (int i = 0; i < contacts.size(); i++) {
                                                            out.print("<option value='" + contacts.get(i).getId() + "'>" + contacts.get(i).getName() + "</option>");
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>

                                    <br>
                                    <input type="hidden" name="id" value="<%=customerID%>">
                                    <input type="hidden" name="name" value="<%=customerName%>">
                                    <input type="hidden" name="target" value="GenerateCreditNote">
                                </div>
                                <footer class="panel-footer">
                                    <div class="row">
                                        <div class="col-md-12 text-right">
                                            <button class="btn btn-success" type="submit">Generate Credit Note</button>
                                            <button class="btn btn-default modal-dismiss">Cancel</button>
                                        </div>
                                    </div>
                                </footer>
                            </form>
                        </section>
                    </div>

                </section>
            </div>
        </section>
        <jsp:include page="../jspIncludePages/foot.html" />
    </body>
</html>
<%}%>

