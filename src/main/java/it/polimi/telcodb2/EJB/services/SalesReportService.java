package it.polimi.telcodb2.EJB.services;

import it.polimi.telcodb2.EJB.entities.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class SalesReportService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Access a materialized view
     * @return the list of the average numbers of products sold with each service package
     */
    public List<AvgProductSoldView> findAvgProductSold() {
        return em.createNamedQuery("AvgProductSoldView.findAll", AvgProductSoldView.class).getResultList();
    }

    /**
     * Access a materialized view
     * @return the list of the average numbers of products sold with each service package
     */
    public BestSellerProductView findBestSellerProduct() {
        Optional<BestSellerProductView> optBestSellerProductView = em.createNamedQuery("BestSellerProductView.findAll", BestSellerProductView.class)
                .getResultStream().findFirst();
        return optBestSellerProductView.orElse(null);
    }

    /**
     * Access a materialized view
     * @return the list of insolent customers, their unpaid orders and the relative alerts
     */
    public List<InsolventCustomersReportView> findInsolventCustomersReport() {
        return em.createNamedQuery("InsolventCustomersReportView.findAll", InsolventCustomersReportView.class).getResultList();
    }

    /**
     * Access a materialized view
     * @return the list of the numbers of purchases for each package-validity couple
     */
    public List<PurchasesPerPackageValidityView> findPurchasePerPackageValidity() {
        return em.createNamedQuery("PurchasesPerPackageValidityView.findAll", PurchasesPerPackageValidityView.class).getResultList();
    }

    /**
     * Access a materialized view
     * @return the list of the numbers of purchases for each service package
     */
    public List<PurchasesPerPackageView> findPurchasesPerPackage() {
        return em.createNamedQuery("PurchasesPerPackageView.findAll", PurchasesPerPackageView.class).getResultList();
    }

    /**
     * Access a materialized view
     * @return the list of total sales values for each service package
     */
    public List<TotalSalesValueView> findTotalSalesValue() {
        return em.createNamedQuery("TotalSalesValueView.findAll", TotalSalesValueView.class).getResultList();
    }
}
