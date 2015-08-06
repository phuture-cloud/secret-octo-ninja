package PaymentManagement;

import EntityManager.Contact;
import EntityManager.CreditNote;
import EntityManager.Customer;
import EntityManager.Invoice;
import EntityManager.PaymentRecord;
import EntityManager.ReturnHelper;
import OrderManagement.InvoiceManagementBeanLocal;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PaymentManagementBean implements PaymentManagementBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private EJBContext context;
    
    @EJB
    private InvoiceManagementBeanLocal imbl;

    public PaymentManagementBean() {
    }

    @Override
    public ReturnHelper addPayment(Long invoiceID, Double amount, Date date, String paymentMethod, String paymentReferenceNumber, String notes) {
        System.out.println("PaymentManagementBean: addPayment() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            PaymentRecord paymentRecord = new PaymentRecord();
            Invoice invoice = em.getReference(Invoice.class, invoiceID);
            if (invoice.getIsDeleted()) {
                result.setDescription("Unable to attach payment record to invoice as the invoice has been deleted.");
                return result;
            }
            Customer customer = invoice.getSalesConfirmationOrder().getCustomer();
            paymentRecord.setCustomer(customer);
            paymentRecord.setInvoice(invoice);
            paymentRecord.setAmount(amount);
            paymentRecord.setPaymentDate(date);
            if (paymentMethod == null) {
                paymentMethod = "";
            }
            paymentRecord.setPaymentMethod(paymentMethod);
            if (paymentReferenceNumber == null) {
                paymentReferenceNumber = "";
            }
            paymentRecord.setPaymentReferenceNumber(paymentReferenceNumber);
            if (notes == null) {
                notes = "";
            }
            paymentRecord.setNotes(notes);
            em.persist(paymentRecord);
            //Update customer records
            List<PaymentRecord> paymentRecords = customer.getPaymentRecords();
            paymentRecords.add(paymentRecord);
            customer.setPaymentRecords(paymentRecords);
            em.merge(customer);
            //Update invoice record
            paymentRecords = invoice.getPaymentRecords();
            paymentRecords.add(paymentRecord);
            invoice.setPaymentRecords(paymentRecords);
            invoice.setNumOfPaymentRecords(invoice.getNumOfPaymentRecords() + 1);
            em.merge(invoice);
            //Update invoice total amount paid
            invoice.setTotalAmountPaid(getInvoiceTotalPaymentAmount(invoice.getId()));
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Payment record added.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: addPayment(): Invoice not found");
            result.setDescription("Invoice does not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: addPayment() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updatePayment(Long paymentID, Double amount, Date date, String paymentMethod, String paymentReferenceNumber, String notes) {
        System.out.println("PaymentManagementBean: updatePayment() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            PaymentRecord paymentRecord = em.getReference(PaymentRecord.class, paymentID);
            if (paymentRecord.getIsDeleted()) {
                result.setDescription("Payment record has been deleted and cannot be updated.");
                return result;
            }
            paymentRecord.setAmount(amount);
            paymentRecord.setPaymentDate(date);
            if (paymentMethod == null) {
                paymentMethod = "";
            }
            paymentRecord.setPaymentMethod(paymentMethod);
            if (paymentReferenceNumber == null) {
                paymentReferenceNumber = "";
            }
            paymentRecord.setPaymentReferenceNumber(paymentReferenceNumber);
            if (notes == null) {
                notes = "";
            }
            paymentRecord.setNotes(notes);
            em.merge(paymentRecord);
            //Update invoice total amount paid
            Invoice invoice = paymentRecord.getInvoice();
            invoice.setTotalAmountPaid(getInvoiceTotalPaymentAmount(invoice.getId()));
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Payment record updated.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: updatePayment(): Payment record not found");
            result.setDescription("Invoice does not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: updatePayment() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper deletePayment(Long paymentID) {
        System.out.println("PaymentManagementBean: deletePayment() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            PaymentRecord paymentRecord = em.getReference(PaymentRecord.class, paymentID);
            if (!paymentRecord.getIsDeleted()) {
                paymentRecord.setIsDeleted(true);
                em.merge(paymentRecord);
                Invoice invoice = paymentRecord.getInvoice();
                invoice.setNumOfPaymentRecords(invoice.getNumOfPaymentRecords() - 1);
                em.merge(invoice);
                //Update invoice total amount paid
                invoice.setTotalAmountPaid(getInvoiceTotalPaymentAmount(invoice.getId()));
                em.merge(invoice);
            }
            result.setResult(true);
            result.setDescription("Payment record deleted.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: deletePayment(): Payment record not found");
            result.setDescription("Invoice does not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: deletePayment() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public PaymentRecord getPayment(Long paymentID) {
        System.out.println("PaymentManagementBean: getPayment() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            PaymentRecord paymentRecord = em.getReference(PaymentRecord.class, paymentID);
            return paymentRecord;
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: getPayment(): Payment record not found");
            result.setDescription("Invoice does not exist.");
            return null;
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: getPayment() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PaymentRecord> listPaymentByCustomer(Long customerID) {
        System.out.println("PaymentManagementBean: listPaymentByCustomer() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT e FROM PaymentRecord e WHERE e.customer.id=:customerID AND e.isDeleted=false");
            q.setParameter("customerID", customerID);
            return q.getResultList();
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: listPaymentByCustomer() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PaymentRecord> listPaymentByInvoice(Long invoiceID) {
        System.out.println("PaymentManagementBean: listPaymentByInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT e FROM PaymentRecord e WHERE e.invoice.id=:invoiceID AND e.isDeleted=false");
            q.setParameter("invoiceID", invoiceID);
            return q.getResultList();
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: listPaymentByInvoice() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PaymentRecord> listAllPayment() {
        System.out.println("PaymentManagementBean: listAllInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT e FROM PaymentRecord e WHERE e.isDeleted=false");
            return q.getResultList();
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: listAllInvoice() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Double getInvoiceTotalPaymentAmount(Long invoiceID) {
        System.out.println("PaymentManagementBean: getInvoiceTotalPaymentAmount() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Invoice invoice = em.getReference(Invoice.class, invoiceID);
            Query q = em.createQuery("SELECT e FROM PaymentRecord e WHERE e.invoice.id=:invoiceID AND e.isDeleted=false");
            q.setParameter("invoiceID", invoiceID);
            List<PaymentRecord> paymentRecords = q.getResultList();
            Double totalAmount = 0.0;
            for (PaymentRecord paymentRecord : paymentRecords) {
                totalAmount += paymentRecord.getAmount();
            }
            for (CreditNote creditNote : invoice.getCreditNotes()) {
                totalAmount -= creditNote.getCreditAmount();
            }
            if (totalAmount < 0) {
                totalAmount = 0.0;
            }
            return totalAmount;
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: getInvoiceTotalPaymentAmount() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper addCreditNote(Long contactID, Double amount, Date creditNoteDate) {
        System.out.println("PaymentManagementBean: addCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            CreditNote creditNote = new CreditNote();
            Contact contact = em.getReference(Contact.class, contactID);
            Customer customer = contact.getCustomer();
            if (customer.getIsDeleted()) {
                result.setDescription("Unable to create a credit note for this customer as the customer has been deleted.");
                return result;
            }
            creditNote.setCustomer(customer);
            if (amount == null || amount < 0.0) {
                result.setDescription("Credit note amount must be more than $0");
                return result;
            }
            creditNote.setCreditAmount(amount);
            creditNote.setContactName(contact.getName());
            creditNote.setContactMobileNo(contact.getMobileNo());
            creditNote.setContactOfficeNo(contact.getOfficeNo());
            creditNote.setContactFaxNo(contact.getFaxNo());
            creditNote.setContactEmail(contact.getEmail());
            creditNote.setContactAddress(contact.getAddress());
            if (creditNoteDate == null) {
                result.setDescription("Credit note date cannot be empty");
                return result;
            }
            creditNote.setDateIssued(creditNoteDate);
            em.persist(creditNote);
            List<CreditNote> creditNotes = customer.getCreditNotes();
            creditNotes.add(creditNote);
            customer.setCreditNotes(creditNotes);
            customer.setTotalAvailableCredits(customer.getTotalAvailableCredits() + amount);
            em.merge(customer);
            result.setResult(true);
            result.setDescription("Credit note added.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: addCreditNote(): Customer not found");
            result.setDescription("Customer does not exist.");

        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: addCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            context.setRollbackOnly();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper updateCreditNote(Long creditNoteID, Long contactID, Date creditNoteDate, Double amount) {
        System.out.println("PaymentManagementBean: updateCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            CreditNote creditNote = em.getReference(CreditNote.class, creditNoteID);
            if (creditNote.getIsDeleted()) {
                result.setDescription("Credit note has been deleted and cannot be updated.");
                return result;
            }
            Double oldAmt = creditNote.getCreditAmount();
            Contact contact = em.getReference(Contact.class, contactID);
            if (contact.getCustomer().getId() != creditNote.getCustomer().getId()) {
                result.setDescription("Unable to tie this credit note to another customer. Create a new one instead.");
                return result;
            }
            Customer customer = contact.getCustomer();
            if (customer.getIsDeleted()) {
                result.setDescription("Unable to update the credit note for this customer as the customer has been deleted.");
                return result;
            }
            creditNote.setCustomer(customer);
            if (amount == null || amount < 0.0) {
                result.setDescription("Credit note amount must be more than $0");
                return result;
            }
            creditNote.setCreditAmount(amount);
            creditNote.setContactName(contact.getName());
            creditNote.setContactMobileNo(contact.getMobileNo());
            creditNote.setContactOfficeNo(contact.getOfficeNo());
            creditNote.setContactFaxNo(contact.getFaxNo());
            creditNote.setContactEmail(contact.getEmail());
            creditNote.setContactAddress(contact.getAddress());
            if (creditNoteDate == null) {
                result.setDescription("Credit note date cannot be empty");
                return result;
            }
            creditNote.setDateIssued(creditNoteDate);
            em.merge(creditNote);
            customer.setTotalAvailableCredits(customer.getTotalAvailableCredits() - oldAmt + amount);
            em.merge(customer);
            result.setResult(true);
            result.setDescription("Credit note updated.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: updateCreditNote(): Credit note or contact not found");
            result.setDescription("Credit note or contact specified does not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: updateCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            context.setRollbackOnly();
        }
        return result;
    }

    @Override
    public ReturnHelper deleteCreditNote(Long creditNoteID) {
        System.out.println("PaymentManagementBean: deleteCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            CreditNote creditNote = em.getReference(CreditNote.class, creditNoteID);
            if (!creditNote.getIsDeleted()) {
                creditNote.setIsDeleted(true);
                em.merge(creditNote);
                //Update customer available credits
                Customer customer = creditNote.getCustomer();
                customer.setTotalAvailableCredits(customer.getTotalAvailableCredits() - creditNote.getCreditAmount());
                em.merge(customer);
            }
            result.setResult(true);
            result.setDescription("Credit note deleted.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: deleteCreditNote(): Credit note not found");
            result.setDescription("Credit note not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: deleteCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper voidCreditNote(Long creditNoteID) {
        System.out.println("PaymentManagementBean: voidCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            CreditNote creditNote = em.getReference(CreditNote.class, creditNoteID);
            if (creditNote.getAppliedToInvoice()!=null) {
                result.setDescription("Credit note cannot be voided as it has been applied to an invoice.");
                return result;
            }
            if (!creditNote.getIsDeleted()) {
                creditNote.setIsVoided(true);
                em.merge(creditNote);
                //Update customer available credits
                Customer customer = creditNote.getCustomer();
                customer.setTotalAvailableCredits(customer.getTotalAvailableCredits() - creditNote.getCreditAmount());
                em.merge(customer);
            }
            result.setResult(true);
            result.setDescription("Credit note voided.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: voidCreditNote(): Credit note not found");
            result.setDescription("Credit note not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: voidCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public ReturnHelper applyCreditNote(Long creditNoteID, Long invoiceID) {
        System.out.println("PaymentManagementBean: applyCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            CreditNote creditNote = em.getReference(CreditNote.class, creditNoteID);
            if (creditNote.getIsDeleted()) {
                result.setDescription("Credit note has been deleted and cannot be updated.");
                return result;
            } else if (creditNote.getIsVoided()) {
                result.setDescription("Credit note has been voided and cannot be used anymore.");
                return result;
            }
            Invoice invoice = em.getReference(Invoice.class, invoiceID);
            if (invoice.getIsDeleted()) {
                result.setDescription("Unable to apply the credit note as the invoice has been deleted.");
                return result;
            } else if (invoice.getStatus().equals("Voided")) {
                result.setDescription("Unable to apply the credit note as the invoice has been voided.");
                return result;
            }
            //Check if invoice & credit note belongs to the same customer
            if (invoice.getSalesConfirmationOrder().getCustomer().getId() != creditNote.getCustomer().getId()) {
                result.setDescription("Unable to apply the credit note to this invoice as it belongs to a different customer.");
                return result;
            }
            //Use credit note
            creditNote.setAppliedToInvoice(invoice);
            em.merge(creditNote);
            //Update invoice amount
            imbl.refreshInvoice(invoiceID);
            //Update customer available credits
            Customer customer = creditNote.getCustomer();
            customer.setTotalAvailableCredits(customer.getTotalAvailableCredits() - creditNote.getCreditAmount());
            em.merge(customer);
            result.setResult(true);
            result.setDescription("Credit note applied to invoice.");
        } catch (EntityNotFoundException ex) {
            System.out.println("PaymentManagementBean: applyCreditNote(): Credit note or Invoice not found");
            result.setDescription("Credit note or invoice specified does not exist.");
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: applyCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            context.setRollbackOnly();
        }
        return result;
    }

    @Override
    public List<CreditNote> getCreditNoteByCustomer(Long customerID) {
        System.out.println("PaymentManagementBean: getCreditNoteByCustomer() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT e FROM CreditNote e WHERE e.isDeleted=false AND e.customer.id=:customerID");
            q.setParameter("customerID", customerID);
            return q.getResultList();
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: getCreditNoteByCustomer() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CreditNote> listAllCreditNote(Long staffID) {
        System.out.println("PaymentManagementBean: listAllCreditNote() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT e FROM CreditNote e WHERE e.isDeleted=false");
            return q.getResultList();
        } catch (Exception ex) {
            System.out.println("PaymentManagementBean: listAllCreditNote() failed");
            result.setDescription("Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

}
