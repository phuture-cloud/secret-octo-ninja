package OrderManagement;

import EntityManager.LineItem;
import EntityManager.OrderNumbers;
import EntityManager.PurchaseOrder;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import EntityManager.Supplier;
import EntityManager.SupplierContact;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PurchaseOrderManagementBean implements PurchaseOrderManagementBeanLocal {

    public PurchaseOrderManagementBean() {
    }

    @Resource
    private EJBContext context;
    @PersistenceContext
    private EntityManager em;

    private static final Double gstRate = 7.0;//7%

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private String getNewPurchaseOrderNumber() {
        System.out.println("PurchaseOrderManagementBean: TransactionAttributeType() called");
        Query q = em.createQuery("SELECT e FROM OrderNumbers e");
        q.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        OrderNumbers orderNumbers = (OrderNumbers) q.getResultList().get(0);
        Long nextOrderNumber = orderNumbers.getNextPO();
        orderNumbers.setNextPO(nextOrderNumber + 1);
        orderNumbers.setLastGeneratedPO(new Date());
        em.merge(orderNumbers);
        return nextOrderNumber.toString();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper createPurchaseOrder(Long salesConfirmationOrderID, Date purchaseOrderDate) {
        System.out.println("PurchaseOrderManagementBean: createPurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to create a new PO. The selected SCO may have been deleted while the PO is being created. Please try again.");
                return result;
            }
//            ReturnHelper uniqueResult = checkIfPOnumberIsUnique(purchaseOrderNumber);
//            if (!uniqueResult.getResult()) {
//                uniqueResult.setDescription("Failed to save the PO as the PO number is already in use.");
//                return uniqueResult;
//            }
            String purchaseOrderNumber = getNewPurchaseOrderNumber();

            //Create new PO
            PurchaseOrder po = new PurchaseOrder();
            po.setSalesConfirmationOrder(sco);
            po.setPurchaseOrderNumber(purchaseOrderNumber);
            po.setPurchaseOrderDate(purchaseOrderDate);
            em.persist(po);
            //Copy line items from SCO
            replacePOlineItemWithSCOitems(sco.getId(), po.getId(), true);
            //Update SCO list of POs
            List<PurchaseOrder> purchaseOrders = sco.getPurchaseOrders();
            purchaseOrders.add(po);
            sco.setPurchaseOrders(purchaseOrders);
            sco.setNumOfPurchaseOrders(sco.getNumOfPurchaseOrders() + 1);
            em.merge(sco);
            result.setID(po.getId());
            result.setResult(true);
            result.setDescription("PO created successfully.");
            return result;
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("PurchaseOrderManagementBean: createPurchaseOrder() could not find one or more ID(s).");
            result.setDescription("Failed to create the PO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("PurchaseOrderManagementBean: createPurchaseOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrder(Long purchaseOrderID, Long newSupplierContactID, Date purchaseOrderDate, String status, String terms, Date deliveryDate, String remarks, String currency, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
//            ReturnHelper uniqueResult = checkIfPOnumberIsUnique(purchaseOrderNumber);
//            if (!uniqueResult.getResult() && !purchaseOrderNumber.equals(po.getPurchaseOrderNumber())) {
//                uniqueResult.setDescription("Failed to save the PO as the PO number is already in use.");
//                return uniqueResult;
//            }
            ReturnHelper updateStatusResult = updatePurchaseOrderStatus(purchaseOrderID, status, adminOverwrite);
            if (updateStatusResult.getResult() == false) {
                return updateStatusResult;
            }
            if (newSupplierContactID != null) {
                q = em.createQuery("SELECT c FROM SupplierContact c WHERE c.id=:id");
                q.setParameter("id", newSupplierContactID);
                SupplierContact newSupplierContact = (SupplierContact) q.getSingleResult();
                String newSupplierName = newSupplierContact.getSupplier().getSupplierName();
                if (newSupplierContact.getIsDeleted()) {
                    result.setDescription("Failed to edit the PO. The selected supplier contact may have been deleted while the PO is being updated. Please try again.");
                    return result;
                }
                //Remove away the old links
                Supplier oldSupplier = po.getSupplierLink();
                if (oldSupplier != null) {
                    List<PurchaseOrder> oldSupplierPOs = oldSupplier.getPurchaseOrders();
                    oldSupplierPOs.remove(po);
                    em.merge(oldSupplier);
                }
                //Add links to other
                List<PurchaseOrder> supplierPOs = newSupplierContact.getSupplier().getPurchaseOrders();
                supplierPOs.add(po);
                newSupplierContact.getSupplier().setPurchaseOrders(supplierPOs);
                em.merge(newSupplierContact);
                //Update fields 
                po.setSupplierName(newSupplierName);
                po.setSupplierLink(newSupplierContact.getSupplier());
                po.setContactName(newSupplierContact.getName());
                po.setContactEmail(newSupplierContact.getEmail());
                po.setContactOfficeNo(newSupplierContact.getOfficeNo());
                po.setContactMobileNo(newSupplierContact.getMobileNo());
                po.setContactFaxNo(newSupplierContact.getFaxNo());
                po.setContactAddress(newSupplierContact.getAddress());
            }

            po.setPurchaseOrderDate(purchaseOrderDate);
//            po.setPurchaseOrderNumber(purchaseOrderNumber);
            po.setTerms(terms);
            po.setDeliveryDate(deliveryDate);
            po.setRemarks(remarks);
            po.setCurrency(currency);
            em.merge(po);
            result.setID(po.getId());
            result.setResult(true);
            result.setDescription("PO saved successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderSupplierContactDetails(Long purchaseOrderID, String supplierName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderSupplierContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder purchaseOrder = (PurchaseOrder) q.getSingleResult();
            if (purchaseOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            purchaseOrder.setSupplierName(supplierName);
            purchaseOrder.setContactAddress(address);
            purchaseOrder.setContactEmail(email);
            purchaseOrder.setContactOfficeNo(officeNo);
            purchaseOrder.setContactFaxNo(faxNo);
            purchaseOrder.setContactMobileNo(mobileNo);
            purchaseOrder.setContactName(contactName);
            em.merge(purchaseOrder);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderSupplierContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit a PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderSupplierContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderSupplierContactDetails(Long purchaseOrderID, Long newSupplierContactID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderSupplierContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder purchaseOrder = (PurchaseOrder) q.getSingleResult();
            if (purchaseOrder.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }

            q = em.createQuery("SELECT c FROM SupplierContact c WHERE c.id=:id");
            q.setParameter("id", newSupplierContactID);
            SupplierContact supplierContact = (SupplierContact) q.getSingleResult();
            Supplier newSupplier = supplierContact.getSupplier();
            String newSupplierName = newSupplier.getSupplierName();
            if (newSupplier.getIsDeleted()) {
                result.setDescription("Failed to edit the PO. The selected supplier may have been deleted while the PO is being updated. Please try again.");
                return result;
            }
            // Update customer link if it's different
            if (newSupplier.getId() != purchaseOrder.getSupplierLink().getId()) {
                //Remove away the old links
                Supplier oldSupplier = purchaseOrder.getSupplierLink();
                List<PurchaseOrder> oldSupplierPOs = oldSupplier.getPurchaseOrders();
                oldSupplierPOs.remove(purchaseOrder);
                em.merge(oldSupplier);
                //Add links to other
                List<PurchaseOrder> supplierPOs = newSupplier.getPurchaseOrders();
                supplierPOs.add(purchaseOrder);
                newSupplier.setPurchaseOrders(supplierPOs);
                em.merge(newSupplier);
                purchaseOrder.setSupplierLink(newSupplier);
            }
            //Update fields
            purchaseOrder.setSupplierName(newSupplierName);
            purchaseOrder.setContactAddress(supplierContact.getAddress());
            purchaseOrder.setContactEmail(supplierContact.getEmail());
            purchaseOrder.setContactOfficeNo(supplierContact.getOfficeNo());
            purchaseOrder.setContactFaxNo(supplierContact.getFaxNo());
            purchaseOrder.setContactMobileNo(supplierContact.getMobileNo());
            purchaseOrder.setContactName(supplierContact.getName());
            em.merge(purchaseOrder);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderSupplierContactDetails() could not find one or more ID(s).");
            result.setDescription("Failed to edit a PO. The PO or supplier or contact selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderSupplierContactDetails() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderStatus(Long purchaseOrderID, String status, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderStatus() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            switch (status) {
                case "Pending":
                    po.setStatus("Pending");
                    break;
                case "Completed":
                    po.setStatus("Completed");
                    break;
                default:
                    result.setDescription("Failed to update the PO to the status specified.");
                    System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderStatus() received an unknown status.");
                    break;
            }
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderStatus() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderStatus() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderNotes(Long purchaseOrderID, String notes, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderNotes() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            po.setNotes(notes);
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderNotes() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderRemarks(Long purchaseOrderID, String remarks, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderRemarks() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            po.setRemarks(remarks);
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderRemarks() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderRemarks() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deletePurchaseOrder(Long purchaseOrderID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: deletePurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (!po.getIsDeleted()) {
                po.setIsDeleted(true);
                em.merge(po);
                SalesConfirmationOrder sco = po.getSalesConfirmationOrder();
                sco.setNumOfPurchaseOrders(sco.getNumOfPurchaseOrders() - 1);
                em.merge(sco);
            }
            result.setResult(true);
            result.setDescription("PO deleted successfully.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: deletePurchaseOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public ReturnHelper voidPurchaseOrder(Long purchaseOrderID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: voidPurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder purchaseOrder = (PurchaseOrder) q.getSingleResult();
            //ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            //if (!checkResult.getResult()) {
//                result.setDescription(checkResult.getDescription());
//                return result;
//            }
            if (!purchaseOrder.getStatus().equals("Voided")) {
                purchaseOrder.setStatus("Voided");
                em.merge(purchaseOrder);
            }
            result.setResult(true);
            result.setDescription("PO voided.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("PurchaseOrderManagementBean: voidPurchaseOrder() failed");
            result.setDescription("Failed to void an PO due to internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfPOnumberIsUnique(String purchaseOrderNumber) {
        System.out.println("PurchaseOrderManagementBean: checkIfPOnumberIsUnique() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.purchaseOrderNumber=:number AND s.isDeleted=false");
            q.setParameter("number", purchaseOrderNumber);
            List<PurchaseOrder> purchaseOrders = q.getResultList();
            if (purchaseOrders.size() == 0) {
                result.setResult(true);
                result.setDescription("PO number is unique");
                return result;
            } else {
                result.setDescription("PO number is already in use.");
                return result;
            }
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: checkIfPOnumberIsUnique() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfPOisEditable(Long purchaseOrderID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: checkIfPOisEditable() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder purchaseOrder = (PurchaseOrder) q.getSingleResult();
            if (!adminOverwrite) {//If not admin account
                //Check if PO status is shipped. Prevent editing if it is already shipped.
                //if (purchaseOrder.getStatus().equals("Shipped")) {
                //    result.setDescription("PO can not be edited/deleted as the first invoice has already been issued.");
                //    return result;
                //}
            }
            if (purchaseOrder.getIsDeleted()) {
                result.setDescription("PO can not be edited/deleted as it has already been deleted.");
                return result;
            }
            if (purchaseOrder.getStatus().equals("Voided")) {
                result.setDescription("PO can not be edited/deleted as it has already been voided.");
                return result;
            }
            result.setResult(true);
            result.setDescription("Editable PO.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: checkIfPOisEditable() can not find PO");
            result.setDescription("Unable to complete request, PO not found.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: checkIfPOisEditable() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public PurchaseOrder getPurchaseOrder(Long purchaseOrderID) {
        System.out.println("PurchaseOrderManagementBean: getPurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id AND s.isDeleted=false");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            return po;
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: getPurchaseOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PurchaseOrder> listAllPurchaseOrder(Long staffID) {
        System.out.println("PurchaseOrderManagementBean: listAllPurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
            q.setParameter("staffID", staffID);
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsAdmin()) {
                //List all for admin
                q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.isDeleted=false");
            } else {
                //List only those that they create for normal staff
                q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.isDeleted=false and s.salesConfirmationOrder.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<PurchaseOrder> purchases = q.getResultList();
            return purchases;
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: listAllPurchaseOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PurchaseOrder> listPurchaseOrdersTiedToSCO(Long salesConfirmationOrderID) {
        System.out.println("PurchaseOrderManagementBean: listPurchaseOrderTiedToSCO() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.isDeleted=false AND s.salesConfirmationOrder.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            List<PurchaseOrder> purchaseOrders = q.getResultList();
            return purchaseOrders;
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: listPurchaseOrderTiedToSCO() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper replacePOlineItemWithSCOitems(Long salesConfirmationOrderID, Long purchaseOrderID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: replacePOlineItemWithSCOitems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            q = em.createQuery("SELECT p FROM PurchaseOrder p WHERE p.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (sco.getIsDeleted() || po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO. The selected SCO or PO may have been deleted while the PO is being created. Please try again.");
                return result;
            }
            //Delete all the line items first
            ReturnHelper deleteResult = deleteallPOlineItem(purchaseOrderID, adminOverwrite);
            if (!deleteResult.getResult()) {
                return deleteResult;
            }
            List<LineItem> scoLineItems = sco.getItems();
            List<LineItem> poLineItems = new ArrayList<>();
            for (LineItem scoLineItem : scoLineItems) {
                //Create the new line item for PO
                LineItem item = new LineItem();
                item.setItemName(scoLineItem.getItemName());
                item.setItemDescription(scoLineItem.getItemDescription());
                item.setItemQty(scoLineItem.getItemQty());
                item.setItemUnitPrice(scoLineItem.getItemUnitPrice());
                em.persist(item);
                //Add the line item to the PO
                poLineItems.add(item);
            }
            po.setItems(poLineItems);
            //Update PO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : poLineItems) {
                totalPrice = totalPrice + (curLineItem.getItemUnitPrice() * curLineItem.getItemQty());
                //Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                //totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                //totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            po.setTotalPrice(totalPrice);
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: replacePOlineItemWithSCOitems() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO or SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: replacePOlineItemWithSCOitems() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit the PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper addPOlineItem(Long purchaseOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: addPOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            LineItem lineItem = new LineItem();
            lineItem.setItemName(itemName);
            lineItem.setItemDescription(itemDescription);
            lineItem.setItemQty(itemQty);
            lineItem.setItemUnitPrice(itemUnitPrice);
            em.persist(lineItem);
            List<LineItem> lineItems = po.getItems();
            lineItems.add(lineItem);
            po.setItems(lineItems);
            //Update PO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                totalPrice = totalPrice + (curLineItem.getItemUnitPrice() * curLineItem.getItemQty());
                //Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                //totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                //totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            po.setTotalPrice(totalPrice);
            em.merge(po);
            result.setResult(true);
            result.setDescription("Item added.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: addPOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: addPOlineItem() failed");
            result.setDescription("Unable to add line item, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updatePOlineItem(Long purchaseOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: updatePOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
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
            //Update SCO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            List<LineItem> lineItems = po.getItems();
            for (LineItem curLineItem : lineItems) {
                totalPrice = totalPrice + (curLineItem.getItemUnitPrice() * curLineItem.getItemQty());
                //Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                //totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                //totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            po.setTotalPrice(totalPrice);
            em.merge(po);
            result.setResult(true);
            result.setDescription("Line item updated.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO or item selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePOlineItem() failed");
            result.setDescription("Unable to update line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ReturnHelper deletePOlineItem(Long purchaseOrderID, Long lineItemID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: deletePOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to delete the item as the PO has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            List<LineItem> lineItems = po.getItems();
            lineItems.remove(lineItem);
            po.setItems(lineItems);
            //Update SCO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                totalPrice = totalPrice + (curLineItem.getItemUnitPrice() * curLineItem.getItemQty());
                //Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                //totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                //totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            po.setTotalPrice(totalPrice);
            em.merge(po);
            em.remove(lineItem);
            result.setResult(true);
            result.setDescription("Item deleted.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: deletePOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the PO. The PO or item selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: deletePOlineItem() failed");
            result.setDescription("Unable to delete line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public ReturnHelper deleteallPOlineItem(Long purchaseOrderID, Boolean adminOverwrite) {
        System.out.println("PurchaseOrderManagementBean: deleteallPOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (po.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfPOisEditable(purchaseOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            List<LineItem> lineItems = po.getItems();
            for (LineItem lineItem : lineItems) {
                lineItems.remove(lineItem);
                em.remove(lineItem);
            }
            po.setItems(lineItems);
            po.setTotalPrice(0.0);
            em.merge(po);
            result.setResult(true);
            result.setDescription("Line items deleted.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: deleteallPOlineItem() failed");
            result.setDescription("Unable to edit line items, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public List<LineItem> listPOlineItems(Long purchaseOrderID) {
        System.out.println("PurchaseOrderManagementBean: listPOlineItems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            List<LineItem> lineItems = po.getItems();
            return lineItems;
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: listPOlineItems() failed");
            ex.printStackTrace();
            return null;
        }
    }

}
