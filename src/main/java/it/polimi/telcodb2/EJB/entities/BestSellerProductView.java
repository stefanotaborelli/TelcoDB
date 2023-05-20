package it.polimi.telcodb2.EJB.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "bestsellerproduct_mv", schema = "TelcoDB")
@NamedQueries(
        {
                @NamedQuery(
                        name = "BestSellerProductView.findAll",
                        query = "SELECT elem FROM BestSellerProductView elem"
                )
        }
)
public class BestSellerProductView implements Serializable {
    @Id
    @Column(name = "idProduct", nullable = false)
    private int idProduct;
    @Column(name = "name", nullable = false, length = 256)
    private String productName;
    @Column(name = "valueOfSales", nullable = false, precision = 2)
    private BigDecimal valueOfSales;

    public BestSellerProductView(int idProduct, String productName, BigDecimal valueOfSales) {
        this.idProduct = idProduct;
        this.productName = productName;
        this.valueOfSales = valueOfSales;
    }

    public BestSellerProductView() {
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getValueOfSales() {
        return valueOfSales;
    }

    public void setValueOfSales(BigDecimal valueOfSales) {
        this.valueOfSales = valueOfSales;
    }
}
