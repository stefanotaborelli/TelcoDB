package it.polimi.telcodb2.EJB.utils;

import java.time.ZoneId;

public class TimezoneHandler {
    public static ZoneId getCustomTimezoneID() {
        return ZoneId.of("Etc/GMT+0");
    }
}
