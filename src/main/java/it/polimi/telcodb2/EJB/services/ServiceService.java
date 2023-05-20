package it.polimi.telcodb2.EJB.services;

import it.polimi.telcodb2.EJB.entities.Service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class ServiceService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Find all equivalent services (matches each given attributes)
     * @param serviceType type of the service
     * @param minutes minutes of phone calls included
     * @param extraMinutesFee fee for each extra minute
     * @param sms SMS included
     * @param extraSmsFee fee for each extra SMS
     * @param giga GB of Internet traffic included
     * @param extraGigaFee fee for each extra GB
     * @return the list of matching services
     */
    public List<Service> findEquivalents(int serviceType, Integer minutes, Float extraMinutesFee, Integer sms, Float extraSmsFee, Integer giga, Float extraGigaFee) {
        return em.createNamedQuery("Service.findEquivalent", Service.class)
                .setParameter("serviceType", serviceType)
                .setParameter("minutes", minutes)
                .setParameter("extraMinutesFee", extraMinutesFee)
                .setParameter("sms", sms)
                .setParameter("extraSmsFee", extraSmsFee)
                .setParameter("giga", giga)
                .setParameter("extraGigaFee", extraGigaFee)
                .getResultList();
    }

    /**
     * Find all services in the database
     * @return the list of services
     */
    public List<Service> findAll() {
        return em.createNamedQuery("Service.findAll", Service.class)
                .getResultList();
    }

    /**
     * Create new service and add it to the database
     * @param serviceType type of the service
     * @param minutes minutes included in the service
     * @param extraMinutesFee fee for each extra minute
     * @param sms SMS included in the service
     * @param extraSmsFee fee for each extra SMS
     * @param giga GB of internet traffic included in the service
     * @param extraGigaFee fee for each extra GB of internet traffic
     * @return the service if everything went right, else null
     */
    public Service createService(int serviceType, Integer minutes, Float extraMinutesFee, Integer sms, Float extraSmsFee, Integer giga, Float extraGigaFee) {
        Service newService = new Service(serviceType, minutes, extraMinutesFee, sms, extraSmsFee, giga, extraGigaFee);

        try {
            em.persist(newService);
            em.flush();
            return newService;
        } catch (ConstraintViolationException e) {
            return null;
        }
    }
}

