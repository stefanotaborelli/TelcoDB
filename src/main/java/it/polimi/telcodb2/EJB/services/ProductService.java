package it.polimi.telcodb2.EJB.services;

import it.polimi.telcodb2.EJB.entities.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class ProductService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Fetch all the products available in the database
     * @return the list containing all the products in the database
     */
    public List<Product> findAll() {
        return em.createNamedQuery("Product.findAll", Product.class)
                .getResultList();
    }

    /**
     * Fetch products by name
     * @param name name of the product to look for
     * @return the list containing the products matching the name
     */
    public List<Product> findByName(String name) {
        return em.createNamedQuery("Product.findByName", Product.class)
                .setParameter("name", name)
                .getResultList();
    }

    /**
     * Create new product and insert it into the database
     * @param name name of the product
     * @param fee fee of the product
     * @return the product if everything went good, else null
     */
    public Product createProduct(String name, float fee) {
        // Create new customer entity object
        Product product = new Product(name, fee);

        // Try to store the new customer into the database
        try {
            em.persist(product);
            em.flush();
            return product;
        } catch (ConstraintViolationException e) {
            return null;
        }
    }
}
