package OrderManagement;

import EntityManager.Invoice;
import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

@Local
public interface InvoiceManagementBeanLocal {
    public ReturnHelper createInvoice(Long salesConfirmationOrderID, String invoiceNumber);
    public ReturnHelper updateInvoice(Long invoiceID, String newInvoiceNumber, Date invoiceSent, Date invoicePaid, Boolean adminOverwrite);
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite);
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, Long customerID, Long contactID, Boolean adminOverwrite);
    public ReturnHelper updateInvoiceRemarks(Long invoiceID, String remarks, Boolean adminOverwrite);
    public ReturnHelper updateInvoiceNotes(Long invoiceID, String notes, Boolean adminOverwrite);
    public ReturnHelper deleteInvoice(Long invoiceID, Boolean adminOverwrite);  
    public ReturnHelper checkIfInvoiceisEditable(Long invoiceID, Boolean adminOverwrite);
    public ReturnHelper checkIfInvoiceNumberIsUnique(String invoiceNumber);
    
    public Invoice getInvoice(Long invoiceID);
    public List<Invoice> listAllInvoice(Long staffID);
    public List<Invoice> listInvoicesTiedToSCO(Long salesConfirmationOrderID);
    
    public ReturnHelper replaceInvoiceLineItemWithSCOitems(Long salesConfirmationOrderID, Long invoiceID, Boolean adminOverwrite);
    public ReturnHelper addInvoiceLineItem(Long invoiceID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite);
    public ReturnHelper updateInvoiceLineItem(Long invoiceID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite);
    public ReturnHelper deleteInvoiceLineItem(Long invoiceID, Long lineItemID, Boolean adminOverwrite);
    public ReturnHelper deleteallInvoiceLineItem(Long invoiceID, Boolean adminOverwrite);
    public List<LineItem> listInvoiceLineItems(Long invoiceID);
}
