package CustomerManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.ReturnHelper;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CustomerManagementBeanLocal {
    public ReturnHelper addCustomer(String customerName);
    public ReturnHelper deleteCustomer(Long customerID);
    public ReturnHelper updateCustomer(Long customerID, String newCustomerName);
    public List<Customer> listCustomers();
    
    public ReturnHelper addContact(Long customerID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, String notes);
    public ReturnHelper deleteContact(Long contactID);
    public ReturnHelper updateContact(Long contactID, String newName, String newEmail, String newOfficeNo, String newMobileNo, String newFaxNo, String newAddress, String newNotes);
    public List<Contact> listCustomerContacts(Long customerID);
    public ReturnHelper setPrimaryContact(Long customerID, Long contactID);
    
    //public ReturnHelper addCreditNote(Long customerID, Double creditAmount);
    //public ReturnHelper updateCreditNote(Long creditNoteID, Double creditAmount);
}
