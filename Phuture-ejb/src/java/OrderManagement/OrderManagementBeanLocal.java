package OrderManagement;

import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import java.util.List;
import javax.ejb.Local;

@Local
public interface OrderManagementBeanLocal {
    //SCO
    public ReturnHelper createSalesConfirmationOrder(String salesConfirmationOrderNumber, Long customerID, Long salesStaffID, Integer terms, String remarks, String notes);
    public ReturnHelper updateSalesConfirmationOrder(Long salesConfirmationOrderID, String newSalesConfirmationOrderNumber, Long newCustomerID, Long newSalesStaffID, Integer newTerms, String newRemarks, String newNotes, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderContactDetails(Long salesConfirmationOrderID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, boolean adminOverwrite);
    public ReturnHelper deleteSalesConfirmationOrder(Long salesConfirmationOrderID);
    public ReturnHelper checkIfSCOisEditable(Long salesConfirmationOrderID, Boolean adminOverwrite);
    
    public List<SalesConfirmationOrder> listAllSalesConfirmationOrder();
    public List<SalesConfirmationOrder> listCustomerSalesConfirmationOrder(Long customerID);
    
    public ReturnHelper addSCOlineItem(Long salesConfirmationOrderID, String itemName, String itemDescription, Integer itemQty, Double itemTotalPrice);
    public ReturnHelper updateSCOlineItem(Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemTotalPrice);
    public ReturnHelper deleteSCOlineItem(Long lineItemID);
    public List<LineItem> listSCOlineItems(Long salesConfirmationOrderID);
            
    //DO
    
    
    //public ReturnHelper createDeliveryOrder(Long salesConfirmationOrderID, String deliveryOrderNumber);
    //public ReturnHelper updateDeliveryOrder();
    //public ReturnHelper updateDeliveryOrderContactDetails();
    //public ReturnHelper deleteDeliveryOrder();
//    
//    public ReturnHelper createInvoice();
//    public ReturnHelper updateInvoice();
//    public ReturnHelper deleteInvoice();

    
    
}

