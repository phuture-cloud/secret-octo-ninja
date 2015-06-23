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
public class DeliveryOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String deliveryOrderNumber;
    @ManyToOne
    private SalesConfirmationOrder salesConfirmationOrder;
    @OneToMany
    private List<LineItem> items;
    private Double taxRate;//in %
    private Double totalTax;//total totalTax amount
    private Double totalPrice;//after gst
    private String status;
    
    @Lob
    private String remarks;//Will appear on order
    @Lob
    private String notes;//For internal staff use on the CRM system only
    
    //Automatic
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMarkedAsDelivered;
    //User specified
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryOrderDate;

    private String customerPurchaseOrderNumber;
    private String customerName;
    //Shipping contact
    private String contactName;
    private String contactEmail;
    private String contactOfficeNo;
    private String contactMobileNo;
    private String contactFaxNo;
    @Lob
    private String contactAddress;
    private boolean isDeleted;
    

    public DeliveryOrder() {
        this.dateCreated = new Date();
        this.isDeleted = false;
        this.remarks = "";
        this.notes = "";
        this.totalPrice = 0.0;
        this.totalTax = 0.0;
        setStatusAsCreated();
    }

    public DeliveryOrder(String deliveryOrderNumber) {
        this.dateCreated = new Date();
        this.deliveryOrderNumber = deliveryOrderNumber;
        this.isDeleted = false;
        this.remarks = "";
        this.notes = "";
        this.totalPrice = 0.0;
        this.totalTax = 0.0;
        setStatusAsCreated();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryOrderNumber() {
        return deliveryOrderNumber;
    }

    public void setDeliveryOrderNumber(String deliveryOrderNumber) {
        this.deliveryOrderNumber = deliveryOrderNumber;
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

    public void setStatusAsShipped() {
        this.status = "Shipped";
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateMarkedAsDelivered() {
        return dateMarkedAsDelivered;
    }

    public void setDateMarkedAsDelivered(Date dateMarkedAsDelivered) {
        this.dateMarkedAsDelivered = dateMarkedAsDelivered;
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

    public void setContactOfficeNo(String ontactOfficeNo) {
        this.contactOfficeNo = ontactOfficeNo;
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

    public Date getDeliveryOrderDate() {
        return deliveryOrderDate;
    }

    public void setDeliveryOrderDate(Date deliveryOrderDate) {
        this.deliveryOrderDate = deliveryOrderDate;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPurchaseOrderNumber() {
        return customerPurchaseOrderNumber;
    }

    public void setCustomerPurchaseOrderNumber(String customerPurchaseOrderNumber) {
        this.customerPurchaseOrderNumber = customerPurchaseOrderNumber;
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
        if (!(object instanceof DeliveryOrder)) {
            return false;
        }
        DeliveryOrder other = (DeliveryOrder) object;
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
