package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.util.List;
import javax.ejb.Local;
import javax.servlet.http.Part;

@Local
public interface AccountManagementBeanLocal {
    public ReturnHelper loginStaff(String username, String password);
    public Staff getStaff(String username);
    
    public ReturnHelper disableAccount(Long accountID);
    public ReturnHelper enableAccount(Long accountID);
    
    public ReturnHelper registerStaffAccount(String name, String staffPrefix, String username, String password, boolean isAdmin);  
    public boolean checkIfUsernameExists(String username);
    public String generatePasswordHash(String salt, String password);
    public String generatePasswordSalt();
    
    public ReturnHelper updateStaff(Long staffID, String newName, String newStaffPrefix);
    public ReturnHelper updateStaffPassword(Long staffID, String oldPassword, String newPassword);
    public ReturnHelper updateStaffPassword(Long staffID, String newPassword);
    public ReturnHelper updateStaffSignature(Long staffID, Part signature);
    public ReturnHelper removeStaffSignature(Long staffID);
    public List<Staff> listAllStaffAccount();
}
