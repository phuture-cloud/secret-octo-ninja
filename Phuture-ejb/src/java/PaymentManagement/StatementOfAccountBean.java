package PaymentManagement;

import EntityManager.CreditNote;
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
                result.setDescription("All customers statements refreshed");
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
            customer.setStatementOfAccount(soa);
            em.merge(customer);

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
            //Only loop if SCO not writen off
            q = em.createQuery("SELECT e FROM SalesConfirmationOrder e WHERE e.status!='Write-Off' AND e.status!='Voided' AND e.isDeleted=false AND e.customerLink.id=:customerID");
            q.setParameter("customerID", customerID);
            List<SalesConfirmationOrder> scos = q.getResultList();
            for (SalesConfirmationOrder sco : scos) {
                totalAmountOrdered = totalAmountOrdered + sco.getTotalPrice();
            }

            //Loop thru the customer invoice and create it as an SOALineItem
            //Only loop if the parent SCO is not written off
            q = em.createQuery("SELECT e FROM Invoice e WHERE e.salesConfirmationOrder.status!='Write-Off' AND e.status!='Voided' AND e.isDeleted=false AND e.salesConfirmationOrder.customerLink.id=:customerID");
            q.setParameter("customerID", customerID);
            List<Invoice> invoices = q.getResultList();
            soalis = new ArrayList();
            for (Invoice invoice : invoices) {
                SOALineItem soali = new SOALineItem();
                soali.setStatementOfAccount(customer.getStatementOfAccount());
                soali.setEntryDate(invoice.getDateSent());
                soali.setReferenceNo(invoice.getInvoiceNumber());
                soali.setMethod("");
                soali.setType("Invoice");
                soali.setDueDate(invoice.getDateDue());
                soali.setScoID(invoice.getSalesConfirmationOrder().getId());
                soali.setInvoiceID(invoice.getId());
                soali.setDebit(invoice.getTotalPriceBeforeCreditNote());
                soali.setCredit(0.0);
                em.persist(soali);
                soalis.add(soali);
                if (invoice.getDateSent() != null || invoice.getDatePaid() != null) {
                    totalAmountInvoiced = totalAmountInvoiced + invoice.getTotalPriceBeforeCreditNote();
                }
                //For each invoice
                //Loop thru the customer payment and create it as an SOALineItem
                //At the same time calculate the amount overdue for each invoice
                q = em.createQuery("SELECT e FROM PaymentRecord e where e.invoice.id=:invoiceID AND e.isDeleted=false");
                q.setParameter("invoiceID", invoice.getId());
                List<PaymentRecord> paymentRecords = q.getResultList();
                soalis = new ArrayList();
                Double totalAmountPaidForThisInvoice = 0.0;
                for (PaymentRecord paymentRecord : paymentRecords) {
                    SOALineItem soali2 = new SOALineItem();
                    soali2.setStatementOfAccount(customer.getStatementOfAccount());
                    soali2.setEntryDate(paymentRecord.getPaymentDate());
                    soali2.setReferenceNo(paymentRecord.getPaymentReferenceNumber());
                    soali2.setMethod(paymentRecord.getPaymentMethod());
                    soali2.setType("Payment");
                    soali2.setDueDate(paymentRecord.getInvoice().getDateDue());
                    soali2.setScoID(paymentRecord.getInvoice().getSalesConfirmationOrder().getId());
                    soali2.setInvoiceID(paymentRecord.getInvoice().getId());
                    soali2.setPaymentID(paymentRecord.getId());
                    soali2.setCredit(paymentRecord.getAmount());
                    soali2.setDebit(0.0);
                    em.persist(soali2);
                    soalis.add(soali2);
                    //If payment method is credit note, don't include in total
                    if (!paymentRecord.getPaymentMethod().equals("Credit Note")) {
                        totalAmountPaidForThisCustomer = totalAmountPaidForThisCustomer + paymentRecord.getAmount();
                    }
                    totalAmountPaidForThisInvoice = totalAmountPaidForThisInvoice + paymentRecord.getAmount();
                }

                //Calculate amount overdue for each invoice
                //only if payment is less then amount invoiced
                if (invoice.getTotalPriceAfterCreditNote() > totalAmountPaidForThisInvoice) {
                    if (invoice.getDateDue() != null && invoice.getDateDue().before(todayDate)) {
                        Long dayDifference = getDifferenceDays(invoice.getDateDue(), todayDate);
                        Double amountOwedForThisInvoice = invoice.getTotalPriceAfterCreditNote() - totalAmountPaidForThisInvoice;
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

            //Loop thru the customer credit note and create it as an SOAlineitem
            q = em.createQuery("SELECT e FROM CreditNote e where e.customer.id=:customerID AND e.isDeleted=false AND e.isVoided=false");
            q.setParameter("customerID", customerID);
            List<CreditNote> creditNotes = q.getResultList();
            soalis = new ArrayList();
            for (CreditNote creditNote : creditNotes) {
                SOALineItem soali3 = new SOALineItem();
                soali3.setStatementOfAccount(customer.getStatementOfAccount());
                //Use date used if the CN is applied
                if (creditNote.getAppliedToInvoice() != null) {
                    soali3.setEntryDate(creditNote.getDateUsed());
                } else {
                    soali3.setEntryDate(creditNote.getDateIssued());
                }
                soali3.setReferenceNo(creditNote.getCreditNoteNumber());
                soali3.setMethod("");
                soali3.setType("Credit Note");
                soali3.setDueDate(null);
                Invoice currentInvoice = creditNote.getAppliedToInvoice();
                //If applied to invoice
                if (currentInvoice != null) {
                    soali3.setScoID(creditNote.getAppliedToInvoice().getSalesConfirmationOrder().getId());
                    soali3.setInvoiceID(creditNote.getAppliedToInvoice().getId());
                    //TODO (future) Update logic to support multiple credit note
                    //if credit note is applied and over the invoice amount, don't over add
                    if (creditNote.getCreditAmount() > (currentInvoice.getTotalPriceBeforeCreditNote() - currentInvoice.getTotalAmountPaid())) {
                        Double creditAmount = currentInvoice.getTotalPriceBeforeCreditNote() - currentInvoice.getTotalAmountPaid();
                        if (creditAmount < 0) {
                            creditAmount = 0.0;
                        }
                        soali3.setCredit(creditAmount);
                    } else {
                        soali3.setCredit(creditNote.getCreditAmount());
                    }
                    soali3.setScoID(currentInvoice.getSalesConfirmationOrder().getId());
                    soali3.setInvoiceID(currentInvoice.getId());
                } else {
                    soali3.setCredit(creditNote.getCreditAmount());
                    soali3.setScoID(null);
                    soali3.setInvoiceID(null);
                }
                soali3.setPaymentID(null);
                soali3.setDebit(0.0);
                em.persist(soali3);
                soalis.add(soali3);
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
