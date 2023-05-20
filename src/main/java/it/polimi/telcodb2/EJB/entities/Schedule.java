package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Schedule", schema = "TelcoDB")
public class Schedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idSchedule", nullable = false)
    private int idSchedule;

    @Column(name="activationDate", nullable = false)
    private LocalDate activationDate;

    @Column(name="deactivationDate", nullable = false)
    private LocalDate deactivationDate;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "idCustomer")
    private Customer customer;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "ScheduledProducts",
            joinColumns = @JoinColumn(name = "idSchedule"),
            inverseJoinColumns = @JoinColumn(name = "idProduct"))
    private List<Product> products;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "ScheduledServices",
            joinColumns = @JoinColumn(name = "idSchedule"),
            inverseJoinColumns = @JoinColumn(name = "idService"))
    private List<Service> services;

    public Schedule() {
    }

    public Schedule(LocalDate activationDate, LocalDate deactivationDate, Customer customer, List<Product> products, List<Service> services) {
        this.activationDate = activationDate;
        this.deactivationDate = deactivationDate;
        this.customer = customer;
        this.products = products;
        this.services = services;
    }

    public int getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(int idSchedule) {
        this.idSchedule = idSchedule;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDate getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(LocalDate deactivationDate) {
        this.deactivationDate = deactivationDate;
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

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
