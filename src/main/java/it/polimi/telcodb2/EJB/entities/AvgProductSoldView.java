package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "avgproductssold_mv", schema = "TelcoDB")
@NamedQueries(
        {
            @NamedQuery(
                    name = "AvgProductSoldView.findAll",
                    query = "SELECT elem FROM AvgProductSoldView elem"
            )
        }
)
public class AvgProductSoldView implements Serializable {
    @Id
    @Column(name = "idPackage", nullable = false)
    private int idPackage;
    @Column(name = "name", nullable = false, length = 256)
    private String name;
    @Column(name = "avgNumOfProducts", nullable = false, precision = 2)
    private BigDecimal avgNumOfProducts;

    public AvgProductSoldView(int idPackage, String name, BigDecimal avgNumOfProducts) {
        this.idPackage = idPackage;
        this.name = name;
        this.avgNumOfProducts = avgNumOfProducts;
    }

    public AvgProductSoldView() {
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

    public BigDecimal getAvgNumOfProducts() {
        return avgNumOfProducts;
    }

    public void setAvgNumOfProducts(BigDecimal avgNumOfProducts) {
        this.avgNumOfProducts = avgNumOfProducts;
    }
}
