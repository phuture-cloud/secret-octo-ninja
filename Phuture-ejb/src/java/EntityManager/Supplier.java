package EntityManager;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@Entity
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private Timestamp version;
    private String supplierName;
    @OneToOne
    private SupplierContact primaryContact;
    @OneToMany
    private List<SupplierContact> companyContacts;
    @OneToMany
    private List<PurchaseOrder> purchaseOrders;
    private Double totalAvailableCredits;
    private boolean isDeleted;

    public Supplier() {
        this.companyContacts = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.isDeleted = false;
        this.totalAvailableCredits = 0.0;
    }

    public Supplier(String supplierName) {
        this.supplierName = supplierName;
        this.companyContacts = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.isDeleted = false;
        this.totalAvailableCredits = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public SupplierContact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(SupplierContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public List<SupplierContact> getCompanyContacts() {
        return companyContacts;
    }

    public void setCompanyContacts(List<SupplierContact> companyContacts) {
        this.companyContacts = companyContacts;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public Double getTotalAvailableCredits() {
        return totalAvailableCredits;
    }

    public void setTotalAvailableCredits(Double totalAvailableCredits) {
        this.totalAvailableCredits = totalAvailableCredits;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Supplier)) {
            return false;
        }
        Supplier other = (Supplier) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntityManager.CustomerEntity[ id=" + id + " ]";
    }

}
