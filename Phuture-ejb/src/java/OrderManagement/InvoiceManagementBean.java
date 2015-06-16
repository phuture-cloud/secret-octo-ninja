package OrderManagement;

import EntityManager.Invoice;
import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class InvoiceManagementBean implements InvoiceManagementBeanLocal {

    public InvoiceManagementBean() {
    }

    @PersistenceContext
    private EntityManager em;

    private static final Double gstRate = 7.0;//7%
    
    @Override
    public ReturnHelper createInvoice(Long salesConfirmationOrderID, String invoiceNumber, Date invoiceDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoice(Long invoiceID, String newInvoiceNumber, Date newDelvieryOrderDate, String status, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, Long customerID, Long contactID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoiceRemarks(Long invoiceID, String remarks, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoiceNotes(Long invoiceID, String notes, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoiceStatus(Long invoiceID, String status, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteInvoice(Long invoiceID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper checkIfInvoiceisEditable(Long invoiceID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Invoice getInvoice(Long invoiceID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Invoice> listAllInvoice() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Invoice> listInvoicesTiedToSCO(Long salesConfirmationOrderID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper replaceInvoiceLineItemWithSCOitems(Long salesConfirmationOrderID, Long invoiceID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper addInvoiceLineItem(Long invoiceID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateInvoiceLineItem(Long invoiceID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteInvoiceLineItem(Long invoiceID, Long lineItemID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteallInvoiceLineItem(Long invoiceID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<LineItem> listInvoiceLineItems(Long invoiceID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
