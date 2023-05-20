package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "purchasesperpackagevalidity_mv", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "PurchasesPerPackageValidityView.findAll",
                        query = "SELECT elem FROM PurchasesPerPackageValidityView elem"
                )
        }
)
public class PurchasesPerPackageValidityView implements Serializable {
    @Id
    @Column(name = "hashid", nullable = false)
    private String hash;

    @Column(name = "idPackage", nullable = false)
    private int idPackage;
    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "idValidity", nullable = false)
    private int idValidity;
    @Column(name = "duration", nullable = false)
    private int duration;
    @Column(name = "fee", nullable = false, precision = 2)
    private BigDecimal fee;
    @Column(name = "purchases", nullable = false)
    private long numOfPurchases;

    public PurchasesPerPackageValidityView(int idPackage, String name, int idValidity, int duration, BigDecimal fee, long numOfPurchases) {
        this.idPackage = idPackage;
        this.name = name;
        this.idValidity = idValidity;
        this.duration = duration;
        this.fee = fee;
        this.numOfPurchases = numOfPurchases;
    }

    public PurchasesPerPackageValidityView() {
    }

    public int getIdPackage() {
        return idPackage;
    }

    public void setIdPackage(int idPackage) {
        this.idPackage = idPackage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdValidity() {
        return idValidity;
    }

    public void setIdValidity(int idValidity) {
        this.idValidity = idValidity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public long getNumOfPurchases() {
        return numOfPurchases;
    }

    public void setNumOfPurchases(long numOfPurchases) {
        this.numOfPurchases = numOfPurchases;
    }
}
