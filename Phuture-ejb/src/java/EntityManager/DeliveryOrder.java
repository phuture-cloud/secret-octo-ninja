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
    private Double deliveryOrderNumber;
    @ManyToOne
    private SalesConfirmationOrder salesConfirmationOrder;
    @OneToMany
    private List<LineItem> items;
    private Double tax;//gst
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

    private String shippingContactName;
    private String shippingContactEmail;
    private String shippingContactOfficeNo;
    private String shippingContactMobileNo;
    private String shippingContactFaxNo;
    @Lob
    private String shippingContactAddress;
    private boolean isDeleted;
    

    public DeliveryOrder() {
        this.dateCreated = new Date();
        setStatusAsCreated();
    }

    public DeliveryOrder(Double deliveryOrderNumber) {
        this.dateCreated = new Date();
        this.deliveryOrderNumber = deliveryOrderNumber;
        setStatusAsCreated();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDeliveryOrderNumber() {
        return deliveryOrderNumber;
    }

    public void setDeliveryOrderNumber(Double deliveryOrderNumber) {
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

    public String getShippingContactName() {
        return shippingContactName;
    }

    public void setShippingContactName(String shippingContactName) {
        this.shippingContactName = shippingContactName;
    }

    public String getShippingContactEmail() {
        return shippingContactEmail;
    }

    public void setShippingContactEmail(String shippingContactEmail) {
        this.shippingContactEmail = shippingContactEmail;
    }

    public String getShippingContactOfficeNo() {
        return shippingContactOfficeNo;
    }

    public void setShippingContactOfficeNo(String shippingContactOfficeNo) {
        this.shippingContactOfficeNo = shippingContactOfficeNo;
    }

    public String getShippingContactMobileNo() {
        return shippingContactMobileNo;
    }

    public void setShippingContactMobileNo(String shippingContactMobileNo) {
        this.shippingContactMobileNo = shippingContactMobileNo;
    }

    public String getShippingContactFaxNo() {
        return shippingContactFaxNo;
    }

    public void setShippingContactFaxNo(String shippingContactFaxNo) {
        this.shippingContactFaxNo = shippingContactFaxNo;
    }

    public String getShippingContactAddress() {
        return shippingContactAddress;
    }

    public void setShippingContactAddress(String shippingContactAddress) {
        this.shippingContactAddress = shippingContactAddress;
    }

    public Date getDeliveryOrderDate() {
        return deliveryOrderDate;
    }

    public void setDeliveryOrderDate(Date deliveryOrderDate) {
        this.deliveryOrderDate = deliveryOrderDate;
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
