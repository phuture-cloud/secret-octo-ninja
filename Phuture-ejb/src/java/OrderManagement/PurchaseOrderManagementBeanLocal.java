package OrderManagement;

import EntityManager.LineItem;
import EntityManager.PurchaseOrder;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PurchaseOrderManagementBeanLocal {
    public ReturnHelper createPurchaseOrder(Long salesConfirmationOrderID, Date purchaseOrderDate);
    public ReturnHelper updatePurchaseOrder(Long purchaseOrderID, Long newSupplierContactID, Date purchaseOrderDate, String status, String terms, Date deliveryDate, String remarks, String currency);
    public ReturnHelper updatePurchaseOrderSupplierContactDetails(Long salesConfirmationOrderID, String supplierName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderSupplierContactDetails(Long salesConfirmationOrderID, Long supplierID, Long contactID, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderStatus(Long purchaseOrderID, String status);
    public ReturnHelper updatePurchaseOrderNotes(Long purchaseOrderID, String notes);
    public ReturnHelper updatePurchaseOrderRemarks(Long purchaseOrderID, String remarks);
    public ReturnHelper deletePurchaseOrder(Long purchaseOrderID);
    public ReturnHelper checkIfPOnumberIsUnique(String purchaseOrderNumber);
    
    public PurchaseOrder getPurchaseOrder(Long purchaseOrderID);
    public List<PurchaseOrder> listAllPurchaseOrder(Long staffID);
    public List<PurchaseOrder> listPurchaseOrdersTiedToSCO(Long salesConfirmationOrderID);
    
    public ReturnHelper replacePOlineItemWithSCOitems(Long salesConfirmationOrderID, Long purchaseOrderID);
    public ReturnHelper addPOlineItem(Long purchaseOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice);
    public ReturnHelper updatePOlineItem(Long purchaseOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice);
    public ReturnHelper deletePOlineItem(Long purchaseOrderID, Long lineItemID);
    public ReturnHelper deleteallPOlineItem(Long purchaseOrderID);
    public List<LineItem> listPOlineItems(Long purchaseOrderID);  
}
