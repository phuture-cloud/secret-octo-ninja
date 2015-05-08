package EntityManager;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class CreditNote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long creditNoteNumber;
    private Double creditAmount;
    @OneToOne
    private Invoice appliedToInvoice;

    public CreditNote() {
    }

    public CreditNote(Long creditNoteNumber, Double creditAmount) {
        this.creditNoteNumber = creditNoteNumber;
        this.creditAmount = creditAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreditNoteNumber() {
        return creditNoteNumber;
    }

    public void setCreditNoteNumber(Long creditNoteNumber) {
        this.creditNoteNumber = creditNoteNumber;
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
