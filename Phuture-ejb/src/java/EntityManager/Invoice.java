package EntityManager;

import java.io.Serializable;
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

@Entity
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String invoiceNumber;
    @ManyToOne
    private SalesConfirmationOrder salesConfirmationOrder;
    @OneToMany
    private List<PaymentRecord> paymentRecords;
    private Integer numOfPaymentRecords;
    private Double totalAmountPaid;
    @OneToMany
    private List<LineItem> items;
    private Double taxRate;//in %
    private Double totalTax;//total totalTax amount
    private Double totalPrice;//after gst
    @Lob
    private String remarks;//Will appear on order
    @Lob
    private String notes;//For internal staff use on the CRM system only  
    private Integer terms;
    private String status;
    //Automatic
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Temporal(TemporalType.DATE)
    private Date dateDue;
    //Manual
    @Temporal(TemporalType.DATE)
    private Date dateSent;
    @Temporal(TemporalType.DATE)
    private Date datePaid;
    private String estimatedDeliveryDate;

    private String customerPurchaseOrderNumber;
    private String customerName;
    //Billing contact
    private String contactName;
    private String contactEmail;
    private String contactOfficeNo;
    private String contactMobileNo;
    private String contactFaxNo;
    @Lob
    private String contactAddress;
    private boolean isDeleted;

    public Invoice() {
        this.items = new ArrayList();
        this.paymentRecords = new ArrayList();
        this.dateCreated = new Date();
        this.isDeleted = false;
        this.remarks = "";
        this.notes = "";
        this.totalPrice = 0.0;
        this.totalTax = 0.0;
        setStatusAsCreated();
        this.numOfPaymentRecords=0;
        this.totalAmountPaid=0.0;
    }

    public Invoice(String invoiceNumber) {
        this.items = new ArrayList();
        this.paymentRecords = new ArrayList();
        this.invoiceNumber = invoiceNumber;
        this.dateCreated = new Date();
        this.isDeleted = false;
        this.remarks = "";
        this.notes = "";
        this.totalPrice = 0.0;
        this.totalTax = 0.0;
        setStatusAsCreated();
        this.numOfPaymentRecords=0;
        this.totalAmountPaid=0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumOfPaymentRecords() {
        return numOfPaymentRecords;
    }

    public void setNumOfPaymentRecords(Integer numOfPaymentRecords) {
        this.numOfPaymentRecords = numOfPaymentRecords;
    }
 
    public Date getDateDue() {
        return dateDue;
    }

    public void setDateDue(Date dateDue) {
        this.dateDue = dateDue;
    }

    public Integer getTerms() {
        return terms;
    }

    public void setTerms(Integer terms) {
        this.terms = terms;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
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
    
    public void setStatusAsVoided() {
        this.status = "Voided";
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

    public Double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
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

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getCustomerPurchaseOrderNumber() {
        return customerPurchaseOrderNumber;
    }

    public void setCustomerPurchaseOrderNumber(String customerPurchaseOrderNumber) {
        this.customerPurchaseOrderNumber = customerPurchaseOrderNumber;
    }

    public List<PaymentRecord> getPaymentRecords() {
        return paymentRecords;
    }

    public void setPaymentRecords(List<PaymentRecord> paymentRecords) {
        this.paymentRecords = paymentRecords;
    }

    public Double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(Double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
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
