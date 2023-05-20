package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "insolventcustomersreport_mv", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "InsolventCustomersReportView.findAll",
                        query = "SELECT elem FROM InsolventCustomersReportView elem"
                )
        }
)
public class InsolventCustomersReportView implements Serializable {
    @Column(name = "idCustomer", nullable = false)
    private int idCustomer;
    @Column(name = "username", nullable = false, length = 256)
    private String username;
    @Column(name = "email", nullable = false, length = 256)
    private String email;
    @Id
    @Column(name = "idOrder", nullable = false)
    private int idOrder;
    @Temporal(TemporalType.DATE)
    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creationDateTime", nullable = false)
    private LocalDateTime creationDateTime;
    @Column(name = "totalCost", nullable = false, precision = 2)
    private BigDecimal totalCost;
    @Column(name = "idPackage", nullable = false)
    private int idPackage;
    @Column(name = "idValidity", nullable = false)
    private int idValidity;
    @Column(name = "idAlert", nullable = true)
    private Integer idAlert;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastPayment", nullable = true)
    private LocalDateTime lastPayment;
    @Column(name = "amount", nullable = true, precision = 2)
    private BigDecimal amount;

    public InsolventCustomersReportView(int idCustomer, String username, String email, int idOrder, LocalDate startDate, LocalDateTime creationDateTime, BigDecimal totalCost, int idPackage, int idValidity, Integer idAlert, LocalDateTime lastPayment, BigDecimal amount) {
        this.idCustomer = idCustomer;
        this.username = username;
        this.email = email;
        this.idOrder = idOrder;
        this.startDate = startDate;
        this.creationDateTime = creationDateTime;
        this.totalCost = totalCost;
        this.idPackage = idPackage;
        this.idValidity = idValidity;
        this.idAlert = idAlert;
        this.lastPayment = lastPayment;
        this.amount = amount;
    }

    public InsolventCustomersReportView() {
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public int getIdPackage() {
        return idPackage;
    }

    public void setIdPackage(int idPackage) {
        this.idPackage = idPackage;
    }

    public int getIdValidity() {
        return idValidity;
    }

    public void setIdValidity(int idValidity) {
        this.idValidity = idValidity;
    }

    public Integer getIdAlert() {
        return idAlert;
    }

    public void setIdAlert(Integer idAlert) {
        this.idAlert = idAlert;
    }

    public LocalDateTime getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(LocalDateTime lastPayment) {
        this.lastPayment = lastPayment;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
