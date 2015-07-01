/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrderManagement;

import PaymentManagement.StatementOfAccountBeanLocal;
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
@WebService(serviceName = "NewWebService")
@Stateless()
public class NewWebService {
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
    
}
