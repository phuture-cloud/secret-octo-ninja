package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.util.List;
import javax.ejb.Local;

@Local
public interface AccountManagementBeanLocal {
    public ReturnHelper loginStaff(String username, String password);
    public Staff getStaff(String username);
    
    public ReturnHelper disableAccount(String username);
    public ReturnHelper enableAccount(String username);
    
    public ReturnHelper registerStaffAccount(String name, String staffPrefix, String username, String password, boolean isAdmin);  
    public boolean checkIfUsernameExists(String username);
    public String generatePasswordHash(String salt, String password);
    public String generatePasswordSalt();
    
    public ReturnHelper updateStaffName(Long staffID, String newName);
    public ReturnHelper updateStaffPassword(Long staffID, String oldPassword, String newPassword);
    public List<Staff> listAllStaffAccount();
}
