package se.sll.paymentresponsible.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Peter
 *
 */
public class Facility extends AbstractTermItem  implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Commission> commissions = new ArrayList<Commission>();


    public List<Commission> getCommissions() {
        return commissions;
    }

    public void setCommissions(List<Commission> commissions) {
        this.commissions = commissions;
    }

}
