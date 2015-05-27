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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class SalesConfirmationOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String salesConfirmationOrderNumber;
    @Temporal(TemporalType.DATE)
    private Date dateCreated;
    
    private String customerName;
    private String shippingContactName;
    private String shippingContactEmail;
    private String shippingContactOfficeNo;
    private String shippingContactMobileNo;
    private String shippingContactFaxNo;
    @Lob
    private String shippingContactAddress;
    
    private String billingContactName;
    private String billingContactEmail;
    private String billingContactOfficeNo;
    private String billingContactMobileNo;
    private String billingContactFaxNo;
    @Lob
    private String billingContactAddress;
    
    @ManyToOne
    private Customer customerLink;
    @ManyToOne
    private Staff salesPerson;
    @OneToMany
    private List<DeliveryOrder> deliveryOrders;
    @OneToMany
    private List<PurchaseOrder> purchaseOrders;
    @OneToMany
    private List<Invoice> invoices;
    private String status;
    @OneToMany
    private List<LineItem> items;

    public SalesConfirmationOrder() {
        this.status = "Created";
        this.deliveryOrders = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.invoices = new ArrayList();
        this.dateCreated = new Date();
    }

    public SalesConfirmationOrder(String salesConfirmationOrderNumber, Customer customer, Staff salesPerson, Contact shippingContact, Contact billingContact) {
        this.status = "Created";
        this.salesConfirmationOrderNumber = salesConfirmationOrderNumber;
        this.customerLink = customer;
        this.salesPerson = salesPerson;
        this.deliveryOrders = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.invoices = new ArrayList();
        this.dateCreated = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusAsFulfilled() {
        this.status = "Fulfilled";
    }

    public void setStatusAsCompleted() {
        this.status = "Completed";
    }

    public String getSalesConfirmationOrderNumber() {
        return salesConfirmationOrderNumber;
    }

    public void setSalesConfirmationOrderNumber(String salesConfirmationOrderNumber) {
        this.salesConfirmationOrderNumber = salesConfirmationOrderNumber;
    }

    public Customer getCustomerLink() {
        return customerLink;
    }

    public void setCustomerLink(Customer customerLink) {
        this.customerLink = customerLink;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public List<DeliveryOrder> getDeliveryOrders() {
        return deliveryOrders;
    }

    public void setDeliveryOrders(List<DeliveryOrder> deliveryOrders) {
        this.deliveryOrders = deliveryOrders;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public Staff getSalesPerson() {
        return salesPerson;
    }

    public void setSalesPerson(Staff salesPerson) {
        this.salesPerson = salesPerson;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
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
        if (!(object instanceof SalesConfirmationOrder)) {
            return false;
        }
        SalesConfirmationOrder other = (SalesConfirmationOrder) object;
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
