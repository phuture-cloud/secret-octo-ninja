package EntityManager;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private Timestamp version;
    @ManyToOne
    private SalesConfirmationOrder salesConfirmationOrder;
    private String purchaseOrderNumber;
    @Temporal(TemporalType.DATE)
    private Date dateCreated;
    @Temporal(TemporalType.DATE)
    private Date purchaseOrderDate;

    @ManyToOne
    private Supplier supplierLink;
    private String supplierName;
    private String contactName;
    private String contactEmail;
    private String contactOfficeNo;
    private String contactMobileNo;
    private String contactFaxNo;
    @Lob
    private String contactAddress;

    private String terms;
    @Temporal(TemporalType.DATE)
    private Date deliveryDate;

    private String status;
    @OneToMany
    private List<LineItem> items;
    private String currency;
    private Double totalPrice;
    @Lob
    private String notes;
    @Lob
    private String remarks;
    private boolean isDeleted;

    public PurchaseOrder() {
        this.status = "Pending";
        this.dateCreated = new Date();
        this.notes = "";
        this.items = new ArrayList<>();
        this.totalPrice = 0.0;
        this.isDeleted = false;
        this.supplierName = "";
        this.contactName = "";
        this.contactEmail = "";
        this.contactOfficeNo = "";
        this.contactMobileNo = "";
        this.contactFaxNo = "";
        this.contactAddress = "";
        this.terms = "";
        this.remarks = "";
        this.currency = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SalesConfirmationOrder getSalesConfirmationOrder() {
        return salesConfirmationOrder;
    }

    public void setSalesConfirmationOrder(SalesConfirmationOrder salesConfirmationOrder) {
        this.salesConfirmationOrder = salesConfirmationOrder;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    public void setPurchaseOrderDate(Date purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactOfficeNo() {
        return contactOfficeNo;
    }

    public void setContactOfficeNo(String contactOfficeNo) {
        this.contactOfficeNo = contactOfficeNo;
    }

    public String getContactMobileNo() {
        return contactMobileNo;
    }

    public void setContactMobileNo(String contactMobileNo) {
        this.contactMobileNo = contactMobileNo;
    }

    public String getContactFaxNo() {
        return contactFaxNo;
    }

    public void setContactFaxNo(String contactFaxNo) {
        this.contactFaxNo = contactFaxNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Supplier getSupplierLink() {
        return supplierLink;
    }

    public void setSupplierLink(Supplier supplierLink) {
        this.supplierLink = supplierLink;
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
        if (!(object instanceof PurchaseOrder)) {
            return false;
        }
        PurchaseOrder other = (PurchaseOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntityManager.DeliveryOrderEntity[ id=" + id + " ]";
    }

}
