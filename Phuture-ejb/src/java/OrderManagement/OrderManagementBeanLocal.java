package OrderManagement;

import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import java.util.List;
import javax.ejb.Local;

@Local
public interface OrderManagementBeanLocal {
    public ReturnHelper createSalesConfirmationOrder(String salesConfirmationOrderNumber, Long customerID, Long salesStaffID, Integer terms, String remarks, String notes);
    public ReturnHelper updateSalesConfirmationOrder(Long salesConfirmationOrderID, String newSalesConfirmationOrderNumber, Long newCustomerID, Long newSalesStaffID, Integer newTerms, String newRemarks, String newNotes, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderContactDetails(Long salesConfirmationOrderID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, boolean adminOverwrite);
    public ReturnHelper deleteSalesConfirmationOrder(Long salesConfirmationOrderID);
    public ReturnHelper checkIfSCOisEditable(Long salesConfirmationOrderID, Boolean adminOverwrite);
    
    public List<SalesConfirmationOrder> listAllSalesConfirmationOrder();
    public List<SalesConfirmationOrder> listCustomerSalesConfirmationOrder();
    
//    public ReturnHelper createDeliveryOrder();
//    public ReturnHelper updateDeliveryOrder();
//    public ReturnHelper deleteDeliveryOrder();
//    
//    public ReturnHelper createInvoice();
//    public ReturnHelper updateInvoice();
//    public ReturnHelper deleteInvoice();

    
    
}

