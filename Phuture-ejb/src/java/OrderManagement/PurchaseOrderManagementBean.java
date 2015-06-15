package OrderManagement;

import EntityManager.Customer;
import EntityManager.LineItem;
import EntityManager.PurchaseOrder;
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
public class PurchaseOrderManagementBean implements PurchaseOrderManagementBeanLocal {

    public PurchaseOrderManagementBean() {
    }
    
    @PersistenceContext
    private EntityManager em;
    
    private static final Double gstRate = 7.0;//7%

    @Override
    public ReturnHelper createPurchaseOrder(Long salesConfirmationOrderID, String purchaseOrderNumber, Date purchaseOrderDate) {
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
            //Create new PO
            PurchaseOrder po = new PurchaseOrder();
            po.setSalesConfirmationOrder(sco);
            po.setPurchaseOrderNumber(purchaseOrderNumber);
            po.setPurchaseOrderDate(purchaseOrderDate);
            po.setTaxRate(gstRate);
            em.persist(po);
            //Update SCO list of POs
            List<PurchaseOrder> purchaseOrders = sco.getPurchaseOrders();
            purchaseOrders.add(po);
            sco.setPurchaseOrders(purchaseOrders);
            em.merge(sco);
            result.setID(po.getId());
            result.setResult(true);
            result.setDescription("PO created successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: createPurchaseOrder() could not find one or more ID(s).");
            result.setDescription("Failed to create a PO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: createPurchaseOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrder(Long purchaseOrderID, String status, String notes) {
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
            ReturnHelper updateStatusResult = updatePurchaseOrderStatus(purchaseOrderID, status);
            if (updateStatusResult.getResult() == false) {
                return updateStatusResult;
            }
            if (notes == null) {
                notes = "";
            }
            po.setNotes(notes);
            em.merge(po);
            result.setID(po.getId());
            result.setResult(true);
            result.setDescription("PO saved successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a new PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderStatus(Long purchaseOrderID, String status) {
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
            result.setDescription("Failed to edit a PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderStatus() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updatePurchaseOrderNotes(Long purchaseOrderID, String notes) {
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
            po.setNotes(notes);
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderNotes() could not find one or more ID(s).");
            result.setDescription("Failed to edit a PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: updatePurchaseOrderNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a PO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deletePurchaseOrder(Long purchaseOrderID) {
        System.out.println("PurchaseOrderManagementBean: deletePurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            po.setIsDeleted(true);
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO deleted successfully.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: deletePurchaseOrder() failed");
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
    public List<PurchaseOrder> listAllPurchaseOrder() {
        System.out.println("PurchaseOrderManagementBean: listAllPurchaseOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.isDeleted=false");
            List<PurchaseOrder> purchaseOrders = q.getResultList();
            return purchaseOrders;
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: listAllPurchaseOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PurchaseOrder> listPurchaseOrderTiedToSCO(Long salesConfirmationOrderID) {
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
    public ReturnHelper replacePOlineItemWithSCOitems(Long salesConfirmationOrderID, Long purchaseOrderID) {
        System.out.println("PurchaseOrderManagementBean: replacePOlineItemWithSCOitems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            q = em.createQuery("SELECT p FROM PurchaseOrder p WHERE p.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder po = (PurchaseOrder) q.getSingleResult();
            if (sco.getIsDeleted() || po.getIsDeleted()) {
                result.setDescription("Failed to edit a new PO. The selected SCO or PO may have been deleted while the PO is being created. Please try again.");
                return result;
            }
            //Delete all the line items first
            ReturnHelper deleteResult = deleteallPOlineItem(purchaseOrderID);
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
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate/100);
            }
            po.setTotalPrice(totalPrice);
            po.setTotalTax(totalTax);
            em.merge(po);
            result.setResult(true);
            result.setDescription("PO edited successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: replacePOlineItemWithSCOitems() could not find one or more ID(s).");
            result.setDescription("Failed to edit a PO. The PO or SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: replacePOlineItemWithSCOitems() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a PO due to internal server error.");
        }
        return result;
    }
    
    @Override
    public ReturnHelper addPOlineItem(Long purchaseOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice) {
        System.out.println("PurchaseOrderManagementBean: addPOlineItem() called");
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
            LineItem lineItem = new LineItem();
            lineItem.setItemName(itemName);
            lineItem.setItemDescription(itemDescription);
            lineItem.setItemQty(itemQty);
            lineItem.setItemUnitPrice(itemUnitPrice);
            em.persist(lineItem);
            List<LineItem> lineItems = po.getItems();
            lineItems.add(lineItem);
            po.setItems(lineItems);
            //Update SCO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate/100);
            }
            po.setTotalPrice(totalPrice);
            po.setTotalTax(totalTax);
            em.merge(po);
            result.setResult(true);
            result.setDescription("Item added.");
        } catch (NoResultException ex) {
            System.out.println("PurchaseOrderManagementBean: addPOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit a PO. The PO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("PurchaseOrderManagementBean: addPOlineItem() failed");
            result.setDescription("Unable to add line item, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updatePOlineItem(Long purchaseOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice) {
        System.out.println("PurchaseOrderManagementBean: updatePOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM PurchaseOrder s WHERE s.id=:id");
            q.setParameter("id", purchaseOrderID);
            PurchaseOrder sco = (PurchaseOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the PO as it has been deleted.");
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
            List<LineItem> lineItems = sco.getItems();
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate/100);
            }
            sco.setTotalPrice(totalPrice);
            sco.setTotalTax(totalTax);
            em.merge(sco);
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
    public ReturnHelper deletePOlineItem(Long purchaseOrderID, Long lineItemID) {
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
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate/100);
            }
            po.setTotalPrice(totalPrice);
            po.setTotalTax(totalTax);
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
    public ReturnHelper deleteallPOlineItem(Long purchaseOrderID) {
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
            List<LineItem> lineItems = po.getItems();
            for (LineItem lineItem : lineItems) {
                lineItems.remove(lineItem);
                em.remove(lineItem);
            }
            po.setItems(lineItems);
            po.setTotalPrice(0.0);
            po.setTotalTax(0.0);
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
