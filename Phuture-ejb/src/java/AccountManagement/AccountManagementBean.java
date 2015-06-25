package AccountManagement;

import EntityManager.ReturnHelper;
import EntityManager.Staff;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.Part;

@Stateless
public class AccountManagementBean implements AccountManagementBeanLocal {

    public AccountManagementBean() {
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
                if (staff.getIsDisabled()) {
                    result.setResult(false);
                    result.setDescription("Unable to login, account is disabled.");
                    return result;
                }
                System.out.println("loginStaff(): Staff with username:" + username + " logged in successfully.");
                em.detach(staff);
                staff.setPasswordHash(null);
                staff.setPasswordSalt(null);
                result.setResult(true);
                result.setDescription("Login successful.");
                return result;
            } else {
                System.out.println("loginStaff(): Login credentials provided were incorrect, password wrong.");
                result.setResult(false);
                result.setDescription("Login credentials provided incorrect.");
                return result;
            }
        } catch (NoResultException ex) {//cannot find staff with that email
            System.out.println("loginStaff(): Login credentials provided were incorrect, no such username found.");
            result.setResult(false);
            result.setDescription("Login credentials provided incorrect.");
            return result;
        } catch (Exception ex) {
            System.out.println("loginStaff(): Internal error");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Unable to login, internal server error.");
            return result;
        }
    }

    @Override
    public Staff getStaff(String username) {
        System.out.println("AccountManagementBean: getStaff() called");
        try {
            Query q = em.createQuery("SELECT s FROM Staff s where s.username=:username");
            q.setParameter("username", username);
            Staff staff = (Staff) q.getSingleResult();
            return staff;
        } catch (Exception ex) {
            System.out.println("getStaff(): Internal error");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public ReturnHelper enableAccount(Long accountID) {
        System.out.println("AccountManagementBean: enableAccount() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT s FROM Staff s where s.id=:id");
        q.setParameter("id", accountID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsDisabled() == false) {
                result.setResult(true);
                result.setDescription("Account enabled.");
            } else {
                staff.setIsDisabled(false);
                em.merge(staff);
                result.setResult(true);
                result.setDescription("Account enabled.");
            }
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: enableAccount() failed");
            result.setResult(false);
            result.setDescription("Failed to enable account. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper disableAccount(Long accountID) {
        System.out.println("AccountManagementBean: disableAccount() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT s FROM Staff s where s.id=:id");
        q.setParameter("id", accountID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsDisabled() == true) {
                result.setResult(true);
                result.setDescription("Account disabled.");
            } else {
                staff.setIsDisabled(true);
                em.merge(staff);
                result.setResult(true);
                result.setDescription("Account disabled.");
            }
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: enableAccount() failed");
            result.setResult(false);
            result.setDescription("Failed to disable account. Internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper registerStaffAccount(String name, String staffPrefix, String username, String password, boolean isAdmin) {
        System.out.println("AccountManagementBean: registerStaffAccount() called");
        ReturnHelper result = new ReturnHelper();
        try {
            if (checkIfUsernameExists(username)) {
                result.setResult(false);
                result.setDescription("Unable to register, username already in use.");
                return result;
            }
            String passwordSalt = generatePasswordSalt();
            String passwordHash = generatePasswordHash(passwordSalt, password);
            Staff staff = new Staff(name, staffPrefix, username, passwordSalt, passwordHash, isAdmin);
            em.persist(staff);
            result.setResult(true);
            result.setDescription("Account registered successfully.");
            return result;
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: registerStaffAccount() failed");
            ex.printStackTrace();
            result.setResult(false);
            result.setDescription("Failed to register account due to internal server error.");
            return result;
        }
    }

    @Override
    public boolean checkIfUsernameExists(String username) {
        System.out.println("AccountManagementBean: checkIfUsernameExists() called");
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.username=:username");
        q.setParameter("username", username);
        try {
            Staff staff = (Staff) q.getSingleResult();
        } catch (NoResultException ex) {
            return false;
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: checkIfUsernameExists() failed");
            ex.printStackTrace();
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

    @Override
    public ReturnHelper updateStaff(Long staffID, String newName, String newStaffPrefix) {
        System.out.println("AccountManagementBean: checkIfUsernameExists() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
        q.setParameter("id", staffID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsDisabled() == true) {
                result.setDescription("Account is disabled.");
                return result;
            }
            if (newName != null) {
                staff.setName(newName);
            }
            if (newStaffPrefix != null) {
                staff.setStaffPrefix(newStaffPrefix);
            }
            em.merge(staff);
            result.setResult(true);
            result.setDescription("Staff name updated successfully.");
        } catch (NoResultException ex) {
            result.setDescription("Unable to find staff with the provided ID.");
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: updateStaffName() failed");
            result.setDescription("Unable to update staff's name, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateStaffPassword(Long staffID, String oldPassword, String newPassword) {
        System.out.println("AccountManagementBean: updateStaffPassword() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
        q.setParameter("id", staffID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            if (!generatePasswordHash(staff.getPasswordSalt(), oldPassword).equals(staff.getPasswordHash())) {
                result.setDescription("Old password provided is invalid, password not updated.");
            } else {
                staff.setPasswordSalt(generatePasswordSalt());
                staff.setPasswordHash(generatePasswordHash(staff.getPasswordSalt(), newPassword));
                em.merge(staff);
                result.setResult(true);
                result.setDescription("Password updated successfully.");
            }
        } catch (NoResultException ex) {
            result.setDescription("Unable to find staff with the provided ID.");
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: updateStaffPassword() failed");
            result.setDescription("Unable to update staff's password, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateStaffPassword(Long staffID, String newPassword) {
        System.out.println("AccountManagementBean: updateStaffPassword() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
        q.setParameter("id", staffID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            staff.setPasswordSalt(generatePasswordSalt());
            staff.setPasswordHash(generatePasswordHash(staff.getPasswordSalt(), newPassword));
            em.merge(staff);
            result.setResult(true);
            result.setDescription("Password updated successfully.");
        } catch (NoResultException ex) {
            result.setDescription("Unable to find staff with the provided ID.");
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: updateStaffPassword() failed");
            result.setDescription("Unable to update staff's password, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper updateStaffSignature(Long staffID, Part signature) {
        System.out.println("AccountManagementBean: updateStaffSignature() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
        q.setParameter("id", staffID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsDisabled() == true) {
                result.setDescription("Account is disabled.");
                return result;
            }
            if (signature == null) {
                result.setDescription("No file selected.");
                return result;
            }

            //Write the signature file to server filesystem first
            String fileName = UUID.randomUUID().toString();
            InputStream fileInputStream = signature.getInputStream();
            OutputStream fileOutputStream = new FileOutputStream(fileName);
            int nextByte;
            while ((nextByte = fileInputStream.read()) != -1) {
                fileOutputStream.write(nextByte);
            }
            fileOutputStream.close();
            fileInputStream.close();

            //Open the file
            File signatureFile = new File(fileName);
            byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());
            //Write to database
            staff.setSignature(signatureBytes);
            em.merge(staff);

            //Delete the file on the filesystem
            signatureFile.delete();

            result.setResult(true);
            result.setDescription("Signature updated.");
        } catch (NoResultException ex) {
            result.setDescription("Unable to find staff with the provided ID.");
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: updateStaffSignature() failed");
            result.setDescription("Unable to update signature, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public ReturnHelper removeStaffSignature(Long staffID) {
        System.out.println("AccountManagementBean: removeStaffSignature() called");
        ReturnHelper result = new ReturnHelper();
        result.setResult(false);
        Query q = em.createQuery("SELECT s FROM Staff s WHERE s.id=:id");
        q.setParameter("id", staffID);
        try {
            Staff staff = (Staff) q.getSingleResult();
            if (staff.getIsDisabled() == true) {
                result.setDescription("Account is disabled.");
                return result;
            }
            if (staff.getSignature() == null || staff.getSignature().length == 0) {
                result.setDescription("Signature removed.");
                result.setResult(true);
                return result;
            }
            staff.setSignature(null);
            em.merge(staff);
            result.setResult(true);
            result.setDescription("Signature removed.");
        } catch (NoResultException ex) {
            result.setDescription("Unable to find staff with the provided ID.");
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: removeStaffSignature() failed");
            result.setDescription("Unable to remove signature, internal server error.");
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Staff> listAllStaffAccount() {
        System.out.println("AccountManagementBean: listAllStaffAccount() called");
        ReturnHelper result = new ReturnHelper();
        Query q = em.createQuery("SELECT s FROM Staff s");
        try {
            List<Staff> staffs = q.getResultList();
            return staffs;
        } catch (Exception ex) {
            System.out.println("AccountManagementBean: listAllStaffAccount() failed");
            ex.printStackTrace();
            return null;
        }
    }
}
