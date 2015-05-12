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
            ambl.registerStaffAccount("Admin", "--", "admin", "admin", true);
            ambl.registerStaffAccount("Test Staff", "--", "test", "test", false);
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
