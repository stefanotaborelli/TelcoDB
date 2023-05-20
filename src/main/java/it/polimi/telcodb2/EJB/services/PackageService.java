package it.polimi.telcodb2.EJB.services;

import it.polimi.telcodb2.EJB.entities.Package;
import it.polimi.telcodb2.EJB.entities.Product;
import it.polimi.telcodb2.EJB.entities.Service;
import it.polimi.telcodb2.EJB.entities.Validity;
import it.polimi.telcodb2.EJB.utils.Pair;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PackageService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Fetch packages by name
     * @param name name of the product to look for
     * @return the list containing the products matching the name
     */
    public List<Package> findByName(String name) {
        return em.createNamedQuery("Product.findByName", Package.class)
                .setParameter("name", name)
                .getResultList();
    }

    /**
     * Create a package and every related entity that does not exist
     * @param name the name of the package
     * @param validityDataList the list of data for each validity compatible with the package
     * @param serviceIdList the list of ids of the services included in the package
     * @param productIdList the list of ids of the products compatible with the package
     * @return the package entity object
     */
    public Package createPackage(String name, List<Pair<Integer, Float>> validityDataList, List<Integer> serviceIdList, List<Integer> productIdList) {
        // Get optional product entity objects
        List<Product> productList = productIdList.stream()
                .map(id -> em.find(Product.class, id))
                .collect(Collectors.toList());
        // Get included service entity objects
        List<Service> serviceList = serviceIdList.stream()
                .map(id -> em.find(Service.class, id))
                .collect(Collectors.toList());
        // Get or create compatible validity entity objects
        List<Validity> validityList = validityDataList.stream()
                .map(data -> {
                    Validity tmpValidity;
                    List<Validity> tmpValidityList = em.createNamedQuery("Validity.findEquivalent", Validity.class)
                            .setParameter("duration", data.getX())
                            .setParameter("fee", data.getY())
                            .getResultList();
                    if (tmpValidityList.isEmpty()) {    // If there are no equivalent validities, create a new one
                        tmpValidity = new Validity(data.getX(), data.getY());
                        em.persist(tmpValidity);
                        // em.flush(); TODO: check if it
                    } else { // else, take the first (only one)
                        tmpValidity = tmpValidityList.get(0);
                    }
                    return tmpValidity;
                })
                .collect(Collectors.toList());

        // Create new customer entity object
        Package aPackage = new Package(name, validityList, serviceList, productList);
        // Try to store the new customer into the database
        try {
            em.persist(aPackage);
            em.flush();
            return aPackage;
        } catch (ConstraintViolationException e) {
            return null;
        }
    }

    /**
     * Fetch all packages in database
     * @return the list of all packages
     */
    public List<Package> findAll() {
        return em.createNamedQuery("Package.findAll", Package.class)
                .getResultList();
    }

    /**
     * Fetch package by id
     * @param id package id
     * @return the package entity object or null
     */
    public Package find(int id) {
        return em.find(Package.class, id);
    }
}
