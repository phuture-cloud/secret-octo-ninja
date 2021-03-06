package OrderManagement;

import EntityManager.Contact;
import EntityManager.CreditNote;
import EntityManager.Customer;
import EntityManager.Invoice;
import EntityManager.LineItem;
import EntityManager.OrderNumbers;
import EntityManager.PaymentRecord;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import PaymentManagement.PaymentManagementBeanLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class InvoiceManagementBean implements InvoiceManagementBeanLocal {

    public InvoiceManagementBean() {
    }
    @Resource
    private EJBContext context;
    @PersistenceContext
    private EntityManager em;

    @EJB
    private PaymentManagementBeanLocal pmbl;

    private static final Double gstRate = 7.0;//7%

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper createInvoice(Long salesConfirmationOrderID, Date invoiceDate) {
        System.out.println("InvoiceManagementBean: createInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to create a new invoice. The selected SCO may have been deleted while the invoice is being created. Please try again.");
                return result;
            }
//            ReturnHelper uniqueResult = checkIfInvoiceNumberIsUnique(invoiceNumber);
//            if (!uniqueResult.getResult()) {
//                uniqueResult.setDescription("Failed to save the invoice as the invoice number is already in use.");
//                return uniqueResult;
//            }
            //Create new invoice
            Invoice invoice = new Invoice(getNewInvoiceNumber());
            invoice.setSalesConfirmationOrder(sco);
            invoice.setDateSent(invoiceDate);
            invoice.setTaxRate(gstRate);
            //Copy SCO details
            invoice.setTerms(sco.getTerms());
            invoice.setEstimatedDeliveryDate(sco.getEstimatedDeliveryDate());
            invoice.setCustomerPurchaseOrderNumber(sco.getCustomerPurchaseOrderNumber());
            //Copy contacts details from SCO to use as default for billing contact
            invoice.setCustomerName(sco.getCustomerName());
            invoice.setContactName(sco.getContactName());
            invoice.setContactEmail(sco.getContactEmail());
            invoice.setContactAddress(sco.getContactAddress());
            invoice.setContactMobileNo(sco.getContactMobileNo());
            invoice.setContactOfficeNo(sco.getContactOfficeNo());
            invoice.setContactFaxNo(sco.getContactFaxNo());
            //Update due date based on terms
            Calendar c = new GregorianCalendar();
            c.setTime(invoiceDate);
            c.add(Calendar.DAY_OF_YEAR, sco.getTerms());
            Date dateDue = c.getTime();
            invoice.setDateDue(dateDue);
            em.persist(invoice);
            //Copy line items from SCO
            replaceInvoiceLineItemWithSCOitems(sco.getId(), invoice.getId(), false);
            //Update SCO list of invoice
            List<Invoice> invoices = sco.getInvoices();
            invoices.add(invoice);
            sco.setInvoices(invoices);
            sco.setNumOfInvoices(sco.getNumOfInvoices() + 1);
            em.merge(sco);
            result.setID(invoice.getId());
            //Update SCO total amount invoiced
            sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(salesConfirmationOrderID));
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Invoice created.");
            return result;
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: createInvoice() could not find one or more ID(s).");
            result.setDescription("Failed to create the invoice. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: createInvoice() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoice(Long invoiceID, Date invoiceDate, Date invoicePaid, Date estimatedDeliveryDate, Integer terms, String customerPurchaseOrderNumber, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
//            ReturnHelper uniqueResult = checkIfInvoiceNumberIsUnique(newInvoiceNumber);
//            if (!uniqueResult.getResult() && !newInvoiceNumber.equals(invoice.getInvoiceNumber())) {
//                uniqueResult.setDescription("Failed to save the invoice as the invoice number is already in use.");
//                return uniqueResult;
//            }
            //Update fields 
//            invoice.setInvoiceNumber(newInvoiceNumber);
            invoice.setTerms(terms);
            invoice.setEstimatedDeliveryDate(estimatedDeliveryDate);
            invoice.setCustomerPurchaseOrderNumber(customerPurchaseOrderNumber);
            if (invoiceDate != null) {
                invoice.setStatusAsSent();
                invoice.setDateSent(invoiceDate);
                //Update due date based on terms
                Calendar c = new GregorianCalendar();
                c.setTime(invoiceDate);
                c.add(Calendar.DAY_OF_YEAR, terms);
                Date dateDue = c.getTime();
                invoice.setDateDue(dateDue);
            }
            if (invoicePaid != null) {
                invoice.setStatusAsPaid();
                invoice.setDatePaid(invoicePaid);
            } else {
                invoice.setDatePaid(null);
                if (invoice.getDateSent() != null) {
                    invoice.setStatusAsSent();
                } else {
                    invoice.setStatusAsCreated();
                }
            }
            em.merge(invoice);
            result.setID(invoice.getId());
            result.setResult(true);
            result.setDescription("Invoice saved.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoice() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoice() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setCustomerName(customerName);
            invoice.setContactAddress(address);
            invoice.setContactEmail(email);
            invoice.setContactOfficeNo(officeNo);
            invoice.setContactFaxNo(faxNo);
            invoice.setContactMobileNo(mobileNo);
            invoice.setContactName(contactName);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, Long customerID, Long contactID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT c FROM Contact c WHERE c.id=:id");
            q.setParameter("id", contactID);
            Contact contact = (Contact) q.getSingleResult();
            q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id");
            q.setParameter("id", customerID);
            Customer customer = (Customer) q.getSingleResult();
            invoice.setCustomerName(customer.getCustomerName());
            invoice.setContactAddress(contact.getAddress());
            invoice.setContactEmail(contact.getEmail());
            invoice.setContactOfficeNo(contact.getOfficeNo());
            invoice.setContactFaxNo(contact.getFaxNo());
            invoice.setContactMobileNo(contact.getMobileNo());
            invoice.setContactName(contact.getName());
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice or customer or contact selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceRemarks(Long invoiceID, String remarks, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceRemarks() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setRemarks(remarks);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceRemarks() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceRemarks() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceNotes(Long invoiceID, String notes, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceNotes() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setNotes(notes);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceNotes() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper deleteInvoice(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: deleteInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            if (!invoice.getIsDeleted()) {
                invoice.setIsDeleted(true);
                em.merge(invoice);
                SalesConfirmationOrder sco = invoice.getSalesConfirmationOrder();
                sco.setNumOfInvoices(sco.getNumOfInvoices() - 1);
                //Update SCO total amount invoiced
                sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(sco.getId()));
                em.merge(sco);
                //Delete all the payment records
                List<PaymentRecord> paymentRecords = invoice.getPaymentRecords();
                for (PaymentRecord paymentRecord : paymentRecords) {
                    pmbl.deletePayment(paymentRecord.getId());
                }
                //Delete all the credit notes
                List<CreditNote> creditNotes = invoice.getCreditNotes();
                for (CreditNote creditNote : creditNotes) {
                    pmbl.deleteCreditNote(creditNote.getId());
                }
            }
            result.setResult(true);
            result.setDescription("Invoice deleted.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: deleteInvoice() failed");
            result.setDescription("Failed to delete the invoice due to internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public ReturnHelper voidInvoice(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: voidInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            if (!invoice.getStatus().equals("Voided")) {
                SalesConfirmationOrder sco = invoice.getSalesConfirmationOrder();
                //Update SCO total amount invoiced
                sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(sco.getId()));
                em.merge(sco);
                //Delete all the payment records
                for (PaymentRecord paymentRecord : invoice.getPaymentRecords()) {
                    pmbl.deletePayment(paymentRecord.getId());
                }
                invoice.setStatusAsVoided();
                //Void all the credit notes
                List<CreditNote> creditNotes = invoice.getCreditNotes();
                for (CreditNote creditNote : creditNotes) {
                    pmbl.voidCreditNote(creditNote.getId());
                }
                em.merge(invoice);
            }
            result.setResult(true);
            result.setDescription("Invoice voided.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: voidInvoice() failed");
            result.setDescription("Failed to delete the invoice due to internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfInvoiceisEditable(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: checkIfInvoiceisEditable() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (!adminOverwrite) {//If not admin account
                //Check if Invoice status is sent or paid. Prevent editing if it is already sent.
                //if (invoice.getStatus().equals("Sent")||invoice.getStatus().equals("Paid")) {
                //    result.setDescription("Invoice can not be edited/deleted as the first invoice has already been issued.");
                //    return result;
                //}
            }
            if (invoice.getIsDeleted()) {
                result.setDescription("Invoice can not be edited/deleted as it has already been deleted.");
                return result;
            }
            if (invoice.getStatus().equals("Voided")) {
                result.setDescription("Invoice can not be edited/deleted as it has already been voided.");
                return result;
            }
            result.setResult(true);
            result.setDescription("Editable invoice.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: checkIfInvoiceisEditable() can not find invoice");
            result.setDescription("Unable to complete request, invoice not found.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: checkIfInvoiceisEditable() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfInvoiceNumberIsUnique(String invoiceNumber) {
        System.out.println("InvoiceManagementBean: checkIfInvoiceNumberIsUnique() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.invoiceNumber=:number AND s.isDeleted=false");
            q.setParameter("number", invoiceNumber);
            List<Invoice> invoices = q.getResultList();
            if (invoices.size() == 0) {
                result.setResult(true);
                result.setDescription("Invoice number is unique");
                return result;
            } else {
                result.setDescription("Invoice number is already in use.");
                return result;
            }
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: checkIfInvoiceNumberIsUnique() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public Invoice getInvoice(Long invoiceID) {
        System.out.println("InvoiceManagementBean: getInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            return invoice;
        } catch (EntityNotFoundException ex) {
            System.out.println("InvoiceManagementBean: getInvoice(): invoice not found");
            return null;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: getInvoice() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Invoice> listAllInvoice(Long staffID) {
        System.out.println("InvoiceManagementBean: listAllInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
            q.setParameter("staffID", staffID);
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsAdmin()) {
                //List all for admin
                q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false");
            } else {
                //List only those that they create for normal staff
                q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false and s.salesConfirmationOrder.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listAllInvoice() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Invoice> listInvoicesTiedToSCO(Long salesConfirmationOrderID) {
        System.out.println("InvoiceManagementBean: listInvoicesTiedToSCO() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false AND s.salesConfirmationOrder.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listInvoicesTiedToSCO() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Invoice> listInvoicesTiedToCustomer(Long customerID) {
        System.out.println("InvoiceManagementBean: listInvoicesTiedToCustomer() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false AND s.salesConfirmationOrder.customerLink.id=:id");
            q.setParameter("id", customerID);
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listInvoicesTiedToCustomer() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Double getSCOtotalInvoicedAmount(Long salesConfirmationOrderID) {
        System.out.println("InvoiceManagementBean: getSCOtotalInvoicedAmount() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false AND s.salesConfirmationOrder.id=:id AND s.status!='Voided'");
            q.setParameter("id", salesConfirmationOrderID);
            List<Invoice> invoices = q.getResultList();
            Double totalAmount = 0.0;
            for (Invoice i : invoices) {
                totalAmount += i.getTotalPriceBeforeCreditNote();
            }
            return totalAmount;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: getSCOtotalInvoicedAmount() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper replaceInvoiceLineItemWithSCOitems(Long salesConfirmationOrderID, Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: replaceInvoiceLineItemWithSCOitems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (sco.getIsDeleted() || invoice.getIsDeleted()) {
                result.setDescription("Failed to edit a invoice. The selected SCO or invoice may have been deleted while the invoice is being edited..");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            //Delete all the line items in the invoice
            ReturnHelper deleteResult = new ReturnHelper();
            deleteResult = deleteallInvoiceLineItem(invoiceID, adminOverwrite);
            if (!deleteResult.getResult()) {
                return deleteResult;
            }
            //Copy line items from SCO
            List<LineItem> scoLineItems = sco.getItems();
            List<LineItem> invoiceLineItems = new ArrayList<>();
            for (LineItem scoLineItem : scoLineItems) {
                //Create the new line item for invoice
                LineItem item = new LineItem();
                item.setItemName(scoLineItem.getItemName());
                item.setItemDescription(scoLineItem.getItemDescription());
                item.setItemQty(scoLineItem.getItemQty());
                item.setItemUnitPrice(scoLineItem.getItemUnitPrice());
                em.persist(item);
                //Add the line item to the invoice
                invoiceLineItems.add(item);
            }
            invoice.setItems(invoiceLineItems);
            //Update invoice total price & tax
            Double totalPriceBeforeCreditNote = 0.0;
            Double totalTax = 0.0;
            Double totalPriceAfterCreditNote = 0.0;
            for (LineItem curLineItem : invoiceLineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPriceBeforeCreditNote = totalPriceBeforeCreditNote + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPriceBeforeCreditNote(totalPriceBeforeCreditNote);
            invoice.setTotalTax(totalTax);
            totalPriceAfterCreditNote = totalPriceBeforeCreditNote - invoice.getTotalCreditNoteAmount();
            if (totalPriceAfterCreditNote < 0) {
                totalPriceAfterCreditNote = 0.0;
            }
            invoice.setTotalPriceAfterCreditNote(totalPriceAfterCreditNote);
            em.merge(invoice);
            //Update SCO total amount invoiced
            sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(salesConfirmationOrderID));
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Items copied from SCO.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: replaceInvoiceLineItemWithSCOitems() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: replaceInvoiceLineItemWithSCOitems() failed");
            result.setDescription("Failed to edit the invoice due to internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper addInvoiceLineItem(Long invoiceID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: addInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            LineItem lineItem = new LineItem();
            lineItem.setItemName(itemName);
            lineItem.setItemDescription(itemDescription);
            lineItem.setItemQty(itemQty);
            lineItem.setItemUnitPrice(itemUnitPrice);
            em.persist(lineItem);
            List<LineItem> lineItems = invoice.getItems();
            lineItems.add(lineItem);
            invoice.setItems(lineItems);
            //Update invoice total price & tax
            Double totalPriceBeforeCreditNote = 0.0;
            Double totalTax = 0.0;
            Double totalPriceAfterCreditNote = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPriceBeforeCreditNote = totalPriceBeforeCreditNote + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPriceBeforeCreditNote(totalPriceBeforeCreditNote);
            invoice.setTotalTax(totalTax);
            totalPriceAfterCreditNote = totalPriceBeforeCreditNote - invoice.getTotalCreditNoteAmount();
            if (totalPriceAfterCreditNote < 0) {
                totalPriceAfterCreditNote = 0.0;
            }
            invoice.setTotalPriceAfterCreditNote(totalPriceAfterCreditNote);
            em.merge(invoice);
            //Update SCO total amount invoiced
            SalesConfirmationOrder sco = invoice.getSalesConfirmationOrder();
            sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(sco.getId()));
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Item added.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: addInvoiceLineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: addInvoiceLineItem() failed");
            result.setDescription("Unable to add line item, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper updateInvoiceLineItem(Long invoiceID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            lineItem.setItemName(newItemName);
            lineItem.setItemDescription(newItemDescription);
            lineItem.setItemQty(newItemQty);
            lineItem.setItemUnitPrice(newItemUnitPrice);
            em.merge(lineItem);
            List<LineItem> lineItems = invoice.getItems();
            //Update invoice total price & tax
            Double totalPriceBeforeCreditNote = 0.0;
            Double totalTax = 0.0;
            Double totalPriceAfterCreditNote = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPriceBeforeCreditNote = totalPriceBeforeCreditNote + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPriceBeforeCreditNote(totalPriceBeforeCreditNote);
            invoice.setTotalTax(totalTax);
            totalPriceAfterCreditNote = totalPriceBeforeCreditNote - invoice.getTotalCreditNoteAmount();
            if (totalPriceAfterCreditNote < 0) {
                totalPriceAfterCreditNote = 0.0;
            }
            invoice.setTotalPriceAfterCreditNote(totalPriceAfterCreditNote);
            em.merge(invoice);
            //Update SCO total amount invoiced
            SalesConfirmationOrder sco = invoice.getSalesConfirmationOrder();
            sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(sco.getId()));
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Line item updated.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: updateInvoiceLineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice or item selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: updateInvoiceLineItem() failed");
            result.setDescription("Unable to update line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper deleteInvoiceLineItem(Long invoiceID, Long lineItemID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: deleteInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to delete the item as the invoice has been deleted.");
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            List<LineItem> lineItems = invoice.getItems();
            lineItems.remove(lineItem);
            invoice.setItems(lineItems);
            //Update invoice total price & tax
            Double totalPriceBeforeCreditNote = 0.0;
            Double totalTax = 0.0;
            Double totalPriceAfterCreditNote = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPriceBeforeCreditNote = totalPriceBeforeCreditNote + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPriceBeforeCreditNote(totalPriceBeforeCreditNote);
            invoice.setTotalTax(totalTax);
            totalPriceAfterCreditNote = totalPriceBeforeCreditNote - invoice.getTotalCreditNoteAmount();
            if (totalPriceAfterCreditNote < 0) {
                totalPriceAfterCreditNote = 0.0;
            }
            invoice.setTotalPriceAfterCreditNote(totalPriceAfterCreditNote);
            em.merge(invoice);
            em.remove(lineItem);
            //Update SCO total amount invoiced
            SalesConfirmationOrder sco = invoice.getSalesConfirmationOrder();
            sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(sco.getId()));
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Item deleted.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: deleteInvoiceLineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice or item selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: deleteInvoiceLineItem() failed");
            result.setDescription("Unable to delete line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper deleteallInvoiceLineItem(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: deleteallInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            List<LineItem> lineItems = invoice.getItems();
            for (LineItem lineItem : lineItems) {
                lineItems.remove(lineItem);
                em.remove(lineItem);
            }
            invoice.setItems(lineItems);
            invoice.setTotalPriceBeforeCreditNote(0.0);
            invoice.setTotalTax(0.0);
            em.merge(invoice);
            //Update SCO total amount invoiced
            SalesConfirmationOrder sco = invoice.getSalesConfirmationOrder();
            sco.setTotalInvoicedAmount(getSCOtotalInvoicedAmount(sco.getId()));
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Line items deleted.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("InvoiceManagementBean: deleteallInvoiceLineItem() failed");
            result.setDescription("Unable to delete line items, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public List<LineItem> listInvoiceLineItems(Long invoiceID) {
        System.out.println("InvoiceManagementBean: listInvoiceLineItems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            List<LineItem> lineItems = invoice.getItems();
            return lineItems;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listInvoiceLineItems() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Double getInvoiceTotalPriceBeforeCreditNote(Long invoiceID) {
        System.out.println("InvoiceManagementBean: getInvoiceTotalPriceBeforeCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Invoice invoice = em.getReference(Invoice.class, invoiceID);
            Double totalPriceBeforeCreditNote = 0.0;
            for (LineItem lineItem : invoice.getItems()) {
                Double currLineItemTotalPriceBeforeTax = lineItem.getItemUnitPrice() * lineItem.getItemQty();
                totalPriceBeforeCreditNote = totalPriceBeforeCreditNote + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
            }
            return totalPriceBeforeCreditNote;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: getInvoiceTotalPriceBeforeCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper refreshInvoice(Long invoiceID) {
        System.out.println("InvoiceManagementBean: refreshInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT i FROM Invoice i WHERE i.isDeleted=false and i.id=:invoiceID");
            q.setParameter("invoiceID", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            //todo: Recalculate line item?
            //Recalculate totals
            Double totalPriceBeforeCreditNote = getInvoiceTotalPriceBeforeCreditNote(invoiceID);
            Double paymentAmount = pmbl.getInvoiceTotalPaymentAmount(invoiceID);
            Double creditNoteAmount = pmbl.getInvoiceTotalCreditNoteApplied(invoiceID);
            invoice.setTotalCreditNoteAmount(creditNoteAmount);
            Double totalPriceAfterCreditNote = invoice.getTotalPriceBeforeCreditNote() - creditNoteAmount;
            if (totalPriceAfterCreditNote < 0) {
                totalPriceAfterCreditNote = 0.0;
            }
            invoice.setTotalPriceBeforeCreditNote(totalPriceBeforeCreditNote);
            invoice.setTotalPriceAfterCreditNote(totalPriceAfterCreditNote);
            //Recalculate payment amounts
            invoice.setTotalAmountPaid(paymentAmount);
            invoice.setNumOfPaymentRecords(pmbl.listPaymentByInvoice(invoice.getId()).size());
            em.merge(invoice);
            result.setDescription("Invoices refreshed");
            result.setResult(true);
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: refreshInvoice() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper refreshInvoices(Long staffID) {
        System.out.println("InvoiceManagementBean: refreshInvoices() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Staff staff = new Staff();
            Query q;
            if (staffID != null) {
                q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
                q.setParameter("staffID", staffID);
                staff = (Staff) q.getSingleResult();
            }
            if (staffID == null || staff.getIsAdmin()) {
                //Refresh all for admin
                q = em.createQuery("SELECT i FROM Invoice i WHERE i.isDeleted=false");
            } else {
                //Refresh only those that they create for normal staff
                q = em.createQuery("SELECT i FROM Invoice i WHERE i.isDeleted=false and i.salesConfirmationOrder.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<Invoice> invoices = q.getResultList();
            Boolean someFailed = false;
            for (Invoice invoice : invoices) {
                ReturnHelper result2 = refreshInvoice(invoice.getId());
                if (!result2.getResult()) {
                    someFailed = true;
                }
            }
            if (someFailed) {
                result.setDescription("Some invoices could not be refreshed");
            } else {
                result.setDescription("Invoices refreshed");
                result.setResult(true);
            }
            return result;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: refreshInvoices() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private String getNewInvoiceNumber() {
        System.out.println("InvoiceManagementBean: getNewInvoiceNumber() called");
        Query q = em.createQuery("SELECT e FROM OrderNumbers e");
        q.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        OrderNumbers orderNumbers = (OrderNumbers) q.getResultList().get(0);
        Long nextOrderNumber = orderNumbers.getNextInvoice();
        orderNumbers.setNextInvoice(nextOrderNumber + 1);
        orderNumbers.setLastGeneratedInvoice(new Date());
        em.merge(orderNumbers);
        return nextOrderNumber.toString();
    }
}
