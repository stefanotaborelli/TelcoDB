package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Order", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "Order.findPendingByIdCustomer",
                        query = "SELECT o FROM Order o WHERE o.customer.idCustomer = :idCustomer " +
                                "AND o.paid = false"
                )
        }
)
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idOrder", nullable = false)
    private int idOrder;

    @Column(name="startDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(name="creationDateTime", nullable = false)
    private LocalDateTime creationDateTime = null;

    @Column(name="totalCost", nullable = false)
    private float totalCost = 0;

    @Column(name="paid", nullable = false)
    private boolean paid = false;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "idCustomer")
    private Customer customer;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "ChosenProduct",
            joinColumns = @JoinColumn(name = "idOrder"),
            inverseJoinColumns = @JoinColumn(name = "idProduct"))
    private List<Product> products;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "idPackage")
    private Package aPackage;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "idValidity")
    private Validity validity;

    public Order() {
    }

    public Order(LocalDate startDate, Customer customer, List<Product> products, Package aPackage, Validity validity) {
        this.startDate = startDate;
        this.creationDateTime = LocalDateTime.now();
        this.totalCost = products.stream().map(Product::getFee).reduce(validity.getFee(), Float::sum) * validity.getDuration();
        this.customer = customer;
        this.products = products;
        this.aPackage = aPackage;
        this.validity = validity;
        this.paid = false;
    }
    public Order(LocalDate startDate, Customer customer, List<Product> products, Package aPackage, Validity validity, boolean paid) {
        this.startDate = startDate;
        this.creationDateTime = LocalDateTime.now();
        this.totalCost = products.stream().map(Product::getFee).reduce(validity.getFee(), Float::sum) * validity.getDuration();
        this.customer = customer;
        this.products = products;
        this.aPackage = aPackage;
        this.validity = validity;
        this.paid = paid;
    }

    public Order(LocalDate startDate, Validity validity, List<Product> products, Package aPackage) {
        this.idOrder = -1;
        this.startDate = startDate;
        // Sum the fees
        this.totalCost = products.stream().map(Product::getFee).reduce(validity.getFee(), Float::sum) * validity.getDuration();
        this.validity = validity;
        this.products = products;
        this.aPackage = aPackage;
        this.paid = false;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Package getPackage() {
        return aPackage;
    }

    public void setPackage(Package aPackage) {
        this.aPackage = aPackage;
    }

    public Validity getValidity() {
        return validity;
    }

    public void setValidity(Validity validity) {
        this.validity = validity;
    }

    public String getDecimalTotalCost() {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(this.totalCost);
    }
}
