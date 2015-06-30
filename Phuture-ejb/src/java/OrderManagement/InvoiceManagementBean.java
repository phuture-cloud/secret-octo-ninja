package OrderManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.DeliveryOrder;
import EntityManager.Invoice;
import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class InvoiceManagementBean implements InvoiceManagementBeanLocal {

    public InvoiceManagementBean() {
    }

    @PersistenceContext
    private EntityManager em;

    private static final Double gstRate = 7.0;//7%
    
    @Override
    public ReturnHelper createInvoice(Long salesConfirmationOrderID, String invoiceNumber) {
        System.out.println("InvoiceManagementBean: createInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to create a new invoice. The selected SCO may have been deleted while the invoice is being created. Please try again.");
                return result;
            }
            ReturnHelper uniqueResult = checkIfInvoiceNumberIsUnique(invoiceNumber);
            if(!uniqueResult.getResult()) {
                uniqueResult.setDescription("Failed to save the invoice as the invoice number is already in use.");
                return uniqueResult;
            }
            //Create new invoice
            Invoice invoice = new Invoice(invoiceNumber);
            invoice.setSalesConfirmationOrder(sco);
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setTaxRate(gstRate);
            //Copy SCO details
            invoice.setEstimatedDeliveryDate(sco.getEstimatedDeliveryDate());
            invoice.setCustomerPurchaseOrderNumber(sco.getCustomerPurchaseOrderNumber());
            //Copy contacts details from SCO to use as default for billing contact
            invoice.setCustomerName(sco.getCustomerName());
            invoice.setContactName(sco.getContactName());
            invoice.setContactEmail(sco.getContactName());
            invoice.setContactAddress(sco.getContactName());
            invoice.setContactMobileNo(sco.getContactName());
            invoice.setContactOfficeNo(sco.getContactName());
            invoice.setContactFaxNo(sco.getContactName());
            em.persist(invoice);
            //Copy line items from SCO
            replaceInvoiceLineItemWithSCOitems(sco.getId(),invoice.getId(),false);
            //Update SCO list of invoice
            List<Invoice> invoices = sco.getInvoices();
            invoices.add(invoice);
            sco.setInvoices(invoices);
            em.merge(sco);
            result.setID(invoice.getId());
            result.setResult(true);
            result.setDescription("Invoice created successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: createInvoice() could not find one or more ID(s).");
            result.setDescription("Failed to create the invoice. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: createInvoice() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoice(Long invoiceID, String newInvoiceNumber, Date invoiceSent, Date invoicePaid, String estimatedDeliveryDate, String customerPurchaseOrderNumber, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            ReturnHelper uniqueResult = checkIfInvoiceNumberIsUnique(newInvoiceNumber);
            if(!uniqueResult.getResult() && !newInvoiceNumber.equals(invoice.getInvoiceNumber())) {
                uniqueResult.setDescription("Failed to save the invoice as the invoice number is already in use.");
                return uniqueResult;
            }
            //Update fields 
            invoice.setInvoiceNumber(newInvoiceNumber);
            invoice.setDateSent(invoiceSent);
            invoice.setEstimatedDeliveryDate(estimatedDeliveryDate);
            invoice.setCustomerPurchaseOrderNumber(customerPurchaseOrderNumber);
            if (invoiceSent!=null) {
                invoice.setStatusAsSent();
            }
            invoice.setDatePaid(invoicePaid);
            if (invoicePaid!=null) {
                invoice.setStatusAsPaid();
            }
            em.merge(invoice);
            result.setID(invoice.getId());
            result.setResult(true);
            result.setDescription("Invoice saved successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoice() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoice() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setCustomerName(customerName);
            invoice.setContactAddress(address);
            invoice.setContactName(email);
            invoice.setContactOfficeNo(officeNo);
            invoice.setContactFaxNo(faxNo);
            invoice.setContactMobileNo(mobileNo);
            invoice.setContactName(contactName);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceCustomerContactDetails(Long invoiceID, Long customerID, Long contactID, Boolean adminOverwrite) {
         System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT c FROM Contact c WHERE c.id=:id");
            q.setParameter("id", contactID);
            Contact contact = (Contact) q.getSingleResult();
            q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id");
            q.setParameter("id", customerID);
            Customer customer = (Customer) q.getSingleResult();
            invoice.setCustomerName(customer.getCustomerName());
            invoice.setContactAddress(contact.getAddress());
            invoice.setContactEmail(contact.getEmail());
            invoice.setContactOfficeNo(contact.getOfficeNo());
            invoice.setContactFaxNo(contact.getFaxNo());
            invoice.setContactMobileNo(contact.getMobileNo());
            invoice.setContactName(contact.getName());
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice or customer or contact selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceRemarks(Long invoiceID, String remarks, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceRemarks() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setRemarks(remarks);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceRemarks() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceRemarks() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceNotes(Long invoiceID, String notes, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceNotes() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setNotes(notes);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceNotes() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteInvoice(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: deleteInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            invoice.setIsDeleted(true);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Invoice deleted successfully.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: deleteInvoice() failed");
            result.setDescription("Failed to delete the invoice due to internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfInvoiceisEditable(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: checkIfInvoiceisEditable() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (!adminOverwrite) {//If not admin account
                //Check if Invoice status is sent or paid. Prevent editing if it is already sent.
                //if (invoice.getStatus().equals("Sent")||invoice.getStatus().equals("Paid")) {
                //    result.setDescription("Invoice can not be edited/deleted as the first invoice has already been issued.");
                //    return result;
                //}
            }
            if (invoice.getIsDeleted()) {
                result.setDescription("Invoice can not be edited/deleted as it has already been deleted.");
                return result;
            }
            result.setResult(true);
            result.setDescription("Editable invoice.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: checkIfInvoiceisEditable() can not find invoice");
            result.setDescription("Unable to complete request, invoice not found.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: checkIfInvoiceisEditable() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }
    
     @Override
    public ReturnHelper checkIfInvoiceNumberIsUnique(String invoiceNumber) {
        System.out.println("InvoiceManagementBean: checkIfInvoiceNumberIsUnique() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.invoiceNumber=:number");
            q.setParameter("number", invoiceNumber);
            List<Invoice> invoices = q.getResultList();
            if (invoices.size() == 0) {
                result.setResult(true);
                result.setDescription("Invoice number is unique");
                return result;
            } else {
                result.setDescription("Invoice number is already in use.");
                return result;
            }
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: checkIfInvoiceNumberIsUnique() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public Invoice getInvoice(Long invoiceID) {
        System.out.println("InvoiceManagementBean: getInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            return invoice;
        } catch (EntityNotFoundException ex) {
            System.out.println("InvoiceManagementBean: getInvoice(): invoice not found");
            return null;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: getInvoice() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Invoice> listAllInvoice(Long staffID) {
        System.out.println("InvoiceManagementBean: listAllInvoice() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
            q.setParameter("staffID", staffID);
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsAdmin()) {
                //List all for admin
                q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false");
            } else {
                //List only those that they create for normal staff
                q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false and s.salesConfirmationOrder.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listAllInvoice() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Invoice> listInvoicesTiedToSCO(Long salesConfirmationOrderID) {
        System.out.println("InvoiceManagementBean: listInvoicesTiedToSCO() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false AND s.salesConfirmationOrder.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listInvoicesTiedToSCO() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }
    
        @Override
    public List<Invoice> listInvoicesTiedToCustomer(Long customerID) {
        System.out.println("InvoiceManagementBean: listInvoicesTiedToCustomer() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.isDeleted=false AND s.salesConfirmationOrder.customerLink.id=:id");
            q.setParameter("id", customerID);
            List<Invoice> invoices = q.getResultList();
            return invoices;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listInvoicesTiedToCustomer() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper replaceInvoiceLineItemWithSCOitems(Long salesConfirmationOrderID, Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: replaceInvoiceLineItemWithSCOitems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (sco.getIsDeleted() || invoice.getIsDeleted()) {
                result.setDescription("Failed to edit a invoice. The selected SCO or invoice may have been deleted while the invoice is being edited..");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            //Delete all the line items in the invoice
            ReturnHelper deleteResult = new ReturnHelper();
            deleteResult = deleteallInvoiceLineItem(invoiceID, adminOverwrite);
            if (!deleteResult.getResult()) {
                return deleteResult;
            }
            //Copy line items from SCO
            List<LineItem> scoLineItems = sco.getItems();
            List<LineItem> invoiceLineItems = new ArrayList<>();
            for (LineItem scoLineItem : scoLineItems) {
                //Create the new line item for invoice
                LineItem item = new LineItem();
                item.setItemName(scoLineItem.getItemName());
                item.setItemDescription(scoLineItem.getItemDescription());
                item.setItemQty(scoLineItem.getItemQty());
                item.setItemUnitPrice(scoLineItem.getItemUnitPrice());
                em.persist(item);
                //Add the line item to the invoice
                invoiceLineItems.add(item);
            }
            invoice.setItems(invoiceLineItems);
            //Update invoice total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : invoiceLineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPrice(totalPrice);
            invoice.setTotalTax(totalTax);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Items copied from SCO.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: replaceInvoiceLineItemWithSCOitems() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: replaceInvoiceLineItemWithSCOitems() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the invoice due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper addInvoiceLineItem(Long invoiceID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: addInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            LineItem lineItem = new LineItem();
            lineItem.setItemName(itemName);
            lineItem.setItemDescription(itemDescription);
            lineItem.setItemQty(itemQty);
            lineItem.setItemUnitPrice(itemUnitPrice);
            em.persist(lineItem);
            List<LineItem> lineItems = invoice.getItems();
            lineItems.add(lineItem);
            invoice.setItems(lineItems);
            //Update invoice total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPrice(totalPrice);
            invoice.setTotalTax(totalTax);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Item added.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: addInvoiceLineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: addInvoiceLineItem() failed");
            result.setDescription("Unable to add line item, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateInvoiceLineItem(Long invoiceID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: updateInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            lineItem.setItemName(newItemName);
            lineItem.setItemDescription(newItemDescription);
            lineItem.setItemQty(newItemQty);
            lineItem.setItemUnitPrice(newItemUnitPrice);
            em.merge(lineItem);
            //Update invoice total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            List<LineItem> lineItems = invoice.getItems();
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPrice(totalPrice);
            invoice.setTotalTax(totalTax);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Line item updated.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceLineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice or item selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: updateInvoiceLineItem() failed");
            result.setDescription("Unable to update line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ReturnHelper deleteInvoiceLineItem(Long invoiceID, Long lineItemID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: deleteInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to delete the item as the invoice has been deleted.");
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            List<LineItem> lineItems = invoice.getItems();
            lineItems.remove(lineItem);
            invoice.setItems(lineItems);
            //Update invoice total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            invoice.setTotalPrice(totalPrice);
            invoice.setTotalTax(totalTax);
            em.merge(invoice);
            em.remove(lineItem);
            result.setResult(true);
            result.setDescription("Item deleted.");
        } catch (NoResultException ex) {
            System.out.println("InvoiceManagementBean: deleteInvoiceLineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the invoice. The invoice or item selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: deleteInvoiceLineItem() failed");
            result.setDescription("Unable to delete line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ReturnHelper deleteallInvoiceLineItem(Long invoiceID, Boolean adminOverwrite) {
        System.out.println("InvoiceManagementBean: deleteallInvoiceLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            if (invoice.getIsDeleted()) {
                result.setDescription("Failed to edit the invoice as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfInvoiceisEditable(invoiceID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            List<LineItem> lineItems = invoice.getItems();
            for (LineItem lineItem : lineItems) {
                lineItems.remove(lineItem);
                em.remove(lineItem);
            }
            invoice.setItems(lineItems);
            invoice.setTotalPrice(0.0);
            invoice.setTotalTax(0.0);
            em.merge(invoice);
            result.setResult(true);
            result.setDescription("Line items deleted.");
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: deleteallInvoiceLineItem() failed");
            result.setDescription("Unable to delete line items, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public List<LineItem> listInvoiceLineItems(Long invoiceID) {
                System.out.println("InvoiceManagementBean: listInvoiceLineItems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Invoice s WHERE s.id=:id");
            q.setParameter("id", invoiceID);
            Invoice invoice = (Invoice) q.getSingleResult();
            List<LineItem> lineItems = invoice.getItems();
            return lineItems;
        } catch (Exception ex) {
            System.out.println("InvoiceManagementBean: listInvoiceLineItems() failed");
            ex.printStackTrace();
            return null;
        }
    }
    
}
