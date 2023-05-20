package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "totalsalesvalue_mv", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "TotalSalesValueView.findAll",
                        query = "SELECT elem FROM TotalSalesValueView elem"
                )
        }
)
public class TotalSalesValueView implements Serializable {
    @Id
    @Column(name = "idPackage", nullable = false)
    private int idPackage;
    @Column(name = "name", nullable = false, length = 256)
    private String name;
    @Column(name = "completeValue", nullable = false, precision = 2)
    private BigDecimal salesWProducts;
    @Column(name = "partialValue", nullable = false, precision = 2)
    private BigDecimal salesWoutProducts;

    public TotalSalesValueView(int idPackage, String name, BigDecimal salesWProducts, BigDecimal salesWoutProducts) {
        this.idPackage = idPackage;
        this.name = name;
        this.salesWProducts = salesWProducts;
        this.salesWoutProducts = salesWoutProducts;
    }

    public TotalSalesValueView() {
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

    public BigDecimal getSalesWProducts() {
        return salesWProducts;
    }

    public void setSalesWProducts(BigDecimal salesWProducts) {
        this.salesWProducts = salesWProducts;
    }

    public BigDecimal getSalesWoutProducts() {
        return salesWoutProducts;
    }

    public void setSalesWoutProducts(BigDecimal salesWoutProducts) {
        this.salesWoutProducts = salesWoutProducts;
    }
}
