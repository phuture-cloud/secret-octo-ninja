package PaymentManagement;

import EntityManager.CreditNote;
import EntityManager.PaymentRecord;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PaymentManagementBeanLocal {
    
    public ReturnHelper addPayment(Long invoiceID, Double amount, Date date, String paymentMethod, String paymentReferenceNumber, String notes);
    public ReturnHelper updatePayment(Long paymentID, Double amount, Date date, String paymentMethod, String paymentReferenceNumber, String notes);
    public ReturnHelper deletePayment(Long paymentID);
    public PaymentRecord getPayment(Long paymentID);
    public List<PaymentRecord> listPaymentByCustomer(Long customerID);
    public List<PaymentRecord> listPaymentByInvoice(Long invoiceID);
    public List<PaymentRecord> listAllPayment();
    public Double getInvoiceTotalCreditNoteApplied(Long invoiceID);
    public Double getInvoiceTotalPaymentAmount(Long invoiceID);
    
    public ReturnHelper addCreditNote(Long contactID, Double amount, Date creditNoteDate);
    public ReturnHelper updateCreditNote(Long creditNoteID, Long contactID, Date creditNoteDate, Double amount);
    public ReturnHelper deleteCreditNote(Long creditNoteID);
    public ReturnHelper voidCreditNote(Long creditNoteID);
    public ReturnHelper attachCreditNote(Long creditNoteID, Long invoiceID);
    public ReturnHelper detachCreditNote(Long creditNoteID);
    public ReturnHelper detachAllCreditNote(Long invoiceID);
    
    public CreditNote getCreditNote(Long creditNoteID);
    public List<CreditNote> getCreditNoteByCustomer(Long customerID);
    public List<CreditNote> listAllCreditNote(Long customerID);
    public List<CreditNote> listAvailableCreditNote(Long customerID);
    public List<CreditNote> listAttachedCreditNote(Long invoiceID);
    
}
