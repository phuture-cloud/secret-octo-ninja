package SupplierManagement;

import EntityManager.SupplierContact;
import EntityManager.PurchaseOrder;
import EntityManager.Supplier;
import EntityManager.ReturnHelper;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class SupplierManagementBean implements SupplierManagementBeanLocal {

    @PersistenceContext
    private EntityManager em;

    public SupplierManagementBean() {
    }

    @Override
    public ReturnHelper addSupplier(String supplierName) {
        System.out.println("SupplierManagementBean: addSupplier() called");
        ReturnHelper result = new ReturnHelper();
        try {
            Supplier supplier = new Supplier();
            supplier.setSupplierName(supplierName);
            em.persist(supplier);
            result.setResult(true);
            result.setDescription("Supplier added successfully.");
            em.flush();
            result.setID(supplier.getId());
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: addSupplier() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to add supplier due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteSupplier(Long supplierID) {
        System.out.println("SupplierManagementBean: deleteSupplier() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Supplier c where c.id=:id");
        q.setParameter("id", supplierID);
        try {
            Supplier supplier = (Supplier) q.getSingleResult();
            if (supplier.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("Supplier is already deleted.");
            } else {
                //Allow delete only if all the SCO has been deleted
                List<PurchaseOrder> pos = supplier.getPurchaseOrders();
                for (PurchaseOrder po : pos) {
                    if (!po.getIsDeleted()) {
                        result.setDescription("Supplier has exisiting purchase orders and cannot be deleted.");
                        return result;
                    }
                }
                supplier.setIsDeleted(true);
                //Loop all it's contact and mark them as deleted also
                List<SupplierContact> contacts = supplier.getCompanyContacts();
                for (SupplierContact contact : contacts) {
                    contact.setIsDeleted(true);
                    em.merge(contact);
                }
                em.merge(supplier);
                result.setResult(true);
                result.setDescription("Supplier deleted successfully.");
            }
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: deleteSupplier() failed");
            result.setResult(false);
            result.setDescription("Failed to delete supplier. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateSupplier(Long supplierID, String newSupplierName) {
        System.out.println("SupplierManagementBean: updateSupplier() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Supplier c where c.id=:id");
        q.setParameter("id", supplierID);
        try {
            Supplier supplier = (Supplier) q.getSingleResult();
            if (supplier.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("Supplier cannot be updated, it is deleted.");
            } else {
                supplier.setSupplierName(newSupplierName);
                em.merge(supplier);
                result.setResult(true);
                result.setDescription("Supplier updated successfully.");
            }
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: updateSupplier() failed");
            result.setResult(false);
            result.setDescription("Failed to update supplier. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Supplier> listSuppliers() {
        System.out.println("SupplierManagementBean: getSupplierList() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Supplier c where c.isDeleted = false");
        try {
            List<Supplier> suppliers = q.getResultList();
            return suppliers;
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: getSupplierList() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper addContact(Long supplierID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, String notes) {
        System.out.println("SupplierManagementBean: addContact() called");
        ReturnHelper result = new ReturnHelper();
        try {
            Query q = em.createQuery("SELECT c FROM Supplier c where c.id=:id");
            q.setParameter("id", supplierID);
            Supplier supplier = (Supplier) q.getSingleResult();
            SupplierContact contact = new SupplierContact(supplier, name);
            contact.setEmail(email);
            contact.setOfficeNo(officeNo);
            contact.setMobileNo(mobileNo);
            contact.setFaxNo(faxNo);
            contact.setAddress(address);
            contact.setNotes(notes);
            em.persist(contact);
            List<SupplierContact> companyContacts = supplier.getCompanyContacts();
            if (companyContacts.size() == 0) {
                setPrimaryContact(supplierID, contact.getId());
            }
            companyContacts.add(contact);
            supplier.setCompanyContacts(companyContacts);
            em.flush();
            result.setID(contact.getId());
            em.merge(supplier);
            result.setResult(true);
            result.setDescription("SupplierContact added successfully.");
        } catch (NoResultException ex) {
            System.out.println("SupplierManagementBean: addContact() failed");
            result.setResult(false);
            result.setDescription("Failed to add contact the specified supplier does not exist. It may have been deleted.");
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: addContact() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to add contact due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteContact(Long contactID) {
        System.out.println("SupplierManagementBean: deleteContact() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM SupplierContact c where c.id=:id");
        q.setParameter("id", contactID);
        try {
            SupplierContact contact = (SupplierContact) q.getSingleResult();
            if (contact.getIsPrimaryContact()) {
                result.setResult(false);
                result.setDescription("SupplierContact is the primary contact for this supplier and cannot be deleted. Set another contact as primary before deleting this contact.");
            } else if (contact.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("SupplierContact is already deleted.");
            } else {
                contact.setIsDeleted(true);
                em.merge(contact);
                Supplier supplier = contact.getSupplier();
                List<SupplierContact> companyContacts = supplier.getCompanyContacts();
                companyContacts.remove(contact);
                supplier.setCompanyContacts(companyContacts);
                em.merge(supplier);
                result.setResult(true);
                result.setDescription("SupplierContact deleted successfully.");
            }
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: deleteContact() failed");
            result.setResult(false);
            result.setDescription("Failed to delete contact. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateContact(Long contactID, String newName, String newEmail, String newOfficeNo, String newMobileNo, String newFaxNo, String newAddress, String newNotes) {
        System.out.println("SupplierManagementBean: updateContact() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM SupplierContact c where c.id=:id");
        q.setParameter("id", contactID);
        try {
            SupplierContact contact = (SupplierContact) q.getSingleResult();
            if (contact.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("SupplierContact is deleted and cannot be updated.");
            } else {
                contact.setName(newName);
                contact.setEmail(newEmail);
                contact.setOfficeNo(newOfficeNo);
                contact.setMobileNo(newMobileNo);
                contact.setFaxNo(newFaxNo);
                contact.setAddress(newAddress);
                contact.setNotes(newNotes);
                em.merge(contact);
                result.setResult(true);
                result.setDescription("SupplierContact updated successfully.");
            }
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: updateContact() failed");
            result.setResult(false);
            result.setDescription("Failed to update contact. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<SupplierContact> listSupplierContacts(Long supplierID) {
        System.out.println("SupplierManagementBean: listSupplierContacts() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM SupplierContact c WHERE c.supplier.id=:id and c.isDeleted=false ORDER BY c.isPrimaryContact DESC");
        q.setParameter("id", supplierID);
        try {
            List<SupplierContact> supplierContacts = q.getResultList();
            return supplierContacts;
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: listSupplierContacts() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper setPrimaryContact(Long supplierID, Long contactID) {
        System.out.println("SupplierManagementBean: setPrimaryContact() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM SupplierContact c where c.id=:id");
        q.setParameter("id", contactID);
        try {
            SupplierContact contact = (SupplierContact) q.getSingleResult();
            if (contact.getIsPrimaryContact()) {
                result.setResult(false);
                result.setDescription("SupplierContact is already the primary contact for this supplier.");
            } else if (contact.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("Unable to set contact as primary as it has been deleted.");
            } else {
                q = em.createQuery("SELECT c FROM Supplier c where c.id=:id");
                q.setParameter("id", supplierID);
                Supplier supplier = (Supplier) q.getSingleResult();
                if (supplier.getIsDeleted() == true) {
                    result.setResult(false);
                    result.setDescription("Unable to set contact as primary as the supplier has been deleted.");
                } else {
                    //Clear away the current primary contact first
                    SupplierContact currentPrimaryContact = supplier.getPrimaryContact();
                    if (currentPrimaryContact != null) {
                        currentPrimaryContact.setIsPrimaryContact(false);
                        em.merge(currentPrimaryContact);
                    }
                    //Update primary contact
                    supplier.setPrimaryContact(contact);
                    em.merge(supplier);
                    contact.setIsPrimaryContact(true);
                    em.merge(contact);
                    result.setResult(true);
                    if (supplier.getCompanyContacts().size() == 1) {
                        result.setDescription("Supplier and primary contact added successfully.");
                    } else {
                        result.setDescription("SupplierContact marked as primary contact.");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("SupplierManagementBean: setPrimaryContact() failed");
            result.setResult(false);
            result.setDescription("Failed to set primary contact. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

}
