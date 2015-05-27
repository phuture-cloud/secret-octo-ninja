package OrderManagement;

import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OrderManagementBean implements OrderManagementBeanLocal {

    public OrderManagementBean() {
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public ReturnHelper createSalesConfirmationOrder(String salesConfirmationOrderNumber, Long customerID, Long shippingContactID, Long billingContactID, Long salesStaffID) {
        System.out.println("OrderManagementBean: addSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        try {
           
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: addSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to create a new SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper editSalesConfirmationOrder(Long salesConfirmationOrderID, Long customerID, Long shippingContactID, Long billingContactID, Long salesStaffID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteSalesConfrimationOrder(Long salesConfirmationOrderID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesConfirmationOrder> listAllSalesConfirmationOrder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SalesConfirmationOrder> listCustomerSalesConfirmationOrder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
