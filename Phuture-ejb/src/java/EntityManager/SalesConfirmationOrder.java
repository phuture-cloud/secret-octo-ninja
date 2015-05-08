package EntityManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class SalesConfirmationOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Customer customer;
    @OneToOne
    private Contact shippingContact;
    @OneToOne
    private Contact billingContact;
    @ManyToOne
    private Staff salesPerson;
    @OneToMany
    private List<DeliveryOrder> deliveryOrders;
    @OneToMany
    private List<PurchaseOrder> purchaseOrders;
    @OneToMany
    private List<Invoice> invoices;
    private String status;
    private String salesConfirmationOrderNumber;
    @OneToMany
    private List<LineItem> items;

    public SalesConfirmationOrder() {
        this.status = "Created";
        this.deliveryOrders = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.invoices = new ArrayList();
    }

    public SalesConfirmationOrder(String salesConfirmationOrderNumber, Customer customer, Staff salesPerson, Contact shippingContact, Contact billingContact) {
        this.status = "Created";
        this.salesConfirmationOrderNumber = salesConfirmationOrderNumber;
        this.customer = customer;
        this.salesPerson = salesPerson;
        this.shippingContact = shippingContact;
        this.billingContact = billingContact;
        this.deliveryOrders = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.invoices = new ArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Contact getShippingContact() {
        return shippingContact;
    }

    public void setShippingContact(Contact shippingContact) {
        this.shippingContact = shippingContact;
    }

    public Contact getBillingContact() {
        return billingContact;
    }

    public void setBillingContact(Contact billingContact) {
        this.billingContact = billingContact;
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
