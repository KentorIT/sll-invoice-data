package se.sll.paymentresponsible.util;

import java.io.Serializable;

public class HSAMapping extends AbstractTermItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Facility facility;

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Override
    public String toString() {
        return String.format("HSAId: %s, Kombika: %s, Period: %3$tY-%3$tm-%3$td - %4$tY-%4$tm-%4$td", getId(), facility.getId(), getValidFrom(), getValidTo());
    }
}
