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
    public ReturnHelper updatePurchaseOrder(Long purchaseOrderID, String status, String notes, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderStatus(Long purchaseOrderID, String status);
    public ReturnHelper updateSalesConfirmationOrderStatus(Long purchaseOrderID, String notes, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderNotes(Long salesConfirmationOrderID, String notes, Boolean adminOverwrite);
    public ReturnHelper deletePurchaseOrder(Long purchaseOrderID);  
    
    public PurchaseOrder getPurchaseOrder(Long purchaseOrderID);
    public List<PurchaseOrder> listAllPurchaseOrder();
    public List<PurchaseOrder> listPurchaseOrderTiedToSCO(Long salesConfirmationOrderID);
    
    public ReturnHelper addPOlineItem(Long purchaseOrderID, String itemName, String itemDescription, Integer itemQty, Double itemTotalPrice,Boolean adminOverwrite);
    public ReturnHelper updatePOlineItem(Long purchaseOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite);
    public ReturnHelper deletePOlineItem(Long purchaseOrderID, Long lineItemID, Boolean adminOverwrite);
    public ReturnHelper deletePOallLineItem(Long purchaseOrderID, Boolean adminOverwrite);
    public List<LineItem> listPOlineItems(Long purchaseOrderID);
    
}
