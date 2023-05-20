package it.polimi.telcodb2.EJB.services;

import it.polimi.telcodb2.EJB.entities.Customer;
import it.polimi.telcodb2.EJB.entities.Order;
import it.polimi.telcodb2.EJB.exceptions.CredentialsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class CustomerService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Find customer given his/her username
     * @param username the username of the customer
     * @return the customer entity object
     */
    public List<Customer> findByUsername(String username) {
        return em.createNamedQuery("Customer.findByUsername", Customer.class)
                .setParameter("username", username)
                .getResultList();
    }


    /**
     * Find customer given his/her email
     * @param email the email of the customer
     * @return the customer entity object
     */
    public List<Customer> findByEmail(String email) {
        return em.createNamedQuery("Customer.findByEmail", Customer.class)
                .setParameter("email", email)
                .getResultList();
    }

    /**
     * Create new customer and insert it into the database.
     * @param username the username of the customer
     * @param password the password of the customer
     * @param email the email of the password
     * @return the customer entity object if the operation was successful, else null
     */
    public Customer createCustomer(String username, String password, String email) {
        // Create new customer entity object
        Customer customer = new Customer(
                username, password, email);

        // Try to store the new customer into the database
        try {
            em.persist(customer);
            em.flush();
            return customer;
        } catch (ConstraintViolationException e) {
            return null;
        }
    }

    /**
     * Check that there is a customer in the database that matches some given credentials.
     * @param username username to look for
     * @param password password to verify
     * @return the customer entity object the credentials match, else null
     * @throws CredentialsException if any error occurs during the fetching of the data from the database
     * @throws NonUniqueResultException if there are more than one customer with the same username
     */
    public Customer
    checkCredentials(String username, String password) throws NonUniqueResultException {
        // Fetch customers by username
        List<Customer> customerList;
        customerList = findByUsername(username);

        // If there are no customers, then return null
        // if there is only one customer, then check the password
        // else (there are more customers for the same username) throw exception
        if (customerList.isEmpty()) {
            return null;
        } else if (customerList.size() == 1) {
            Customer customer = customerList.get(0);
            // If the password match, then return the customer
            // else return null
            if (customer.getPassword().equals(password)) {
                return customer;
            } else {
                return null;
            }

        } else {
            throw new NonUniqueResultException("More than one user registered with the same credentials");
        }
    }

    /**
     * Get the list of all non paid order for a customer
     * @param customerId the id of the customer
     * @return the list of orders
     */
    public List<Order> findPendingOrders(int customerId) {
        // Check if customer is valid
        if (customerId < 0) {
            return null;
        }

        // Find pending orders
        return em.createNamedQuery("Order.findPendingByIdCustomer", Order.class)
                .setParameter("idCustomer", customerId)
                .getResultList();
    }
}
