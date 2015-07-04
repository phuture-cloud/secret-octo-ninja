package PaymentManagement;

import EntityManager.Invoice;
import EntityManager.ReturnHelper;
import EntityManager.StatementOfAccount;
import java.util.List;
import javax.ejb.Local;

@Local
public interface StatementOfAccountBeanLocal {
    
    public List<StatementOfAccount> listAllStatementOfAccounts();
    public StatementOfAccount getCustomerSOA(Long customerID);
    
    public ReturnHelper refreshAllSOA();
    public ReturnHelper refreshCustomerSOA(Long customerID);
    
    public ReturnHelper checkIfSCOisValid(Long scoID);
    public ReturnHelper checkIfInvoiceIsValid(Long invoiceID);
    public ReturnHelper checkIfPaymentIsValid(Long paymentID);
    
}
