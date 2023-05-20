package it.polimi.telcodb2.EJB.utils;

import it.polimi.telcodb2.EJB.entities.*;
import it.polimi.telcodb2.EJB.entities.Package;

import javax.validation.ConstraintViolationException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderSummary {

    private Integer idOrder = -1;
    private LocalDate startDate;
    private Validity validity;
    private List<Product> products;
    private Package pkg;
    private LocalDateTime creationDateTime;
    private Float totalCost;

    /**
     * Constructor for small data
     * @param startDate start date chosen with the order
     * @param validity validity chosen with the order
     * @param products optional product chosen with the order
     * @param pkg package included in the order
     */
    public OrderSummary(LocalDate startDate, Validity validity, List<Product> products, Package pkg) {
        this.creationDateTime = LocalDateTime.now();
        this.totalCost = products.stream().map(Product::getFee).reduce(validity.getFee(), Float::sum) * validity.getDuration();
        this.startDate = startDate;
        this.validity = validity;
        this.products = products;
        this.pkg = pkg;
        this.idOrder = -1;
    }

    /**
     * Constructor for complete order data
     * @param idOrder id of the order entity in the database
     * @param startDate start date chosen with the order
     * @param creationDateTime creation timestamp of the order entity
     * @param validity validity chosen with the order
     * @param products optional products chosen with the order
     * @param pkg package included in the order
     * @param totalCost total cost of the order
     */
    public OrderSummary(Integer idOrder, LocalDate startDate, LocalDateTime creationDateTime, Validity validity, List<Product> products, Package pkg, Float totalCost) {
        this.idOrder = idOrder;
        this.startDate = startDate;
        this.validity = validity;
        this.products = products;
        this.pkg = pkg;
        this.creationDateTime = creationDateTime;
        this.totalCost = totalCost;
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Validity getValidity() {
        return validity;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Package getPackage() {
        return pkg;
    }

    /**
     * Return the total cost in a stable and human understandable way
     * @return the string format of the total cost
     */
    public String getDecimalTotalCost() {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(this.totalCost);
    }
}
