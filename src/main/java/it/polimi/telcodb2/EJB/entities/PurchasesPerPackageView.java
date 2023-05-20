package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "purchasesperpackage_mv", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "PurchasesPerPackageView.findAll",
                        query = "SELECT elem FROM PurchasesPerPackageView elem"
                )
        }
)
public class PurchasesPerPackageView implements Serializable {
    @Id
    @Column(name = "idPackage", nullable = false)
    private int idPackage;
    @Column(name = "name", nullable = false, length = 256)
    private String name;
    @Column(name = "purchases", nullable = false)
    private long numOfPurchases;

    public PurchasesPerPackageView(int idPackage, String name, long numOfPurchases) {
        this.idPackage = idPackage;
        this.name = name;
        this.numOfPurchases = numOfPurchases;
    }

    public PurchasesPerPackageView() {
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

    public long getNumOfPurchases() {
        return numOfPurchases;
    }

    public void setNumOfPurchases(long numOfPurchases) {
        this.numOfPurchases = numOfPurchases;
    }
}
