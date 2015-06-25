package EntityManager;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Singleton
@Startup
public class StartupBean {

    @EJB
    private AccountManagement.AccountManagementBeanLocal ambl;
    @EJB
    private CustomerManagement.CustomerManagementBeanLocal cmbl;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    private void startup() {
        try {
            Query q = em.createQuery("SELECT s FROM Staff s where s.username=:username");
            q.setParameter("username", "admin");
            List<Staff> staff = q.getResultList();
            // Don't insert anything if database appears to be initiated.
            if (staff != null && staff.size() > 0) {
                System.out.println("Skipping init of database, already initated.");
                return;
            }
            ambl.registerStaffAccount("Admin", "--", null, "admin", "admin", true);
            ambl.registerStaffAccount("Test Staff", "--", null, "test", "test", false);
            ReturnHelper result;
            result = cmbl.addCustomer("Test Customer 1");
            cmbl.addContact(result.getID(), "Alpha", "alpha@test.com", "61111111", "61111111", "61111111", "Block 111 Alpha Avenue 1 #01-01 Singapore 111111", "Customer notes can be writen here");
            cmbl.addContact(result.getID(), "Bravo", "bravo@test.com", "62222222", "62222222", "62222222", "Block 222 Bravo Road 2 #02-02 Singapore 222222", "Customer notes can be writen here");
            result = cmbl.addCustomer("Test Customer 2");
            cmbl.addContact(result.getID(), "Charlie", "charlie@test.com", "63333333", "63333333", "63333333", "Block 333 Charlie Road 3 #03-03 Singapore 333333", "Customer notes can be writen here");
        } catch (Exception ex) {
            System.out.println("Error initating database");
            ex.printStackTrace();
        }
    }

    @PreDestroy
    private void shutdown() {
        System.out.println("Application is shutting down.");
    }
}
