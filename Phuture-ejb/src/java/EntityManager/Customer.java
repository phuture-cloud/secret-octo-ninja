package EntityManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerName;
    @OneToOne
    private Contact primaryContact;
    @OneToMany
    private List<Contact> companyContacts;
    @OneToMany
    private List<SalesConfirmationOrder> SCOs;
    @OneToMany
    private List<PaymentRecord> PaymentRecords;
    @OneToOne
    private StatementOfAccount statementOfAccount;
    @OneToMany
    private List<CreditNote> creditNotes;
    private Double availableCredits;
    private boolean isDeleted;

    public Customer() {
        this.companyContacts = new ArrayList();
        this.SCOs = new ArrayList();
        this.PaymentRecords = new ArrayList();
        this.creditNotes = new ArrayList();
        this.isDeleted = false;
    }

    public Customer(String customerName) {
        this.customerName = customerName;
        this.companyContacts = new ArrayList();
        this.SCOs = new ArrayList();
        this.PaymentRecords = new ArrayList();
        this.creditNotes = new ArrayList();
        this.isDeleted = false;
    }

    public StatementOfAccount getStatementOfAccount() {
        return statementOfAccount;
    }

    public void setStatementOfAccount(StatementOfAccount statementOfAccount) {
        this.statementOfAccount = statementOfAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Contact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(Contact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public List<Contact> getCustomerContacts() {
        return companyContacts;
    }

    public void setCompanyContacts(List<Contact> companyContacts) {
        this.companyContacts = companyContacts;
    }

    public List<SalesConfirmationOrder> getSCOs() {
        return SCOs;
    }

    public void setSCOs(List<SalesConfirmationOrder> SCOs) {
        this.SCOs = SCOs;
    }

    public List<CreditNote> getCreditNotes() {
        return creditNotes;
    }

    public void setCreditNotes(List<CreditNote> creditNotes) {
        this.creditNotes = creditNotes;
    }

    public List<PaymentRecord> getPaymentRecords() {
        return PaymentRecords;
    }

    public void setPaymentRecords(List<PaymentRecord> PaymentRecords) {
        this.PaymentRecords = PaymentRecords;
    }

//    public StatementOfAccount getStatementOfAccount() {
//        return statementOfAccount;
//    }
//
//    public void setStatementOfAccount(StatementOfAccount statementOfAccount) {
//        this.statementOfAccount = statementOfAccount;
//    }

    public boolean getIsDeleted() {
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
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntityManager.CustomerEntity[ id=" + id + " ]";
    }

}
