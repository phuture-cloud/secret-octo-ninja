package OrderManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.DeliveryOrder;
import EntityManager.LineItem;
import EntityManager.PurchaseOrder;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
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
            //Create new DO
            DeliveryOrder deliveryOrder = new DeliveryOrder(deliveryOrderNumber);
            deliveryOrder.setSalesConfirmationOrder(sco);
            deliveryOrder.setDeliveryOrderNumber(deliveryOrderNumber);
            deliveryOrder.setDeliveryOrderDate(deliveryOrderDate);
            deliveryOrder.setTaxRate(gstRate);
            //Copy shipping contacts from SCO contact as default
            deliveryOrder.setCustomerName(sco.getCustomerName());
            deliveryOrder.setContactName(sco.getContactName());
            deliveryOrder.setContactEmail(sco.getContactName());
            deliveryOrder.setContactAddress(sco.getContactName());
            deliveryOrder.setContactMobileNo(sco.getContactName());
            deliveryOrder.setContactOfficeNo(sco.getContactName());
            deliveryOrder.setContactFaxNo(sco.getContactName());
            em.persist(deliveryOrder);
            //Update SCO list of DOs
            List<DeliveryOrder> deliveryOrders = sco.getDeliveryOrders();
            deliveryOrders.add(deliveryOrder);
            sco.setDeliveryOrders(deliveryOrders);
            em.merge(sco);
            result.setID(deliveryOrder.getId());
            result.setResult(true);
            result.setDescription("PO created successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: createDeliveryOrder() could not find one or more ID(s).");
            result.setDescription("Failed to create a PO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: createDeliveryOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateDeliveryOrder(Long deliveryOrderID, String newDeliveryOrderNumber, Date newDelvieryOrderDate, String status, Boolean adminOverwrite) {
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
            ReturnHelper updateStatusResult = updateDeliveryOrderStatus(deliveryOrderID, status, adminOverwrite);
            if (updateStatusResult.getResult() == false) {
                result.setDescription(updateStatusResult.getDescription());
                return result;
            }
            //Update fields 
            deliveryOrder.setDeliveryOrderDate(newDelvieryOrderDate);
            deliveryOrder.setDeliveryOrderNumber(newDeliveryOrderNumber);
            em.merge(deliveryOrder);
            result.setID(deliveryOrder.getId());
            result.setResult(true);
            result.setDescription("DO saved successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a DO. The delivery selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a DO due to internal server error.");
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
                result.setDescription("Failed to edit the SCO as it has been deleted.");
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
            result.setDescription("Failed to edit a DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a DO due to internal server error.");
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
                result.setDescription("Failed to edit the SCO as it has been deleted.");
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
            result.setDescription("Failed to edit a DO. The DO or customer or contact selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderCustomerContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a DO due to internal server error.");
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
            result.setDescription("Failed to edit a DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderRemarks() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a DO due to internal server error.");
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
            result.setDescription("Failed to edit a DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a DO due to internal server error.");
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
            result.setDescription("Failed to edit a DO. The DO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("DeliveryOrderManagementBean: updateDeliveryOrderStatus() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a DO due to internal server error.");
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
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfDOisEditable(Long deliveryOrderID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DeliveryOrder getDeliveryOrder(Long deliveryOrderID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DeliveryOrder> listAllDeliveryOrder() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DeliveryOrder> listDeliveryOrderTiedToSCO(Long salesConfirmationOrderID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper replacePOlineItemWithSCOitems(Long salesConfirmationOrderID, Long deliveryOrderID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper addPOlineItem(Long deliveryOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updatePOlineItem(Long deliveryOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deletePOlineItem(Long deliveryOrderID, Long lineItemID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteallPOlineItem(Long deliveryOrderID, Boolean adminOverwrite) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<LineItem> listPOlineItems(Long deliveryOrderID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
