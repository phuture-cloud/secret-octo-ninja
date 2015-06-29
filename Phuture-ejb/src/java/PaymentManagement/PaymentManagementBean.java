package PaymentManagement;

import EntityManager.Customer;
import EntityManager.Invoice;
import EntityManager.PaymentRecord;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PaymentManagementBean implements PaymentManagementBeanLocal {

    @PersistenceContext
    private EntityManager em;

    public PaymentManagementBean() {
    }

    @Override
    public ReturnHelper addPayment(Long invoiceID, Double amount, Date date, String paymentMethod, String paymentReferenceNumber) {
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
            paymentRecord.setPaymentMethod(paymentMethod);
            paymentRecord.setPaymentReferenceNumber(paymentReferenceNumber);
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
    public ReturnHelper updatePayment(Long paymentID, Double amount, Date date, String paymentMethod, String paymentReferenceNumber) {
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
            paymentRecord.setPaymentMethod(paymentMethod);
            paymentRecord.setPaymentReferenceNumber(paymentReferenceNumber);
            em.merge(paymentRecord);
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
            paymentRecord.setIsDeleted(true);
            em.merge(paymentRecord);
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
            Query q = em.createQuery("SELECT e FROM PaymentRecord e WHERE e.invoice=:invoiceID AND e.isDeleted=false");
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
    public List<PaymentRecord> listAllInvoice() {
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

}
