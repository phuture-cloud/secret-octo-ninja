package SupplierManagement;

import EntityManager.Supplier;
import EntityManager.ReturnHelper;
import EntityManager.SupplierContact;
import java.util.List;
import javax.ejb.Local;

@Local
public interface SupplierManagementBeanLocal {
    public ReturnHelper addSupplier(String supplierName);
    public ReturnHelper deleteSupplier(Long supplierID);
    public ReturnHelper updateSupplier(Long supplierID, String newSupplierName);
    public List<Supplier> listSuppliers();
    
    public ReturnHelper addContact(Long supplierID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, String notes);
    public ReturnHelper deleteContact(Long contactID);
    public ReturnHelper updateContact(Long contactID, String newName, String newEmail, String newOfficeNo, String newMobileNo, String newFaxNo, String newAddress, String newNotes);
    public List<SupplierContact> listSupplierContacts(Long supplierID);
    public ReturnHelper setPrimaryContact(Long supplierID, Long contactID);
    
    //public ReturnHelper addCreditNote(Long supplierID, Double creditAmount);
    //public ReturnHelper updateCreditNote(Long creditNoteID, Double creditAmount);
}
