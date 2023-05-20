package it.polimi.telcodb2.EJB.enums;

public enum ValidityDuration {

    ONE_YEAR    (12),
    TWO_YEARS   (24),
    THREE_YEARS (36);

    private int months;

    ValidityDuration(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}
