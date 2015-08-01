package PaymentManagement;

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
    public Double getInvoiceTotalPaymentAmount(Long invoiceID);
}
