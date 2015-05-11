package CustomerManagement;

import EntityManager.Customer;
import EntityManager.ReturnHelper;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
        } catch (Exception ex) {
            System.out.println("CustomerManagementBean: addCustomer() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to add customer due to internal server error.");
            return result;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteCustomer(Long customerID) {
        System.out.println("AccountManagementBean: deleteCustomer() called");
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
            System.out.println("AccountManagementBean: deleteCustomer() failed");
            result.setResult(false);
            result.setDescription("Failed to delete customer. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Customer> listAllCustomers() {
        System.out.println("AccountManagementBean: getCustomerList() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT c FROM Customer c where c.isDeleted = false");
        try {
            List<Customer> customers = q.getResultList();
            return customers;
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: getCustomerList() failed");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper addContact(Long customerID, String name, String email, String officeNo, String mobileNo, String faxNo, String address, String notes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReturnHelper deleteContact(Long customerID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
