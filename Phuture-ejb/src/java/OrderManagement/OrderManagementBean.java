package OrderManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.DeliveryOrder;
import EntityManager.Invoice;
import EntityManager.LineItem;
import EntityManager.OrderNumbers;
import EntityManager.PurchaseOrder;
import EntityManager.ReturnHelper;
import EntityManager.SalesConfirmationOrder;
import EntityManager.SalesConfirmationOrderHelper;
import EntityManager.Staff;
import PaymentManagement.PaymentManagementBeanLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class OrderManagementBean implements OrderManagementBeanLocal {

    public OrderManagementBean() {
    }

    @Resource
    private EJBContext context;

    @PersistenceContext
    private EntityManager em;

    @EJB
    private InvoiceManagementBeanLocal imbl;
    @EJB
    private DeliveryOrderManagementBeanLocal dombl;
    @EJB
    private PurchaseOrderManagementBeanLocal pombl;
    @EJB
    private PaymentManagementBeanLocal pmbl;

    private static final Double gstRate = 7.0;//7%

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    private String getNewSalesConfirmationOrderNumber() {
        System.out.println("OrderManagementBean: getNewSalesConfirmationOrderNumber() called");
        Query q = em.createQuery("SELECT e FROM OrderNumbers e");
        q.setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        OrderNumbers orderNumbers = (OrderNumbers) q.getResultList().get(0);
        Long nextOrderNumber = orderNumbers.getNextSCO();
        orderNumbers.setNextSCO(nextOrderNumber + 1);
        orderNumbers.setLastGeneratedSCO(new Date());
        em.merge(orderNumbers);
        return nextOrderNumber.toString();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper createSalesConfirmationOrder(Date salesConfirmationOrderDate, Date estimatedDeliveryDate, String customerPurchaseOrderNumber, Long customerID, Long contactID, Long salesStaffID, Integer terms) {
        System.out.println("OrderManagementBean: createSalesConfirmationOrder() called");
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
            q = em.createQuery("SELECT c FROM Contact c WHERE c.id=:id");
            q.setParameter("id", contactID);
            Contact contact = (Contact) q.getSingleResult();
            if (customer.getIsDeleted() || contact.getIsDeleted()) {
                result.setDescription("Failed to create a new SCO. The selected customer or contact may have been deleted while the SCO is being created. Please try again.");
                return result;
            }
//            ReturnHelper uniqueResult = checkIfSCOnumberIsUnique(salesConfirmationOrderNumber);
//            if (!uniqueResult.getResult()) {
//                uniqueResult.setDescription("Failed to create a new SCO. The SCO number is already in use.");
//                return uniqueResult;
//            }
            SalesConfirmationOrder sco = new SalesConfirmationOrder(getNewSalesConfirmationOrderNumber(), salesConfirmationOrderDate, customerName, staff, terms, gstRate);
            sco.setEstimatedDeliveryDate(estimatedDeliveryDate);
            sco.setCustomerPurchaseOrderNumber(customerPurchaseOrderNumber);
            sco.setCustomer(customer);
            sco.setContactAddress(contact.getAddress());
            sco.setContactEmail(contact.getEmail());
            sco.setContactOfficeNo(contact.getOfficeNo());
            sco.setContactFaxNo(contact.getFaxNo());
            sco.setContactMobileNo(contact.getMobileNo());
            sco.setContactName(contact.getName());
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
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: createSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to create a SCO. The customer or staff selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: createSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to create a new SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrder(Long salesConfirmationOrderID, Date newSalesConfirmationOrderDate, Date newEstimatedDeliveryDate, String customerPurchaseOrderNumber, Long newCustomerID, String status, Integer newTerms, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
//            ReturnHelper uniqueResult = checkIfSCOnumberIsUnique(newSalesConfirmationOrderNumber);
//            if (!uniqueResult.getResult() && !newSalesConfirmationOrderNumber.equals(sco.getSalesConfirmationOrderNumber())) {
//                uniqueResult.setDescription("Failed to save the SCO as the SCO number is already in use.");
//                return uniqueResult;
//            }
            ReturnHelper updateStatusResult = updateSalesConfirmationOrderStatus(salesConfirmationOrderID, status);
            if (updateStatusResult.getResult() == false) {
                result.setDescription(updateStatusResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id");
            q.setParameter("id", newCustomerID);
            Customer newCustomer = (Customer) q.getSingleResult();
            String newCustomerName = newCustomer.getCustomerName();
            if (newCustomer.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO. The selected customer may have been deleted while the SCO is being updated. Please try again.");
                return result;
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
            //Update fields 
            sco.setCustomerName(newCustomerName);
            sco.setCustomer(newCustomer);
//            sco.setSalesConfirmationOrderNumber(newSalesConfirmationOrderNumber);
            sco.setSalesConfirmationOrderDate(newSalesConfirmationOrderDate);
            sco.setEstimatedDeliveryDate(newEstimatedDeliveryDate);
            sco.setCustomerPurchaseOrderNumber(customerPurchaseOrderNumber);
            sco.setTerms(newTerms);
            em.merge(sco);
            result.setID(sco.getId());
            result.setResult(true);
            result.setDescription("SCO saved successfully.");
            return result;
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The customer or staff selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrderCustomerContactDetails(Long salesConfirmationOrderID, String customerName, String contactName, String email, String officeNo, String mobileNo, String faxNo, String address, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrderContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            sco.setCustomerName(customerName);
            sco.setContactAddress(address);
            sco.setContactEmail(email);
            sco.setContactOfficeNo(officeNo);
            sco.setContactFaxNo(faxNo);
            sco.setContactMobileNo(mobileNo);
            sco.setContactName(contactName);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrderCustomerContactDetails(Long salesConfirmationOrderID, Long customerID, Long contactID, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrderContactDetails() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT c FROM Contact c WHERE c.id=:id");
            q.setParameter("id", contactID);
            Contact contact = (Contact) q.getSingleResult();
            q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id");
            q.setParameter("id", customerID);
            Customer newCustomer = (Customer) q.getSingleResult();
            String newCustomerName = newCustomer.getCustomerName();
            if (newCustomer.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO. The selected customer may have been deleted while the SCO is being updated. Please try again.");
                return result;
            }
            // Update customer link if it's different
            if (newCustomer.getId() != sco.getCustomer().getId()) {
                //Remove away the old links
                Customer oldCustomer = sco.getCustomer();
                List<SalesConfirmationOrder> oldCustomerSCOs = oldCustomer.getSCOs();
                oldCustomerSCOs.remove(sco);
                em.merge(oldCustomer);
                //Add links to other
                List<SalesConfirmationOrder> customerSCOs = newCustomer.getSCOs();
                customerSCOs.add(sco);
                newCustomer.setSCOs(customerSCOs);
                em.merge(newCustomer);
                //Update fields
                sco.setCustomerName(newCustomerName);
                sco.setCustomer(newCustomer);
            }
            sco.setContactAddress(contact.getAddress());
            sco.setContactEmail(contact.getEmail());
            sco.setContactOfficeNo(contact.getOfficeNo());
            sco.setContactFaxNo(contact.getFaxNo());
            sco.setContactMobileNo(contact.getMobileNo());
            sco.setContactName(contact.getName());
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO or customer or contact selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrder() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrderRemarks(Long salesConfirmationOrderID, String remarks, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrderRemarks() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            sco.setRemarks(remarks);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrderRemarks() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrderRemarks() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrderNotes(Long salesConfirmationOrderID, String notes, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrderNotes() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            sco.setNotes(notes);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrderNotes() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrderNotes() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a SCO due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper updateSalesConfirmationOrderStatus(Long salesConfirmationOrderID, String status) {
        System.out.println("OrderManagementBean: updateSalesConfirmationOrderStatus() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            switch (status) {
                case "Unfulfilled":
                    sco.setStatusAsUnfulfilled();
                    break;
                case "Fulfilled":
                    sco.setStatusAsFulfilled();
                    break;
                case "Completed":
                    //TODO: Check if all payment is received first before allow marking as completed
                    sco.setStatusAsCompleted();
                    break;
                case "Write-Off":
                    sco.setStatusAsWritenOff();
                    break;
                default:
                    result.setDescription("Failed to update the SCO to the status specified.");
                    System.out.println("OrderManagementBean: updateSalesConfirmationOrderStatus() received an unknown status.");
                    break;
            }
            em.merge(sco);
            result.setResult(true);
            result.setDescription("SCO edited successfully.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrderStatus() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: updateSalesConfirmationOrderStatus() failed");
            ex.printStackTrace();
            result.setDescription("Failed to edit a SCO due to internal server error.");
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper deleteSalesConfirmationOrder(Long salesConfirmationOrderID, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: deleteSalesConfirmationOrder() called");
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
            sco.setIsDeleted(true);
            em.merge(sco);
            List<PurchaseOrder> pos = sco.getPurchaseOrders();
            for (PurchaseOrder purchaseOrder : pos) {
                pombl.deletePurchaseOrder(purchaseOrder.getId(), adminOverwrite);
            }
            List<DeliveryOrder> dos = sco.getDeliveryOrders();
            for (DeliveryOrder deliveryOrder : dos) {
                dombl.deleteDeliveryOrder(deliveryOrder.getId(), adminOverwrite);
            }
            List<Invoice> is = sco.getInvoices();
            for (Invoice invoice : is) {
                imbl.deleteInvoice(invoice.getId(), adminOverwrite);
            }
            result.setResult(true);
            result.setDescription("SCO deleted successfully.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: deleteSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper voidSalesConfirmationOrder(Long salesConfirmationOrderID, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: deleteSalesConfirmationOrder() called");
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
            sco.setStatusAsVoided();
            em.merge(sco);
            List<PurchaseOrder> pos = sco.getPurchaseOrders();
            for (PurchaseOrder purchaseOrder : pos) {
                pombl.deletePurchaseOrder(purchaseOrder.getId(), adminOverwrite);
            }
            List<DeliveryOrder> dos = sco.getDeliveryOrders();
            for (DeliveryOrder deliveryOrder : dos) {
                dombl.voidDeliveryOrder(deliveryOrder.getId(), adminOverwrite);
            }
            List<Invoice> is = sco.getInvoices();
            for (Invoice invoice : is) {
                imbl.voidInvoice(invoice.getId(), adminOverwrite);
            }
            result.setResult(true);
            result.setDescription("SCO voided.");
        } catch (Exception ex) {
            context.setRollbackOnly();
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
                //Prevent editing of SCO if any invoice has been sent
                //List<Invoice> invoices = sco.getInvoices();
                //for (Invoice invoice : invoices) {
                //    if (invoice.getStatus().equals("Sent")) {
                //        result.setDescription("SCO can not be edited/deleted as the first invoice has already been issued.");
                //        return result;
                //    }
                //}
            }
            if (sco.getIsDeleted()) {
                result.setDescription("SCO can not be edited/deleted as it has already been deleted.");
                return result;
            }
            if (sco.getStatus().equals("Voided")) {
                result.setDescription("SCO can not be edited/deleted as it has already been voided.");
                return result;
            }
            result.setResult(true);
            result.setDescription("Editable SCO.");
        } catch (NoResultException ex) {
            System.out.println("OrderManagementBean: checkIfSCOisEditable() can not find SCO");
            result.setDescription("Unable to complete request, SCO not found.");
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: checkIfSCOisEditable() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper checkIfSCOnumberIsUnique(String salesConfirmationOrderNumber) {
        System.out.println("OrderManagementBean: checkIfSCOnumberIsUnique() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.salesConfirmationOrderNumber=:number AND s.isDeleted=false");
            q.setParameter("number", salesConfirmationOrderNumber);
            List<SalesConfirmationOrder> scos = q.getResultList();
            if (scos.size() == 0) {
                result.setResult(true);
                result.setDescription("SCO number is unique");
                return result;
            } else {
                result.setDescription("SCO number is already in use.");
                return result;
            }
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: checkIfSCOnumberIsUnique() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public SalesConfirmationOrder getSalesConfirmationOrder(Long salesConfirmationOrderID) {
        System.out.println("OrderManagementBean: getSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder salesConfirmationOrder = (SalesConfirmationOrder) q.getSingleResult();
            return salesConfirmationOrder;
        } catch (EntityNotFoundException ex) {
            System.out.println("OrderManagementBean: getSalesConfirmationOrder(): order not found");
            return null;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: getSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SalesConfirmationOrderHelper> listAllSalesConfirmationOrder(Long staffID) {
        System.out.println("OrderManagementBean: listAllSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
            q.setParameter("staffID", staffID);
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsAdmin()) {
                //List all for admin
                q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false");
            } else {
                //List only those that they create for normal staff
                q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false and s.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<SalesConfirmationOrder> salesConfirmationOrders = q.getResultList();
            List<SalesConfirmationOrderHelper> salesConfirmationOrderHelpers = new ArrayList();
            for (SalesConfirmationOrder salesConfirmationOrder : salesConfirmationOrders) {
                SalesConfirmationOrderHelper salesConfirmationOrderHelper = new SalesConfirmationOrderHelper();
                salesConfirmationOrderHelper.setSco(salesConfirmationOrder);
                salesConfirmationOrderHelper.setDeliveryOrders(dombl.listDeliveryOrdersTiedToSCO(salesConfirmationOrder.getId()));
                salesConfirmationOrderHelper.setInvoices(imbl.listInvoicesTiedToSCO(salesConfirmationOrder.getId()));
                salesConfirmationOrderHelper.setPurchaseOrders(pombl.listPurchaseOrdersTiedToSCO(salesConfirmationOrder.getId()));
                salesConfirmationOrderHelpers.add(salesConfirmationOrderHelper);
            }
            return salesConfirmationOrderHelpers;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: listAllSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SalesConfirmationOrderHelper> listCustomerSalesConfirmationOrder(Long customerID) {
        System.out.println("OrderManagementBean: listCustomerSalesConfirmationOrder() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false AND s.customerLink.id=:id");
            q.setParameter("id", customerID);
            List<SalesConfirmationOrder> salesConfirmationOrders = q.getResultList();
            List<SalesConfirmationOrderHelper> salesConfirmationOrderHelpers = new ArrayList();
            for (SalesConfirmationOrder salesConfirmationOrder : salesConfirmationOrders) {
                SalesConfirmationOrderHelper salesConfirmationOrderHelper = new SalesConfirmationOrderHelper();
                salesConfirmationOrderHelper.setSco(salesConfirmationOrder);
                salesConfirmationOrderHelper.setDeliveryOrders(dombl.listDeliveryOrdersTiedToSCO(salesConfirmationOrder.getId()));
                salesConfirmationOrderHelper.setInvoices(imbl.listInvoicesTiedToSCO(salesConfirmationOrder.getId()));
                salesConfirmationOrderHelper.setPurchaseOrders(pombl.listPurchaseOrdersTiedToSCO(salesConfirmationOrder.getId()));
                salesConfirmationOrderHelpers.add(salesConfirmationOrderHelper);
            }
            return salesConfirmationOrderHelpers;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: listAllSalesConfirmationOrder() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper addSCOlineItem(Long salesConfirmationOrderID, String itemName, String itemDescription, Integer itemQty, Double itemUnitPrice, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: addSCOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
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
            List<LineItem> lineItems = sco.getItems();
            lineItems.add(lineItem);
            sco.setItems(lineItems);
            //Update SCO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            sco.setTotalPrice(totalPrice);
            sco.setTotalTax(totalTax);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Item added.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: addSCOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit a SCO. The SCO selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: addSCOlineItem() failed");
            result.setDescription("Unable to add line item, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper updateSCOlineItem(Long salesConfirmationOrderID, Long lineItemID, String newItemName, String newItemDescription, Integer newItemQty, Double newItemUnitPrice, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: updateSCOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
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
            List<LineItem> lineItems = sco.getItems();
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            sco.setTotalPrice(totalPrice);
            sco.setTotalTax(totalTax);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Line item updated.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: updateSCOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the SCO. The SCO or item selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: updateSCOlineItem() failed");
            result.setDescription("Unable to update line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper deleteSCOlineItem(Long salesConfirmationOrderID, Long lineItemID, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: deleteSCOlineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to delete the item as the SCO has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            q = em.createQuery("SELECT l FROM LineItem l WHERE l.id=:id");
            q.setParameter("id", lineItemID);
            LineItem lineItem = (LineItem) q.getSingleResult();
            List<LineItem> lineItems = sco.getItems();
            lineItems.remove(lineItem);
            sco.setItems(lineItems);
            //Update SCO total price & tax
            Double totalPrice = 0.0;
            Double totalTax = 0.0;
            for (LineItem curLineItem : lineItems) {
                Double currLineItemTotalPriceBeforeTax = curLineItem.getItemUnitPrice() * curLineItem.getItemQty();
                totalPrice = totalPrice + (currLineItemTotalPriceBeforeTax * ((gstRate / 100) + 1));
                totalTax = totalTax + (currLineItemTotalPriceBeforeTax * gstRate / 100);
            }
            sco.setTotalPrice(totalPrice);
            sco.setTotalTax(totalTax);
            em.merge(sco);
            em.remove(lineItem);
            result.setResult(true);
            result.setDescription("Item deleted.");
        } catch (NoResultException ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: deleteSCOlineItem() could not find one or more ID(s).");
            result.setDescription("Failed to edit the SCO. The SCO or item selected no longer exist in the system.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: deleteSCOlineItem() failed");
            result.setDescription("Unable to delete line item, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public ReturnHelper deleteSCOallLineItem(Long salesConfirmationOrderID, Boolean adminOverwrite) {
        System.out.println("OrderManagementBean: deleteSCOallLineItem() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder sco = (SalesConfirmationOrder) q.getSingleResult();
            if (sco.getIsDeleted()) {
                result.setDescription("Failed to edit the SCO as it has been deleted.");
                return result;
            }
            ReturnHelper checkResult = checkIfSCOisEditable(salesConfirmationOrderID, adminOverwrite);
            if (!checkResult.getResult()) {
                result.setDescription(checkResult.getDescription());
                return result;
            }
            List<LineItem> lineItems = sco.getItems();
            for (LineItem lineItem : lineItems) {
                lineItems.remove(lineItem);
                em.remove(lineItem);
            }
            sco.setItems(lineItems);
            sco.setTotalPrice(0.0);
            sco.setTotalTax(0.0);
            em.merge(sco);
            result.setResult(true);
            result.setDescription("Line items deleted.");
        } catch (Exception ex) {
            context.setRollbackOnly();
            System.out.println("OrderManagementBean: deleteSCOallLineItem() failed");
            result.setDescription("Unable to delete line items, internal server error.");
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public List<LineItem> listSCOlineItems(Long salesConfirmationOrderID) {
        System.out.println("OrderManagementBean: listSCOlineItems() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Query q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.id=:id");
            q.setParameter("id", salesConfirmationOrderID);
            SalesConfirmationOrder salesConfirmationOrder = (SalesConfirmationOrder) q.getSingleResult();
            List<LineItem> lineItems = salesConfirmationOrder.getItems();
            return lineItems;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: listSCOlineItems() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper refreshSCOs(Long staffID) {
        System.out.println("OrderManagementBean: refreshSCOs() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        try {
            Staff staff = new Staff();
            Query q;
            if (staffID != null) {
                q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:staffID");
                q.setParameter("staffID", staffID);
                staff = (Staff) q.getSingleResult();
            }
            if (staffID == null || staff.getIsAdmin()) {
                //Refresh all for admin
                q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false");
            } else {
                //Refresh only those that they create for normal staff
                q = em.createQuery("SELECT s FROM SalesConfirmationOrder s WHERE s.isDeleted=false and s.salesPerson.id=:staffID");
                q.setParameter("staffID", staffID);
            }
            List<SalesConfirmationOrder> salesConfirmationOrders = q.getResultList();
            Boolean someFailed = false;
            for (SalesConfirmationOrder sco : salesConfirmationOrders) {
                Double invoicedAmount = imbl.getSCOtotalInvoicedAmount(sco.getId());
                if (invoicedAmount == null) {
                    someFailed = true;
                }
                sco.setTotalInvoicedAmount(invoicedAmount);
                sco.setNumOfInvoices(imbl.listInvoicesTiedToSCO(sco.getId()).size());
                sco.setNumOfDeliveryOrders(dombl.listDeliveryOrdersTiedToSCO(sco.getId()).size());
                sco.setNumOfPurchaseOrders(pombl.listPurchaseOrdersTiedToSCO(sco.getId()).size());
                em.merge(sco);
            }
            if (someFailed) {
                result.setDescription("Some orders could not be refreshed");
            } else {
                result.setDescription("Orders refreshed");
                result.setResult(true);
            }
            return result;
        } catch (Exception ex) {
            System.out.println("OrderManagementBean: refreshSCOs() failed");
            result.setDescription("Internal server error.");
            ex.printStackTrace();
            return null;
        }
    }
}
