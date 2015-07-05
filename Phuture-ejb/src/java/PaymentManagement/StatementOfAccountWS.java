/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PaymentManagement;

import EntityManager.Invoice;
import EntityManager.ReturnHelper;
import EntityManager.StatementOfAccount;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author -VeRyLuNaTiC
 */
@WebService(serviceName = "StatementOfAccountWS")
@Stateless()
public class StatementOfAccountWS {
    @EJB
    private StatementOfAccountBeanLocal ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "listAllStatementOfAccounts")
    public List<StatementOfAccount> listAllStatementOfAccounts() {
        return ejbRef.listAllStatementOfAccounts();
    }

    @WebMethod(operationName = "getCustomerSOA")
    public StatementOfAccount getCustomerSOA(@WebParam(name = "customerID") Long customerID) {
        return ejbRef.getCustomerSOA(customerID);
    }

    @WebMethod(operationName = "refreshAllSOA")
    public ReturnHelper refreshAllSOA() {
        return ejbRef.refreshAllSOA();
    }

    @WebMethod(operationName = "refreshCustomerSOA")
    public ReturnHelper refreshCustomerSOA(@WebParam(name = "customerID") Long customerID) {
        return ejbRef.refreshCustomerSOA(customerID);
    }

    @WebMethod(operationName = "checkIfSCOisValid")
    public ReturnHelper checkIfSCOisValid(@WebParam(name = "scoID") Long scoID) {
        return ejbRef.checkIfSCOisValid(scoID);
    }

    @WebMethod(operationName = "checkIfInvoiceIsValid")
    public ReturnHelper checkIfInvoiceIsValid(@WebParam(name = "invoiceID") Long invoiceID) {
        return ejbRef.checkIfInvoiceIsValid(invoiceID);
    }

    @WebMethod(operationName = "checkIfPaymentIsValid")
    public ReturnHelper checkIfPaymentIsValid(@WebParam(name = "paymentID") Long paymentID) {
        return ejbRef.checkIfPaymentIsValid(paymentID);
    }
    
}
