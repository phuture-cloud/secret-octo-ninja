package EntityManager;

import java.io.Serializable;
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

@Entity
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long invoiceNumber;
    @ManyToOne
    private SalesConfirmationOrder salesConfirmationOrder;
    @OneToMany
    private List<LineItem> items;
    private Double tax;//gst
    private Double totalPrice;//after gst
    @Lob
    private String remarks;//Will appear on order
    @Lob
    private String notes;//For internal staff use on the CRM system only  

    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSent;
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePaid;

    private String billingContactName;
    private String billingContactEmail;
    private String billingContactOfficeNo;
    private String billingContactMobileNo;
    private String billingContactFaxNo;
    @Lob
    private String billingContactAddress;

    private boolean isDeleted;

    public Invoice() {
        setStatusAsCreated();
    }

    public Invoice(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        setStatusAsCreated();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public SalesConfirmationOrder getSalesConfirmationOrder() {
        return salesConfirmationOrder;
    }

    public void setSalesConfirmationOrder(SalesConfirmationOrder salesConfirmationOrder) {
        this.salesConfirmationOrder = salesConfirmationOrder;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusAsCreated() {
        this.status = "Created";
    }

    public void setStatusAsSent() {
        this.status = "Sent";
    }

    public void setStatusAsPaid() {
        this.status = "Paid";
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Date datePaid) {
        this.datePaid = datePaid;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBillingContactName() {
        return billingContactName;
    }

    public void setBillingContactName(String billingContactName) {
        this.billingContactName = billingContactName;
    }

    public String getBillingContactEmail() {
        return billingContactEmail;
    }

    public void setBillingContactEmail(String billingContactEmail) {
        this.billingContactEmail = billingContactEmail;
    }

    public String getBillingContactOfficeNo() {
        return billingContactOfficeNo;
    }

    public void setBillingContactOfficeNo(String billingContactOfficeNo) {
        this.billingContactOfficeNo = billingContactOfficeNo;
    }

    public String getBillingContactMobileNo() {
        return billingContactMobileNo;
    }

    public void setBillingContactMobileNo(String billingContactMobileNo) {
        this.billingContactMobileNo = billingContactMobileNo;
    }

    public String getBillingContactFaxNo() {
        return billingContactFaxNo;
    }

    public void setBillingContactFaxNo(String billingContactFaxNo) {
        this.billingContactFaxNo = billingContactFaxNo;
    }

    public String getBillingContactAddress() {
        return billingContactAddress;
    }

    public void setBillingContactAddress(String billingContactAddress) {
        this.billingContactAddress = billingContactAddress;
    }

    public boolean isIsDeleted() {
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
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice) object;
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
