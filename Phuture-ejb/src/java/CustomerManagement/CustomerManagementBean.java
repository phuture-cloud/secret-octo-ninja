package CustomerManagement;

import EntityManager.Contact;
import EntityManager.Customer;
import EntityManager.ReturnHelper;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CustomerManagementBean implements CustomerManagementBeanLocal {

    @PersistenceContext
    private EntityManager em;

    public CustomerManagementBean() {
    }

    @Override
    public ReturnHelper addCustomer(String customerName) {
        System.out.println("CustomerManagementBean: addCustomer() called");
        ReturnHelper result = new ReturnHelper();
        try {
            Customer customer = new Customer();
            customer.setCustomerName(customerName);
            em.persist(customer);
            result.setResult(true);
            result.setDescription("Customer added successfully.");
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: addCustomer() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to add customer due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteCustomer(Long customerID) {
        System.out.println("CustomerManagementBean: deleteCustomer() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Customer c where c.id=:id");
        q.setParameter("id", customerID);
        try {
            Customer customer = (Customer) q.getSingleResult();
            if (customer.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("Customer is already deleted.");
            } else {
                customer.setIsDeleted(true);
                em.merge(customer);
                result.setResult(true);
                result.setDescription("Customer deleted successfully.");
            }
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: deleteCustomer() failed");
            result.setResult(false);
            result.setDescription("Failed to delete customer. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Customer> listCustomers() {
        System.out.println("CustomerManagementBean: getCustomerList() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Customer c where c.isDeleted = false");
        try {
            List<Customer> customers = q.getResultList();
            return customers;
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: getCustomerList() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper addContact(Long customerID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, String notes) {
        System.out.println("CustomerManagementBean: addContact() called");
        ReturnHelper result = new ReturnHelper();
        try {
            Query q = em.createQuery("SELECT c FROM Customer c where c.id=:id");
            q.setParameter("id", customerID);
            Customer customer = (Customer) q.getSingleResult();
            Contact contact = new Contact(customer, name);
            contact.setEmail(email);
            contact.setOfficeNo(officeNo);
            contact.setMobileNo(mobileNo);
            contact.setFaxNo(faxNo);
            contact.setAddress(address);
            contact.setNotes(notes);
            em.persist(contact);
            List<Contact> companyContacts = customer.getCompanyContacts();
            companyContacts.add(contact);
            customer.setCompanyContacts(companyContacts);
            em.merge(customer);
            result.setResult(true);
            result.setDescription("Contact added successfully.");
        } catch (NoResultException ex) {
            System.out.println("CustomerManagementBean: addContact() failed");
            result.setResult(false);
            result.setDescription("Failed to add contact the specified customer does not exist. It may have been deleted.");
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: addContact() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to add contact due to internal server error.");
        }
        return result;
    }

    @Override
    public ReturnHelper deleteContact(Long contactID) {
        System.out.println("CustomerManagementBean: deleteContact() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Contact c where c.id=:id");
        q.setParameter("id", contactID);
        try {
            Contact contact = (Contact) q.getSingleResult();
            if (contact.getIsPrimaryContact()) {
                result.setResult(false);
                result.setDescription("Contact is the primary contact for this customer and cannot be deleted. Set another contact as primary before deleting this contact.");
            } else if (contact.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("Contact is already deleted.");
            } else {
                contact.setIsDeleted(true);
                em.merge(contact);
                Customer customer = contact.getCustomer();
                List<Contact> companyContacts = customer.getCompanyContacts();
                companyContacts.remove(contact);
                customer.setCompanyContacts(companyContacts);
                em.merge(customer);
                result.setResult(true);
                result.setDescription("Contact deleted successfully.");
            }
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: deleteContact() failed");
            result.setResult(false);
            result.setDescription("Failed to delete contact. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Contact> listCustomerContacts(Long customerID) {
        System.out.println("CustomerManagementBean: listCustomerContacts() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Contact c where c.customer.id=:id");
        try {
            List<Contact> customerContacts = q.getResultList();
            return customerContacts;
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: listCustomerContacts() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper setPrimaryContact(Long customerID, Long contactID) {
        System.out.println("CustomerManagementBean: setPrimaryContact() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Contact c where c.id=:id");
        q.setParameter("id", contactID);
        try {
            Contact contact = (Contact) q.getSingleResult();
            if (contact.getIsPrimaryContact()) {
                result.setResult(false);
                result.setDescription("Contact is already the primary contact for this customer.");
            } else if (contact.getIsDeleted() == true) {
                result.setResult(false);
                result.setDescription("Unable to set contact as primary as it has been deleted.");
            } else {
                q = em.createQuery("SELECT c FROM Customer c where c.id=:id");
                Customer customer = (Customer) q.getSingleResult();
                if (customer.getIsDeleted() == true) {
                    result.setResult(false);
                    result.setDescription("Unable to set contact as primary as the customer has been deleted.");
                } else {
                    customer.setPrimaryContact(contact);
                    em.merge(customer);
                    contact.setIsPrimaryContact(true);
                    em.merge(contact);
                    result.setResult(true);
                    result.setDescription("Contact as primary.");
                }
            }
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: setPrimaryContact() failed");
            result.setResult(false);
            result.setDescription("Failed to set primary contact. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

}
