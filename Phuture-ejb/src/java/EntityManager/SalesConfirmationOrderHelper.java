package EntityManager;

import java.io.Serializable;
import java.util.List;

public class SalesConfirmationOrderHelper implements Serializable {

    private SalesConfirmationOrder sco;
    private List<DeliveryOrder> deliveryOrders;
    private List<Invoice> invoices;
    private List<PurchaseOrder> purchaseOrders;

    public SalesConfirmationOrder getSco() {
        return sco;
    }

    public void setSco(SalesConfirmationOrder sco) {
        this.sco = sco;
    }

    public List<DeliveryOrder> getDeliveryOrders() {
        return deliveryOrders;
    }

    public void setDeliveryOrders(List<DeliveryOrder> deliveryOrders) {
        this.deliveryOrders = deliveryOrders;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }
    
    
   
}
