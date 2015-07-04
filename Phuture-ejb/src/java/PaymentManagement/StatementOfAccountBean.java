package PaymentManagement;

import EntityManager.Customer;
import EntityManager.Invoice;
import EntityManager.PaymentRecord;
import EntityManager.ReturnHelper;
import EntityManager.SOALineItem;
import EntityManager.SalesConfirmationOrder;
import EntityManager.StatementOfAccount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class StatementOfAccountBean implements StatementOfAccountBeanLocal {

    @PersistenceContext
    private EntityManager em;

    public StatementOfAccountBean() {
    }

    @Override
    public List<StatementOfAccount> listAllStatementOfAccounts() {
        System.out.println("StatementOfAccountBean: listAllStatementOfAccounts(): called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            //Delete all the SOA
            Query q = em.createQuery("SELECT e FROM StatementOfAccount e");
            List<StatementOfAccount> soas = q.getResultList();
            return soas;
        } catch (Exception ex) {
            System.out.println("StatementOfAccountBean: listAllStatementOfAccounts() failed");
            result.setDescription("Failed to get statements. Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public StatementOfAccount getCustomerSOA(Long customerID) {
        System.out.println("StatementOfAccountBean: getCustomerSOA(): called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            //Delete all the SOA
            Query q = em.createQuery("SELECT e FROM StatementOfAccount e WHERE e.customer.id=:customerID");
            q.setParameter("customerID", customerID);
            StatementOfAccount soa = (StatementOfAccount) q.getSingleResult();
            return soa;
        } catch (Exception ex) {
            System.out.println("StatementOfAccountBean: getCustomerSOA() failed");
            result.setDescription("Failed to get statements. Internal server error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper refreshAllSOA() {
        System.out.println("StatementOfAccountBean: refreshAllSOA(): called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            //Delete all the SOA
            Query q = em.createQuery("SELECT e FROM SOALineItem e");
            List<SOALineItem> soalis = q.getResultList();
            for (SOALineItem soali : soalis) {
                em.remove(soali);
            }
            q = em.createQuery("SELECT e FROM StatementOfAccount e");
            List<StatementOfAccount> soas = q.getResultList();
            for (StatementOfAccount soa : soas) {
                Customer customer = soa.getCustomer();
                customer.setStatementOfAccount(null);
                em.merge(customer);
                em.remove(soa);
            }
            //Recreate all the SOA
            q = em.createQuery("SELECT e FROM Customer e WHERE e.isDeleted=false");
            List<Customer> customers = q.getResultList();
            Boolean someFailed = false;
            for (Customer customer : customers) {
                result = refreshCustomerSOA(customer.getId());
                if (!result.getResult()) {
                    someFailed = true;
                }
            }
            if (someFailed) {
                result.setDescription("Failed to generate statements for some customers due to inconsistent records.");
            } else {
                result.setDescription("All customer's statements refreshed");
                result.setResult(true);
            }
        } catch (Exception ex) {
            System.out.println("StatementOfAccountBean: refreshAllSOA() failed");
            result.setDescription("Failed to generate statements. Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public ReturnHelper refreshCustomerSOA(Long customerID) {
        System.out.println("StatementOfAccountBean: refreshCustomerSOA(): called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT e FROM Customer e where e.id=:customerID");
            q.setParameter("customerID", customerID);
            Customer customer = (Customer) q.getSingleResult();
            Date todayDate = new Date();
            if (customer.getIsDeleted()) {
                result.setDescription("Unable to refresh as customer has been deleted.");
                return result;
            }
            //Delete existing SOA first
            StatementOfAccount soa = customer.getStatementOfAccount();
            List<SOALineItem> soalis = null;
            if (soa != null) {
                soalis = soa.getLineItem();
                if (soalis != null) {
                    //Delete all SOALineItem
                    for (SOALineItem soali : soalis) {
                        em.remove(soali);
                    }
                }
                em.remove(soa);
            }

            //Create new SOA
            soa = new StatementOfAccount();
            soa.setCustomer(customer);
            em.persist(soa);

            //Create the SOALineItem list and at the same time calculate the total amounts
            soalis = new ArrayList();
            Double totalAmountOrdered = 0.0;
            Double totalAmountInvoiced = 0.0;
            Double totalAmountPaidForThisCustomer = 0.0;
            Double totalAmountOverDue = 0.0;
            List<Invoice> overDueInvoices = new ArrayList();
            Double amountOverDueFrom0to30Days = 0.0;
            Double amountOverDueFrom31to60Days = 0.0;
            Double amountOverDueFrom61to90Days = 0.0;
            Double amountOverDueOver91Days = 0.0;

            //Loop thru the SCO to calculate total amount ordered
            q = em.createQuery("SELECT e FROM SalesConfirmationOrder e where e.customerLink.id=:customerID");
            q.setParameter("customerID", customerID);
            List<SalesConfirmationOrder> scos = q.getResultList();
            for (SalesConfirmationOrder sco : scos) {
                totalAmountOrdered = totalAmountOrdered + sco.getTotalPrice();
            }

            //Loop thru the customer invoice and create it as an SOALineItem
            q = em.createQuery("SELECT e FROM Invoice e where e.salesConfirmationOrder.customerLink.id=:customerID");
            q.setParameter("customerID", customerID);
            List<Invoice> invoices = q.getResultList();
            soalis = new ArrayList();
            for (Invoice invoice : invoices) {
                SOALineItem soali = new SOALineItem();
                soali.setStatementOfAccount(customer.getStatementOfAccount());
                soali.setEntryDate(invoice.getDateSent());
                soali.setReferenceNo(invoice.getInvoiceNumber());
                soali.setMethod("");
                soali.setDescription("Invoice for order " + invoice.getSalesConfirmationOrder().getSalesConfirmationOrderNumber());
                soali.setDueDate(invoice.getDateDue());
                soali.setScoID(invoice.getSalesConfirmationOrder().getId());
                soali.setInvoiceID(invoice.getId());
                soali.setDebit(invoice.getTotalPrice());
                soali.setCredit(0.0);
                em.persist(soali);
                soalis.add(soali);
                totalAmountInvoiced = totalAmountInvoiced + invoice.getTotalPrice();
                //For each invoice
                //Loop thru the customer payment and create it as an SOALineItem
                //At the same time calculate the amount overdue for each invoice
                q = em.createQuery("SELECT e FROM PaymentRecord e where e.customer.id=:customerID");
                q.setParameter("customerID", customerID);
                List<PaymentRecord> paymentRecords = q.getResultList();
                soalis = new ArrayList();
                Double totalAmountPaidForThisInvoice = 0.0;
                for (PaymentRecord paymentRecord : paymentRecords) {
                    SOALineItem soali2 = new SOALineItem();
                    soali2.setStatementOfAccount(customer.getStatementOfAccount());
                    soali2.setEntryDate(paymentRecord.getPaymentDate());
                    soali2.setReferenceNo(paymentRecord.getPaymentReferenceNumber());
                    soali2.setMethod(paymentRecord.getPaymentMethod());
                    soali2.setDescription("Pymt for invoice " + paymentRecord.getInvoice().getInvoiceNumber());
                    soali2.setDueDate(paymentRecord.getInvoice().getDateDue());
                    soali2.setScoID(paymentRecord.getInvoice().getSalesConfirmationOrder().getId());
                    soali2.setInvoiceID(paymentRecord.getInvoice().getId());
                    soali2.setPaymentID(paymentRecord.getId());
                    soali2.setCredit(paymentRecord.getAmount());
                    soali2.setDebit(0.0);
                    em.persist(soali2);
                    soalis.add(soali2);
                    totalAmountPaidForThisCustomer = totalAmountPaidForThisCustomer + paymentRecord.getAmount();
                    totalAmountPaidForThisInvoice = totalAmountPaidForThisInvoice + paymentRecord.getAmount();
                }
                //Calculate amount overdue for each invoice
                //only if payment is less then amount invoiced
                //TODO only mark as overdue if invoice is not writen off
                if (invoice.getTotalPrice() > totalAmountPaidForThisInvoice) {
                    if (invoice.getDateDue() != null && invoice.getDateDue().before(todayDate)) {
                        Long dayDifference = getDifferenceDays(todayDate, invoice.getDateDue());
                        Double amountOwedForThisInvoice = invoice.getTotalPrice() - totalAmountPaidForThisInvoice;
                        if (dayDifference > 91) {
                            amountOverDueOver91Days = amountOverDueOver91Days + amountOwedForThisInvoice;
                        } else if (dayDifference >= 61) {
                            amountOverDueFrom61to90Days = amountOverDueFrom61to90Days + amountOwedForThisInvoice;
                        } else if (dayDifference >= 31) {
                            amountOverDueFrom31to60Days = amountOverDueFrom31to60Days + amountOwedForThisInvoice;
                        } else if (dayDifference >= 0) {
                            amountOverDueFrom0to30Days = amountOverDueFrom0to30Days + amountOwedForThisInvoice;
                        }
                        overDueInvoices.add(invoice);
                    }
                }
            }
            //Sort the SOALIS by their date and calculate the balance fields
            q = em.createQuery("SELECT e FROM SOALineItem e where e.statementOfAccount.customer.id=:customerID ORDER BY e.entryDate ASC");
            q.setParameter("customerID", customerID);
            soalis = q.getResultList();
            Double balance = 0.0;
            for (SOALineItem soali : soalis) {
                balance = balance - soali.getCredit() + soali.getDebit();
                soali.setBalance(balance);
                em.merge(soali);
            }

            // Store the computed total amounts
            soa.setTotalAmountOrdered(totalAmountOrdered);
            soa.setTotalAmountInvoiced(totalAmountInvoiced);
            soa.setTotalAmountPaid(totalAmountPaidForThisCustomer);
            soa.setOverDueInvoices(overDueInvoices);
            soa.setLineItem(soalis);
            totalAmountOverDue = amountOverDueFrom0to30Days + amountOverDueFrom31to60Days + amountOverDueFrom61to90Days + amountOverDueOver91Days;
            soa.setTotalAmountOverDue(totalAmountOverDue);
            soa.setAmountOverDueFrom0to30Days(amountOverDueFrom0to30Days);
            soa.setAmountOverDueFrom31to60Days(amountOverDueFrom31to60Days);
            soa.setAmountOverDueFrom61to90Days(amountOverDueFrom61to90Days);
            soa.setAmountOverDueOver91Days(amountOverDueOver91Days);
            em.merge(soa);
            result.setDescription("Statement of account refreshed");
            result.setResult(true);
        } catch (Exception ex) {
            System.out.println("StatementOfAccountBean: refreshCustomerSOA() failed");
            result.setDescription("Failed to generate statements. Internal server error");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfSCOisValid(Long scoID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper checkIfInvoiceIsValid(Long invoiceID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper checkIfPaymentIsValid(Long paymentID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
