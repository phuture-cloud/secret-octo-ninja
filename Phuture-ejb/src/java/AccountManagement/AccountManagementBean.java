package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateful
public class AccountManagementBean implements AccountManagementBeanLocal {

    Staff loggedInStaff;

    public AccountManagementBean() {
        System.out.println("AccountManagementBean (EJB) created.");
        loggedInStaff = null;
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public ReturnHelper registerStaffAccount(String name, String staffPrefix, String username, String password, boolean isAdmin) {
        System.out.println("AccountManagementBean: registerStaffAccount() called");
        ReturnHelper result = new ReturnHelper();
        try {
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

    private boolean checkIfUsernameExists() {
        System.out.println("AccountManagementBean: checkIfUsernameExists() called");
        ReturnHelper result = new ReturnHelper();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                System.out.println("loginStaff(): Staff with username:" + username + " logged in successfully.");
                loggedInStaff = staff;
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
    }
    
    @Override
    public Staff checkCurrentUser() {
        return loggedInStaff;
    }
}
