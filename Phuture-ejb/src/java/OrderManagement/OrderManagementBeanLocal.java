package OrderManagement;

import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface OrderManagementBeanLocal {
    //SCO
    public ReturnHelper createSalesConfirmationOrder(String salesConfirmationOrderNumber, Date salesConfirmationOrderDate, Long customerID, Long contactID, Long salesStaffID, Integer terms, String remarks, String notes);
    public ReturnHelper updateSalesConfirmationOrder(Long salesConfirmationOrderID, String newSalesConfirmationOrderNumber, Date newSalesConfirmationOrderDate, Long newCustomerID, Long newSalesStaffID, Integer newTerms, String newRemarks, String newNotes, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderCustomerContactDetails(Long salesConfirmationOrderID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderCustomerContactDetails(Long salesConfirmationOrderID, Long customerID, Long contactID, Boolean adminOverwrite);
    public ReturnHelper deleteSalesConfirmationOrder(Long salesConfirmationOrderID);
    public ReturnHelper checkIfSCOisEditable(Long salesConfirmationOrderID, Boolean adminOverwrite);
    
    public SalesConfirmationOrder getSalesConfirmationOrder(Long salesConfirmationOrderID);
    public List<SalesConfirmationOrder> listAllSalesConfirmationOrder();
    public List<SalesConfirmationOrder> listCustomerSalesConfirmationOrder(Long customerID);
    
    public ReturnHelper addSCOlineItem(Long salesConfirmationOrderID, String itemName, String itemDescription, Integer itemQty, Double itemTotalPrice,Boolean adminOverwrite);
    public ReturnHelper updateSCOlineItem(Long salesConfirmationOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemTotalPrice, Boolean adminOverwrite);
    public ReturnHelper deleteSCOlineItem(Long salesConfirmationOrderID, Long lineItemID, Boolean adminOverwrite);
    public ReturnHelper deleteSCOallLineItem(Long salesConfirmationOrderID, Boolean adminOverwrite);
    public List<LineItem> listSCOlineItems(Long salesConfirmationOrderID);
            
  
//    public ReturnHelper addDOlineItem(Long deliveryOrderID, String itemName, String itemDescription, Integer itemQty, Double itemTotalPrice,Boolean adminOverwrite);
//    public ReturnHelper updateDOlineItem(Long deliveryOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemTotalPrice, Boolean adminOverwrite);
//    public ReturnHelper deleteDOlineItem(Long deliveryOrderID, Long lineItemID, Boolean adminOverwrite);
//    public List<LineItem> listDOlineItems(Long deliveryOrderID);
    
//    Invoice
//    public ReturnHelper createInvoice();
//    public ReturnHelper updateInvoice();
//    public ReturnHelper deleteInvoice();

    
    
}

