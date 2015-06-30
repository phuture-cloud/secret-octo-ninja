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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class DeliveryOrderManagementBean implements DeliveryOrderManagementBeanLocal {

    public DeliveryOrderManagementBean() {
    }

    @PersistenceContext
    private EntityManager em;

    private static final Double gstRate = 7.0;//7%

    @Override
    public ReturnHelper createDeliveryOrder(Long salesConfirmationOrderID, String deliveryOrderNumber, Date deliveryOrderDate) {
        System.out.println("DeliveryOrderManagementBean: createDeliveryOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to create a new DO. The selected SCO may have been deleted while the DO is being created. Please try again.");
                return result;
            }
            ReturnHelper uniqueResult = checkIfDOnumberIsUnique(deliveryOrderNumber);
            if (!uniqueResult.getResult()) {
                uniqueResult.setDescription("Failed to save the DO as the DO number is already in use.");
                return uniqueResult;
            }
            //Create new DO
            DeliveryOrder deliveryOrder = new DeliveryOrder(deliveryOrderNumber);
            deliveryOrder.setSalesConfirmationOrder(sco);
            deliveryOrder.setDeliveryOrderNumber(deliveryOrderNumber);
            deliveryOrder.setDeliveryOrderDate(deliveryOrderDate);
            deliveryOrder.setTaxRate(gstRate);
            //Copy SCO details
            deliveryOrder.setCustomerPurchaseOrderNumber(sco.getCustomerPurchaseOrderNumber());
            //Copy contacts detail from SCO contact as default shipping contact
            deliveryOrder.setCustomerName(sco.getCustomerName());
            deliveryOrder.setContactName(sco.getContactName());
            deliveryOrder.setContactEmail(sco.getContactName());
            deliveryOrder.setContactAddress(sco.getContactName());
            deliveryOrder.setContactMobileNo(sco.getContactName());
            deliveryOrder.setContactOfficeNo(sco.getContactName());
            deliveryOrder.setContactFaxNo(sco.getContactName());
            em.persist(deliveryOrder);
            //Copy line items from SCO
            replaceDOlineItemWithSCOitems(sco.getId(),deliveryOrder.getId(),false);
            //Update SCO list of DOs
            List<DeliveryOrder> deliveryOrders = sco.getDeliveryOrders();
            deliveryOrders.add(deliveryOrder);
            sco.setDeliveryOrders(deliveryOrders);
            em.merge(sco);
            result.setID(deliveryOrder.getId());
            result.setResult(true);
            result.setDescription("DO created successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: createDeliveryOrder() could not find one or more ID(s).");
            result.setDescription("Failed to create the DO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: createDeliveryOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrder(Long deliveryOrderID, String newDeliveryOrderNumber, Date newDeliveryOrderDate, Date estimatedDeliveryDate, String customerPurchaseOrderNumber, String status, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDeliveryOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            ReturnHelper uniqueResult = checkIfDOnumberIsUnique(newDeliveryOrderNumber);
            if (!uniqueResult.getResult() && !newDeliveryOrderNumber.equals(deliveryOrder.getDeliveryOrderNumber())) {
                uniqueResult.setDescription("Failed to save the DO as the DO number is already in use.");
                return uniqueResult;
            }
            ReturnHelper updateStatusResult = updateDeliveryOrderStatus(deliveryOrderID, status, adminOverwrite);
            if (updateStatusResult.getResult() == false) {
                result.setDescription(updateStatusResult.getDescription());
                return result;
            }
            //Update fields 
            deliveryOrder.setDeliveryOrderDate(newDeliveryOrderDate);
            deliveryOrder.setDeliveryOrderNumber(newDeliveryOrderNumber);
            deliveryOrder.setCustomerPurchaseOrderNumber(customerPurchaseOrderNumber);
            em.merge(deliveryOrder);
            result.setID(deliveryOrder.getId());
            result.setResult(true);
            result.setDescription("DO saved successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrderCustomerContactDetails(Long deliveryOrderID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            deliveryOrder.setCustomerName(customerName);
            deliveryOrder.setContactAddress(address);
            deliveryOrder.setContactName(email);
            deliveryOrder.setContactOfficeNo(officeNo);
            deliveryOrder.setContactFaxNo(faxNo);
            deliveryOrder.setContactMobileNo(mobileNo);
            deliveryOrder.setContactName(contactName);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("DO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrderCustomerContactDetails(Long deliveryOrderID, Long customerID, Long contactID, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
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
            deliveryOrder.setCustomerName(customer.getCustomerName());
            deliveryOrder.setContactAddress(contact.getAddress());
            deliveryOrder.setContactEmail(contact.getEmail());
            deliveryOrder.setContactOfficeNo(contact.getOfficeNo());
            deliveryOrder.setContactFaxNo(contact.getFaxNo());
            deliveryOrder.setContactMobileNo(contact.getMobileNo());
            deliveryOrder.setContactName(contact.getName());
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("DO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO or customer or contact selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrderRemarks(Long deliveryOrderID, String remarks, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderRemarks() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            deliveryOrder.setRemarks(remarks);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("DO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderRemarks() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderRemarks() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrderNotes(Long deliveryOrderID, String notes, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderNotes() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            deliveryOrder.setNotes(notes);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("DO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderNotes() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrderStatus(Long deliveryOrderID, String status, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderStatus() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            switch (status) {
                case "Created":
                    deliveryOrder.setStatusAsCreated();
                    break;
                case "Shipped":
                    deliveryOrder.setStatusAsShipped();
                    break;
                default:
                    result.setDescription("Failed to update the DO to the status specified.");
                    System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderStatus() received an unknown status.");
                    break;
            }
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("DO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderStatus() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderStatus() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteDeliveryOrder(Long deliveryOrderID, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: deleteDeliveryOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            deliveryOrder.setIsDeleted(true);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("DO deleted successfully.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: deleteDeliveryOrder() failed");
            result.setDescription("Failed to delete an DO due to internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfDOisEditable(Long deliveryOrderID, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: checkIfDOisEditable() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (!adminOverwrite) {//If not admin account
                //Check if DO status is shipped. Prevent editing if it is already shipped.
                //if (deliveryOrder.getStatus().equals("Shipped")) {
                //    result.setDescription("DO can not be edited/deleted as the first invoice has already been issued.");
                //    return result;
                //}
            }
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("DO can not be edited/deleted as it has already been deleted.");
                return result;
            }
            result.setResult(true);
            result.setDescription("Editable DO.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: checkIfDOisEditable() can not find DO");
            result.setDescription("Unable to complete request, DO not found.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: checkIfDOisEditable() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfDOnumberIsUnique(String deliveryOrderNumber) {
        System.out.println("DeliveryOrderManagementBean: checkIfDOnumberIsUnique() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.deliveryOrderNumber=:number");
            q.setParameter("number", deliveryOrderNumber);
            List<DeliveryOrder> deliveryOrders = q.getResultList();
            if (deliveryOrders.size() == 0) {
                result.setResult(true);
                result.setDescription("DO number is unique");
                return result;
            } else {
                result.setDescription("DO number is already in use.");
                return result;
            }
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: checkIfDOnumberIsUnique() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public DeliveryOrder getDeliveryOrder(Long deliveryOrderID) {
        System.out.println("DeliveryOrderManagementBean: getDeliveryOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            return deliveryOrder;
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: getDeliveryOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DeliveryOrder> listAllDeliveryOrder(Long staffID) {
        System.out.println("DeliveryOrderManagementBean: listAllDeliveryOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
            q.setParameter("staffID", staffID);
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsAdmin()) {
                //List all for admin
                q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.isDeleted=false");
            } else {
                //List only those that they create for normal staff
                q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.isDeleted=false and s.salesConfirmationOrder.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<DeliveryOrder> deliveryOrders = q.getResultList();
            return deliveryOrders;
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: listAllDeliveryOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DeliveryOrder> listDeliveryOrdersTiedToSCO(Long salesConfirmationOrderID) {
        System.out.println("DeliveryOrderManagementBean: listDeliveryOrderTiedToSCO() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.isDeleted=false AND s.salesConfirmationOrder.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            List<DeliveryOrder> deliveryOrders = q.getResultList();
            return deliveryOrders;
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: listDeliveryOrderTiedToSCO() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper replaceDOlineItemWithSCOitems(Long salesConfirmationOrderID, Long deliveryOrderID, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: replaceDOlineItemWithSCOitems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (sco.getIsDeleted() || deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO. The selected SCO or DO may have been deleted while the DO is being edited..");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            //Delete all the line items in the DO
            ReturnHelper deleteResult = new ReturnHelper();
            deleteResult = deleteallDOlineItem(deliveryOrderID, adminOverwrite);
            if (!deleteResult.getResult()) {
                return deleteResult;
            }
            //Copy line items from SCO
            List<LineItem> scoLineItems = sco.getItems();
            List<LineItem> doLineItems = new ArrayList<>();
            for (LineItem scoLineItem : scoLineItems) {
                //Create the new line item for DO
                LineItem item = new LineItem();
                item.setItemName(scoLineItem.getItemName());
                item.setItemDescription(scoLineItem.getItemDescription());
                item.setItemQty(scoLineItem.getItemQty());
                item.setItemUnitPrice(scoLineItem.getItemUnitPrice());
                em.persist(item);
                //Add the line item to the DO
                doLineItems.add(item);
            }
            deliveryOrder.setItems(doLineItems);
            //Update DO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : doLineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            deliveryOrder.setTotalPrice(totalPrice);
            deliveryOrder.setTotalTax(totalTax);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("Items copied from SCO.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: replaceDOlineItemWithSCOitems() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: replaceDOlineItemWithSCOitems() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the DO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper addDOlineItem(Long deliveryOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: addDOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            LineItem lineItem = new LineItem();
            lineItem.setItemName(itemName);
            lineItem.setItemDescription(itemDescription);
            lineItem.setItemQty(itemQty);
            lineItem.setItemUnitPrice(itemUnitPrice);
            em.persist(lineItem);
            List<LineItem> lineItems = deliveryOrder.getItems();
            lineItems.add(lineItem);
            deliveryOrder.setItems(lineItems);
            //Update DO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            deliveryOrder.setTotalPrice(totalPrice);
            deliveryOrder.setTotalTax(totalTax);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("Item added.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: addDOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: addDOlineItem() failed");
            result.setDescription("Unable to add line item, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateDOlineItem(Long deliveryOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: updateDOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
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
            //Update DO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            List<LineItem> lineItems = deliveryOrder.getItems();
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            deliveryOrder.setTotalPrice(totalPrice);
            deliveryOrder.setTotalTax(totalTax);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("Line item updated.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO or item selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDOlineItem() failed");
            result.setDescription("Unable to update line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ReturnHelper deleteDOlineItem(Long deliveryOrderID, Long lineItemID, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: deleteDOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to delete the item as the DO has been deleted.");
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            List<LineItem> lineItems = deliveryOrder.getItems();
            lineItems.remove(lineItem);
            deliveryOrder.setItems(lineItems);
            //Update DO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            deliveryOrder.setTotalPrice(totalPrice);
            deliveryOrder.setTotalTax(totalTax);
            em.merge(deliveryOrder);
            em.remove(lineItem);
            result.setResult(true);
            result.setDescription("Item deleted.");
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: deleteDOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the DO. The DO or item selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: deleteDOlineItem() failed");
            result.setDescription("Unable to delete line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ReturnHelper deleteallDOlineItem(Long deliveryOrderID, Boolean adminOverwrite) {
        System.out.println("DeliveryOrderManagementBean: deleteallDOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            if (deliveryOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the DO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfDOisEditable(deliveryOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            List<LineItem> lineItems = deliveryOrder.getItems();
            for (LineItem lineItem : lineItems) {
                lineItems.remove(lineItem);
                em.remove(lineItem);
            }
            deliveryOrder.setItems(lineItems);
            deliveryOrder.setTotalPrice(0.0);
            deliveryOrder.setTotalTax(0.0);
            em.merge(deliveryOrder);
            result.setResult(true);
            result.setDescription("Line items deleted.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: deleteallDOlineItem() failed");
            result.setDescription("Unable to delete line items, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public List<LineItem> listDOlineItems(Long deliveryOrderID) {
        System.out.println("DeliveryOrderManagementBean: listDOlineItems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM DeliveryOrder s WHERE s.id=:id");
            q.setParameter("id", deliveryOrderID);
            DeliveryOrder deliveryOrder = (DeliveryOrder) q.getSingleResult();
            List<LineItem> lineItems = deliveryOrder.getItems();
            return lineItems;
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: listDOlineItems() failed");
            ex.printStackTrace();
            return null;
        }
    }

}
