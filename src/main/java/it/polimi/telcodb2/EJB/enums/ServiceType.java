package it.polimi.telcodb2.EJB.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * Type of service
 */
public enum ServiceType {

    FIXED_PHONE     (0, "Fixed Phone"),
    FIXED_INTERNET  (1, "Fixed Internet"),
    MOBILE_PHONE    (2, "Mobile Phone"),
    MOBILE_INTERNET (3, "Mobile Internet");

    private final int code;
    private final String name;

    ServiceType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * Find ServiceType that matches given name
     * @param name name to look for
     * @return the service type if there is one with the given name, else null
     */
    public static ServiceType findByName(String name) {
        Optional<ServiceType> serviceType = Arrays.stream(values())
                .filter(elem -> elem.getName().equals(name))
                .findFirst();
        return serviceType.isPresent() ? serviceType.get() : null;
    }

    /**
     * Find ServiceType that matches given code
     * @param code code to look for
     * @return the service type if there is one with the given code, else null
     */
    public static ServiceType findByCode(int code) {
        Optional<ServiceType> serviceType = Arrays.stream(values())
                .filter(elem -> elem.getCode() == code)
                .findFirst();
        return serviceType.isPresent() ? serviceType.get() : null;
    }
}
