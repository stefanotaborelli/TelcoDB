package it.polimi.telcodb2.EJB.entities;

import javax.inject.Named;
import javax.persistence.*;

import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "Product", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "Product.findByName",
                        query = "SELECT p FROM Product p WHERE p.name = :name"
                ),
                @NamedQuery(
                        name = "Product.findAll",
                        query = "SELECT p FROM Product p"
                )
        }
)
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProduct", nullable = false)
    private int idProduct;

    @Column(name="name", nullable = true)
    private String name;

    @Column(name="fee", nullable = true)
    private float fee;

    @ManyToMany(mappedBy = "products",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Collection<Package> packages;

    @ManyToMany(mappedBy = "products",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Collection<Schedule> schedules;

    @ManyToMany(mappedBy = "products",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Collection<Order> orders;

    public Product() {
    }

    public Product(String name, float fee) {
        this.name = name;
        this.fee = fee;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public Collection<Package> getPackages() {
        return packages;
    }

    public void setPackages(Collection<Package> packages) {
        this.packages = packages;
    }

    public Collection<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Collection<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }
}
