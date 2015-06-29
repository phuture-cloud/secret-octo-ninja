package EntityManager;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class StatementOfAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //@OneToOne(mappedBy = "statementOfAccount")
    private Customer customer;
    private Double totalAmountInvoiced;
    private Double totalAmountPaid;
    
    @OneToMany
    private List<SOALineItem> lineItem;

    @OneToMany
    private List<Invoice> overDueInvoices;
    private Double amountOverDueFrom0to30Days;
    private Double amountOverDueFrom31to60Days;
    private Double amountOverDueFrom61to90Days;
    private Double amountOverDueOver91Days;
    private Double totalAmountOverDue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getTotalAmountInvoiced() {
        return totalAmountInvoiced;
    }

    public void setTotalAmountInvoiced(Double totalAmountInvoiced) {
        this.totalAmountInvoiced = totalAmountInvoiced;
    }

    public Double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(Double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public List<SOALineItem> getLineItem() {
        return lineItem;
    }

    public void setLineItem(List<SOALineItem> lineItem) {
        this.lineItem = lineItem;
    }

    public List<Invoice> getOverDueInvoices() {
        return overDueInvoices;
    }

    public void setOverDueInvoices(List<Invoice> overDueInvoices) {
        this.overDueInvoices = overDueInvoices;
    }

    public Double getAmountOverDueFrom0to30Days() {
        return amountOverDueFrom0to30Days;
    }

    public void setAmountOverDueFrom0to30Days(Double amountOverDueFrom0to30Days) {
        this.amountOverDueFrom0to30Days = amountOverDueFrom0to30Days;
    }

    public Double getAmountOverDueFrom31to60Days() {
        return amountOverDueFrom31to60Days;
    }

    public void setAmountOverDueFrom31to60Days(Double amountOverDueFrom31to60Days) {
        this.amountOverDueFrom31to60Days = amountOverDueFrom31to60Days;
    }

    public Double getAmountOverDueFrom61to90Days() {
        return amountOverDueFrom61to90Days;
    }

    public void setAmountOverDueFrom61to90Days(Double amountOverDueFrom61to90Days) {
        this.amountOverDueFrom61to90Days = amountOverDueFrom61to90Days;
    }

    public Double getAmountOverDueOver91Days() {
        return amountOverDueOver91Days;
    }

    public void setAmountOverDueOver91Days(Double amountOverDueOver91Days) {
        this.amountOverDueOver91Days = amountOverDueOver91Days;
    }

    public Double getTotalAmountOverDue() {
        return totalAmountOverDue;
    }

    public void setTotalAmountOverDue(Double totalAmountOverDue) {
        this.totalAmountOverDue = totalAmountOverDue;
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
        if (!(object instanceof Contact)) {
            return false;
        }
        StatementOfAccount other = (StatementOfAccount) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntityManager.ContactEntity[ id=" + id + " ]";
    }

}
