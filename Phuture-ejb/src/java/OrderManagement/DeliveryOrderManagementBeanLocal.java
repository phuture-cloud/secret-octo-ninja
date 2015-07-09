package OrderManagement;

import EntityManager.LineItem;
import EntityManager.DeliveryOrder;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface DeliveryOrderManagementBeanLocal {

    public ReturnHelper createDeliveryOrder(Long salesConfirmationOrderID, String deliveryOrderNumber, Date deliveryOrderDate);

    public ReturnHelper updateDeliveryOrder(Long deliveryOrderID, String newDeliveryOrderNumber, Date newDeliveryOrderDate, String customerPurchaseOrderNumber, String status, Boolean adminOverwrite);

    public ReturnHelper updateDeliveryOrderCustomerContactDetails(Long deliveryOrderID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite);

    public ReturnHelper updateDeliveryOrderCustomerContactDetails(Long deliveryOrderID, Long customerID, Long contactID, Boolean adminOverwrite);

    public ReturnHelper updateDeliveryOrderRemarks(Long deliveryOrderID, String remarks, Boolean adminOverwrite);

    public ReturnHelper updateDeliveryOrderNotes(Long deliveryOrderID, String notes, Boolean adminOverwrite);

    public ReturnHelper updateDeliveryOrderStatus(Long deliveryOrderID, String status, Boolean adminOverwrite);

    public ReturnHelper deleteDeliveryOrder(Long deliveryOrderID, Boolean adminOverwrite);

    public ReturnHelper checkIfDOisEditable(Long deliveryOrderID, Boolean adminOverwrite);

    public ReturnHelper checkIfDOnumberIsUnique(String deliveryOrderNumber);

    public DeliveryOrder getDeliveryOrder(Long deliveryOrderID);

    public List<DeliveryOrder> listAllDeliveryOrder(Long staffID);

    public List<DeliveryOrder> listDeliveryOrdersTiedToSCO(Long salesConfirmationOrderID);

    public ReturnHelper replaceDOlineItemWithSCOitems(Long salesConfirmationOrderID, Long deliveryOrderID, Boolean adminOverwrite);

    public ReturnHelper addDOlineItem(Long deliveryOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite);

    public ReturnHelper updateDOlineItem(Long deliveryOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite);

    public ReturnHelper deleteDOlineItem(Long deliveryOrderID, Long lineItemID, Boolean adminOverwrite);

    public ReturnHelper deleteallDOlineItem(Long deliveryOrderID, Boolean adminOverwrite);

    public List<LineItem> listDOlineItems(Long deliveryOrderID);
}
