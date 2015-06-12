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
public class SalesConfirmationOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String salesConfirmationOrderNumber;
    //Automatic
    @Temporal(TemporalType.DATE)
    private Date dateCreated;
    //User specified
    @Temporal(TemporalType.DATE)
    private Date salesConfirmationOrderDate;
    
    private String customerName;
    private String contactName;
    private String contactEmail;
    private String contactOfficeNo;
    private String contactMobileNo;
    private String contactFaxNo;
    @Lob
    private String contactAddress;
    
    @ManyToOne
    private Customer customerLink;
    @ManyToOne
    private Staff salesPerson;
    private String status;
    private Integer terms;
    @OneToMany
    private List<DeliveryOrder> deliveryOrders;
    @OneToMany
    private List<PurchaseOrder> purchaseOrders;
    @OneToMany
    private List<Invoice> invoices;
    @OneToMany
    private List<LineItem> items;
    private Double taxRate;//in %
    private Double totalTax;//total totalTax amount
    private Double totalPrice;//after gst
    @Lob
    private String remarks;//Will appear on order
    @Lob
    private String notes;//For internal staff use on the CRM system only
    private boolean isDeleted;
    

    public SalesConfirmationOrder() {
        this.status = "Unfulfilled";
        this.deliveryOrders = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.invoices = new ArrayList();
        this.dateCreated = new Date();
        this.isDeleted = false;
        this.remarks = "";
        this.notes = "";
        this.totalPrice = 0.0;
        this.totalTax = 0.0;
    }

    public SalesConfirmationOrder(String salesConfirmationOrderNumber, Date salesConfirmationOrderDate, String customerName, Staff salesPerson, Integer term, Double taxRate, String remarks, String notes) {
        this.status = "Unfulfilled";
        this.salesConfirmationOrderNumber = salesConfirmationOrderNumber;
        this.salesConfirmationOrderDate = salesConfirmationOrderDate;
        this.customerName = customerName;
        this.salesPerson = salesPerson;
        this.deliveryOrders = new ArrayList();
        this.purchaseOrders = new ArrayList();
        this.invoices = new ArrayList();
        this.dateCreated = new Date();
        this.terms = term;
        this.taxRate = taxRate;
        this.isDeleted = false;
        this.remarks = remarks;
        this.notes = notes;
        this.totalPrice = 0.0;
        this.totalTax = 0.0;
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

    public void setStatusAsUnfulfilled() {
        this.status = "Unfulfilled";
    }

    public void setStatusAsFulfilled() {
        this.status = "Fulfilled";
    }

    public void setStatusAsCompleted() {
        this.status = "Completed";
    }
    public void setStatusAsWritenOff() {
        this.status = "Write-Off";
    }

    public String getSalesConfirmationOrderNumber() {
        return salesConfirmationOrderNumber;
    }

    public void setSalesConfirmationOrderNumber(String salesConfirmationOrderNumber) {
        this.salesConfirmationOrderNumber = salesConfirmationOrderNumber;
    }

    public Customer getCustomer() {
        return customerLink;
    }

    public void setCustomer(Customer customer) {
        this.customerLink = customer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Integer getTerms() {
        return terms;
    }

    public void setTerms(Integer terms) {
        this.terms = terms;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getSalesConfirmationOrderDate() {
        return salesConfirmationOrderDate;
    }

    public void setSalesConfirmationOrderDate(Date salesConfirmationOrderDate) {
        this.salesConfirmationOrderDate = salesConfirmationOrderDate;
    }

    public Customer getCustomerLink() {
        return customerLink;
    }

    public void setCustomerLink(Customer customerLink) {
        this.customerLink = customerLink;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Double totalTax) {
        this.totalTax = totalTax;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
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
