package EntityManager;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class OrderNumbers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long nextSCO;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastGeneratedSCO;
    private Long nextPO;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastGeneratedPO;
    private Long nextDO;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastGeneratedDO;
    private Long nextInvoice;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastGeneratedInvoice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNextSCO() {
        return nextSCO;
    }

    public void setNextSCO(Long nextSCO) {
        this.nextSCO = nextSCO;
    }

    public Long getNextPO() {
        return nextPO;
    }

    public void setNextPO(Long nextPO) {
        this.nextPO = nextPO;
    }

    public Long getNextDO() {
        return nextDO;
    }

    public void setNextDO(Long lastDO) {
        this.nextDO = lastDO;
    }

    public Long getNextInvoice() {
        return nextInvoice;
    }

    public void setNextInvoice(Long nextInvoice) {
        this.nextInvoice = nextInvoice;
    }

    public Date getLastGeneratedSCO() {
        return lastGeneratedSCO;
    }

    public void setLastGeneratedSCO(Date lastGeneratedSCO) {
        this.lastGeneratedSCO = lastGeneratedSCO;
    }

    public Date getLastGeneratedPO() {
        return lastGeneratedPO;
    }

    public void setLastGeneratedPO(Date lastGeneratedPO) {
        this.lastGeneratedPO = lastGeneratedPO;
    }

    public Date getLastGeneratedInvoice() {
        return lastGeneratedInvoice;
    }

    public void setLastGeneratedInvoice(Date lastGeneratedInvoice) {
        this.lastGeneratedInvoice = lastGeneratedInvoice;
    }

    public Date getLastGeneratedDO() {
        return lastGeneratedDO;
    }

    public void setLastGeneratedDO(Date lastGeneratedDO) {
        this.lastGeneratedDO = lastGeneratedDO;
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
        if (!(object instanceof OrderNumbers)) {
            return false;
        }
        OrderNumbers other = (OrderNumbers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EntityManager.OrderNumbers[ id=" + id + " ]";
    }

}
