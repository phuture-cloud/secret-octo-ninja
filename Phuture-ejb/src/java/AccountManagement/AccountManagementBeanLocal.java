package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.util.List;
import javax.ejb.Local;

@Local
public interface AccountManagementBeanLocal {
    public ReturnHelper loginStaff(String username, String password);
    public void logoutStaff();
    public ReturnHelper registerStaffAccount(String name, String staffPrefix, String username, String password, boolean isAdmin);
    //public List<Staff> listAllStaffAccount();
    
    public String generatePasswordHash(String salt, String password);
    public String generatePasswordSalt();
    
    public Staff checkCurrentUser();
}
