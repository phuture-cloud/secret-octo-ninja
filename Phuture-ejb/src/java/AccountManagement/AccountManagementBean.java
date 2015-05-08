package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import static EntityManager.Staff_.username;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateful
public class AccountManagementBean implements AccountManagementBeanLocal {

    private Boolean isStaffLoggedIn = false;
    private Staff loggedInStaff = null;

    public AccountManagementBean() {
        System.out.println("AccountManagementBean (EJB) created.");
        isStaffLoggedIn = false;
        loggedInStaff = null;
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public ReturnHelper loginStaff(String username, String password) {
        System.out.println("AccountManagementBean: loginStaff() called");
        ReturnHelper result = new ReturnHelper();
        try {
            Query q = em.createQuery("SELECT s FROM Staff s where s.username=:username");
            q.setParameter("username", username);
            Staff staff = (Staff) q.getSingleResult();
            String passwordSalt = staff.getPasswordSalt();
            String passwordHash = generatePasswordHash(passwordSalt, password);
            if (passwordHash.equals(staff.getPasswordHash())) {
                if (staff.isIsDisabled()) {
                    result.setResult(false);
                    result.setResultDescription("Account disabled.");
                    return result;
                }
                System.out.println("loginStaff(): Staff with username:" + username + " logged in successfully.");
                em.detach(staff);
                isStaffLoggedIn = true;
                loggedInStaff = staff;
                loggedInStaff.setPasswordHash(null);
                loggedInStaff.setPasswordSalt(null);
                result.setResult(true);
                result.setResultDescription("Login successful.");
                return result;
            } else {
                System.out.println("loginStaff(): Login credentials provided were incorrect, password wrong.");
                result.setResult(false);
                result.setResultDescription("Login credentials provided incorrect.");
                return result;
            }
        } catch (NoResultException ex) {//cannot find staff with that email
            System.out.println("loginStaff(): Login credentials provided were incorrect, no such username found.");
            result.setResult(false);
            result.setResultDescription("Login credentials provided incorrect.");
            return result;
        } catch (Exception ex) {
            System.out.println("loginStaff(): Internal error");
            ex.printStackTrace();
            result.setResult(false);
            result.setResultDescription("Unable to login, internal server error.");
            return result;
        }
    }

    @Override
    public void logoutStaff() {
        loggedInStaff = null;
        isStaffLoggedIn = false;
    }

    @Override
    public Staff getCurrentUser() {
        return loggedInStaff;
    }
    @Override
    public Boolean checkCurrentUser() {
        return isStaffLoggedIn;
    }

    @Override
    public ReturnHelper registerStaffAccount(String name, String staffPrefix, String username, String password, boolean isAdmin) {
        System.out.println("AccountManagementBean: registerStaffAccount() called");
        ReturnHelper result = new ReturnHelper();
        try {
            if (checkIfUsernameExists(username)) {
                result.setResult(false);
                result.setResultDescription("Unable to register, username already in use.");
                return result;
            }
            String passwordSalt = generatePasswordSalt();
            String passwordHash = generatePasswordHash(passwordSalt, password);
            Staff staff = new Staff(name, staffPrefix, username, passwordSalt, passwordHash, isAdmin);
            em.persist(staff);
            result.setResult(true);
            result.setResultDescription("Account registered successfully.");
            return result;
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: registerStaffAccount() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setResultDescription("Failed to register account due to internal server error.");
            return result;
        }
    }

    @Override
    public boolean checkIfUsernameExists(String username) {
        System.out.println("AccountManagementBean: checkIfUsernameExists() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.username=:username");
        q.setParameter("username", username);
        try {
            Staff staff = (Staff) q.getSingleResult();
        } catch (NoResultException ex) {
            return false;
        } catch (Exception ex) {
        }
        return true;
    }

    @Override
    public String generatePasswordHash(String salt, String password) {
        String passwordHash = null;
        try {
            password = salt + password;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            passwordHash = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("AccountManagementBean: generatePasswordHash() failed");
            ex.printStackTrace();
        }
        return passwordHash;
    }

    @Override
    public String generatePasswordSalt() {
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("AccountManagementBean: generatePasswordSalt() failed");
            ex.printStackTrace();
        }
        return Arrays.toString(salt);
    }

//    @Override
//    public List<Staff> listAllStaffAccount() {
//        System.out.println("AccountManagementBean: listAllStaffAccount() called");
//        ReturnHelper result = new ReturnHelper();
//        Query q = em.createQuery("SELECT s FROM Staff s");
//        q.setParameter("username", username);
//        try {
//            Staff staff = (Staff) q.getSingleResult();
//        } catch (NoResultException ex) {
//            return false;
//        } catch (Exception ex) {
//        }
//        return true;
//    }

}