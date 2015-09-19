package OrderManagement;

import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.SalesConfirmationOrderHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface OrderManagementBeanLocal {
    //SCO
    public ReturnHelper createSalesConfirmationOrder(Date salesConfirmationOrderDate, Date estimatedDeliveryDate, String customerPurchaseOrderNumber, Long customerID, Long contactID, Long salesStaffID, Integer terms);
    public ReturnHelper updateSalesConfirmationOrder(Long salesConfirmationOrderID, Date newSalesConfirmationOrderDate, Date newEstimatedDeliveryDate, String customerPurchaseOrderNumber, Long newCustomerID, String status, Integer newTerms, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderCustomerContactDetails(Long salesConfirmationOrderID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderCustomerContactDetails(Long salesConfirmationOrderID, Long customerID, Long contactID, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderRemarks(Long salesConfirmationOrderID, String remarks, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderNotes(Long salesConfirmationOrderID, String notes, Boolean adminOverwrite);
    public ReturnHelper updateSalesConfirmationOrderStatus(Long salesConfirmationOrderID, String status);
    public ReturnHelper deleteSalesConfirmationOrder(Long salesConfirmationOrderID, Boolean adminOverwrite);
    public ReturnHelper voidSalesConfirmationOrder(Long salesConfirmationOrderID, Boolean adminOverwrite);
    public ReturnHelper checkIfSCOisEditable(Long salesConfirmationOrderID, Boolean adminOverwrite);
    public ReturnHelper checkIfSCOnumberIsUnique(String salesConfirmationNumber);
    
    public SalesConfirmationOrder getSalesConfirmationOrder(Long salesConfirmationOrderID);
    public List<SalesConfirmationOrderHelper> listAllSalesConfirmationOrder(Long staffID);
    public List<SalesConfirmationOrderHelper> listCustomerSalesConfirmationOrder(Long customerID);
    
    public ReturnHelper addSCOlineItem(Long salesConfirmationOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice,Boolean adminOverwrite);
    public ReturnHelper updateSCOlineItem(Long salesConfirmationOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite);
    public ReturnHelper deleteSCOlineItem(Long salesConfirmationOrderID, Long lineItemID, Boolean adminOverwrite);
    public ReturnHelper deleteSCOallLineItem(Long salesConfirmationOrderID, Boolean adminOverwrite);
    public List<LineItem> listSCOlineItems(Long salesConfirmationOrderID);
    
    public ReturnHelper refreshSCOs(Long staffID);
}

