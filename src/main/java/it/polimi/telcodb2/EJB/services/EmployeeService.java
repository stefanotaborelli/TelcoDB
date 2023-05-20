package it.polimi.telcodb2.EJB.services;

import it.polimi.telcodb2.EJB.entities.Employee;
import it.polimi.telcodb2.EJB.exceptions.CredentialsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless
public class EmployeeService {
    @PersistenceContext
    private EntityManager em;

    /**
     * Check if some given credentials match any employee in the database
     * @param username the username of the employee
     * @param password the password of the employee
     * @return the employee entity object if credentials match, else null
     * @throws CredentialsException if any error occurs during the fetching of the data from the database
     * @throws NonUniqueResultException if there are more than one customer with the same username
     */
    public Employee checkCredentials(String username, String password) throws NonUniqueResultException {
        List<Employee> employeeList;

        employeeList = em.createNamedQuery("Employee.checkCredentials", Employee.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList();

        if (employeeList.isEmpty())
            return null;
        else if (employeeList.size() == 1)
            return employeeList.get(0);
        else {
            throw new NonUniqueResultException("More than one user registered with same credentials");
        }
    }
}
