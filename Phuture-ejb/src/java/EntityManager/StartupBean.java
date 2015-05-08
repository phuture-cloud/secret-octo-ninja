package EntityManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;

@Singleton
@Startup
public class StartupBean {

    @EJB
    private AccountManagement.AccountManagementBeanLocal ambl;

    private EntityManager em;

    @PostConstruct
    private void startup() {
        ambl.registerStaffAccount("Admin", "--", "admin", "admin", true);
    }

    @PreDestroy
    private void shutdown() {
        System.out.println("Application is shutting down.");
    }
}
