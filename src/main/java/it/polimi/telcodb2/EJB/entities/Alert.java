package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Alert", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "Alert.findByCustomerId",
                        query = "SELECT a FROM Alert a WHERE a.customer.idCustomer = :idCustomer"
                )
        }
)
public class Alert implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAlert", nullable = false)
    private int idAlert;

    @Column(name = "lastPayment", nullable = false)
    private LocalDateTime lastPayment;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "idCustomer")
    private Customer customer;

    public Alert() {
    }

    public Alert(LocalDateTime lastPayment, float amount, String email, String username, Customer customer) {
        this.lastPayment = lastPayment;
        this.amount = amount;
        this.email = email;
        this.username = username;
        this.customer = customer;
    }

    public void setIdAlert(int idAlert) {
        this.idAlert = idAlert;
    }

    public LocalDateTime getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(LocalDateTime lastPayment) {
        this.lastPayment = lastPayment;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
