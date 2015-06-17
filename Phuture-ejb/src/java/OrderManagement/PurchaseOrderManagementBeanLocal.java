package OrderManagement;

import EntityManager.LineItem;
import EntityManager.PurchaseOrder;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PurchaseOrderManagementBeanLocal {
    public ReturnHelper createPurchaseOrder(Long salesConfirmationOrderID, String purchaseOrderNumber, Date purchaseOrderDate);
    public ReturnHelper updatePurchaseOrder(Long purchaseOrderID, String status, String notes);
    public ReturnHelper updatePurchaseOrderStatus(Long purchaseOrderID, String status);
    public ReturnHelper updatePurchaseOrderNotes(Long salesConfirmationOrderID, String notes);
    public ReturnHelper deletePurchaseOrder(Long purchaseOrderID);  
    
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
