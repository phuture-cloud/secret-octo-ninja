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
    public ReturnHelper updatePurchaseOrder(Long purchaseOrderID, Long newSupplierContactID, Date purchaseOrderDate, String status, String terms, Date deliveryDate, String remarks, String currency, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderSupplierContactDetails(Long salesConfirmationOrderID, String supplierName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderSupplierContactDetails(Long salesConfirmationOrderID, Long newSupplierContactID, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderStatus(Long purchaseOrderID, String status, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderNotes(Long purchaseOrderID, String notes, Boolean adminOverwrite);
    public ReturnHelper updatePurchaseOrderRemarks(Long purchaseOrderID, String remarks, Boolean adminOverwrite);
    public ReturnHelper deletePurchaseOrder(Long purchaseOrderID, Boolean adminOverwrite);
    public ReturnHelper voidPurchaseOrder(Long purchaseOrderID, Boolean adminOverwrite);
    
    public ReturnHelper checkIfPOisEditable(Long purchaseOrderID, Boolean adminOverwrite);
    public ReturnHelper checkIfPOnumberIsUnique(String purchaseOrderNumber);
    
    
    public PurchaseOrder getPurchaseOrder(Long purchaseOrderID);
    public List<PurchaseOrder> listAllPurchaseOrder(Long staffID);
    public List<PurchaseOrder> listPurchaseOrdersTiedToSCO(Long salesConfirmationOrderID);
    
    public ReturnHelper replacePOlineItemWithSCOitems(Long salesConfirmationOrderID, Long purchaseOrderID, Boolean adminOverwrite);
    public ReturnHelper addPOlineItem(Long purchaseOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite);
    public ReturnHelper updatePOlineItem(Long purchaseOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite);
    public ReturnHelper deletePOlineItem(Long purchaseOrderID, Long lineItemID, Boolean adminOverwrite);
    public ReturnHelper deleteallPOlineItem(Long purchaseOrderID, Boolean adminOverwrite);
    public List<LineItem> listPOlineItems(Long purchaseOrderID);  
}
