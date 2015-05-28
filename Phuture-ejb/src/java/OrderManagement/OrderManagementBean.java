package OrderManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.Invoice;
import EntityManager.LineItem;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.Staff;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class OrderManagementBean implements OrderManagementBeanLocal {

    public OrderManagementBean() {
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public ReturnHelper createSalesConfirmationOrder(String salesConfirmationOrderNumber, Long customerID, Long salesStaffID, Integer terms, String remarks, String notes) {
        System.out.println("OrderManagementBean: addSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id");
            q.setParameter("id", customerID);
            Customer customer = (Customer) q.getSingleResult();
            String customerName = customer.getCustomerName();
            q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
            q.setParameter("id", salesStaffID);
            Staff staff = (Staff) q.getSingleResult();
            if (customer.getIsDeleted()) {
                result.setDescription("Failed to create a new SCO. The selected customer may have been deleted while the SCO is being created. Please try again.");
                return result;
            }
            if (remarks == null) {
                remarks = "";
            }
            if (notes == null) {
                notes = "";
            }
            SalesConfirmationOrder sco = new SalesConfirmationOrder(salesConfirmationOrderNumber, customerName, staff, terms, remarks, notes);
            sco.setCustomer(customer);
            em.persist(sco);
            //Add links to other
            List<SalesConfirmationOrder> customerSCOs = customer.getSCOs();
            customerSCOs.add(sco);
            customer.setSCOs(customerSCOs);
            em.merge(customer);
            List<SalesConfirmationOrder> staffSCOs = staff.getSales();
            staffSCOs.add(sco);
            staff.setSales(staffSCOs);
            em.merge(staff);
            result.setID(sco.getId());
            result.setResult(true);
            result.setDescription("SCO created successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: addSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The customer or staff selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: addSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrder(Long salesConfirmationOrderID, String newSalesConfirmationOrderNumber, Long newCustomerID, Long newSalesStaffID, Integer newTerms, String newRemarks, String newNotes, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();

            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id");
            q.setParameter("id", newCustomerID);
            Customer newCustomer = (Customer) q.getSingleResult();
            String newCustomerName = newCustomer.getCustomerName();
            q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
            q.setParameter("id", newSalesStaffID);
            Staff staff = (Staff) q.getSingleResult();
            if (newCustomer.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO. The selected customer may have been deleted while the SCO is being updated. Please try again.");
                return result;
            }
            if (newRemarks == null) {
                newRemarks = "";
            }
            if (newNotes == null) {
                newNotes = "";
            }
            //Remove away the old links
            Customer oldCustomer = sco.getCustomer();
            List<SalesConfirmationOrder> oldCustomerSCOs = oldCustomer.getSCOs();
            oldCustomerSCOs.remove(sco);
            em.merge(oldCustomer);
            Staff oldStaff = sco.getSalesPerson();
            List<SalesConfirmationOrder> oldStaffSCOs = oldStaff.getSales();
            oldStaffSCOs.remove(sco);
            em.merge(oldStaff);
            //Add links to other
            List<SalesConfirmationOrder> customerSCOs = newCustomer.getSCOs();
            customerSCOs.add(sco);
            newCustomer.setSCOs(customerSCOs);
            em.merge(newCustomer);
            List<SalesConfirmationOrder> staffSCOs = staff.getSales();
            staffSCOs.add(sco);
            staff.setSales(staffSCOs);
            em.merge(staff);
            //Update fields
            sco.setCustomerName(newCustomerName);
            sco.setCustomer(newCustomer);
            sco.setSalesConfirmationOrderNumber(newSalesConfirmationOrderNumber);
            sco.setTerms(newTerms);
            sco.setRemarks(newRemarks);
            sco.setNotes(newNotes);
            em.merge(sco);
            result.setID(sco.getId());
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The customer or staff selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a new SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrderContactDetails(Long salesConfirmationOrderID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrderContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();

            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            sco.setContactAddress(address);
            sco.setContactEmail(email);
            sco.setContactOfficeNo(officeNo);
            sco.setContactFaxNo(faxNo);
            sco.setContactMobileNo(mobileNo);
            sco.setContactName(name);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a new SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteSalesConfirmationOrder(Long salesConfirmationOrderID) {
        System.out.println("OrderManagementBean: deleteSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            Boolean adminOverwrite = false; //Admin also has to stick by the cannot delete SCO rule
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            sco.setIsDeleted(true);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO deleted successfully.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: deleteSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfSCOisEditable(Long salesConfirmationOrderID, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: checkIfSCOisEditable() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (!adminOverwrite) {//If not admin account
                //Check if any invoices has been sent to customer
                //Prevent editing of SCO it it has been sent
                List<Invoice> invoices = sco.getInvoices();
                for (Invoice invoice : invoices) {
                    if (invoice.getStatus().equals("Sent")) {
                        result.setDescription("SCO can not be edited/deleted as the first invoice has already been issued.");
                        return result;
                    }
                }
            }
            if (sco.getIsDeleted()) {
                result.setDescription("SCO can not be edited/deleted as it has already been deleted.");
                return result;
            }
            result.setResult(true);
            result.setDescription("Editable SCO.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: checkIfSCOisEditable() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<SalesConfirmationOrder> listAllSalesConfirmationOrder() {
        System.out.println("OrderManagementBean: listAllSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false");
            List<SalesConfirmationOrder> salesConfirmationOrders = q.getResultList();
            return salesConfirmationOrders;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: listAllSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SalesConfirmationOrder> listCustomerSalesConfirmationOrder(Long customerID) {
        System.out.println("OrderManagementBean: listCustomerSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false AND s.customerLink.id=:id");
            q.setParameter("id", customerID);
            List<SalesConfirmationOrder> salesConfirmationOrders = q.getResultList();
            return salesConfirmationOrders;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: listAllSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper addSCOlineItem(Long salesConfirmationOrderID, String itemName, String itemDescription, Integer itemQty, Double itemTotalPrice) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper updateSCOlineItem(Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemTotalPrice) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteSCOlineItem(Long lineItemID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<LineItem> listSCOlineItems(Long salesConfirmationOrderID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
