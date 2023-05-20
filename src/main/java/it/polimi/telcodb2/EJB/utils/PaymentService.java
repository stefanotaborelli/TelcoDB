package it.polimi.telcodb2.EJB.utils;

import java.util.Random;

public class PaymentService {

    /**
     * Simulate the external payment service and return the payment result
     * @return true if the payment was successful, else false
     */
    public static boolean pay() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
