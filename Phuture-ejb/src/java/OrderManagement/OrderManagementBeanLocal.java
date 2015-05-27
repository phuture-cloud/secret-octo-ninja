package OrderManagement;

import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import java.util.List;
import javax.ejb.Local;

@Local
public interface OrderManagementBeanLocal {
    public ReturnHelper createSalesConfirmationOrder(String salesConfirmationOrderNumber, Long customerID, Long shippingContactID, Long billingContactID, Long salesStaffID);
    public ReturnHelper editSalesConfirmationOrder(Long salesConfirmationOrderID, Long customerID, Long shippingContactID, Long billingContactID, Long salesStaffID);
    public ReturnHelper deleteSalesConfrimationOrder(Long salesConfirmationOrderID);
    
    public List<SalesConfirmationOrder> listAllSalesConfirmationOrder();
    public List<SalesConfirmationOrder> listCustomerSalesConfirmationOrder();
    
    
}

