package EntityManager;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
public class CreditNote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private Timestamp version;
    @ManyToOne
    private Customer customer;
    private String creditNoteNumber;
    private Double creditAmount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateIssued;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUsed;
    @ManyToOne
    private Invoice appliedToInvoice;
    private boolean isDeleted;
    private boolean isVoided;

    private String contactName;
    private String contactEmail;
    private String contactOfficeNo;
    private String contactMobileNo;
    private String contactFaxNo;
    @Lob
    private String contactAddress;

    public CreditNote() {
    }

    public CreditNote(String creditNoteNumber, Double creditAmount, Date dateIssued) {
        this.creditNoteNumber = creditNoteNumber;
        this.creditAmount = creditAmount;
        this.dateIssued = dateIssued;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCreditNoteNumber() {
        return creditNoteNumber;
    }

    public void setCreditNoteNumber(String creditNoteNumber) {
        this.creditNoteNumber = creditNoteNumber;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsVoided() {
        return isVoided;
    }

    public void setIsVoided(boolean isVoided) {
        this.isVoided = isVoided;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Date getDateUsed() {
        return dateUsed;
    }

    public void setDateUsed(Date dateUsed) {
        this.dateUsed = dateUsed;
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

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Invoice getAppliedToInvoice() {
        return appliedToInvoice;
    }

    public void setAppliedToInvoice(Invoice appliedToInvoice) {
        this.appliedToInvoice = appliedToInvoice;
        this.dateUsed = new Date();
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
        if (!(object instanceof CreditNote)) {
            return false;
        }
        CreditNote other = (CreditNote) object;
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
