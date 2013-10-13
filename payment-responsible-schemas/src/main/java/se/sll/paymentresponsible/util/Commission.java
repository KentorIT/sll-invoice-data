package se.sll.paymentresponsible.util;

import java.io.Serializable;

/**
 * 
 * @author Peter
 *
 */
public class Commission extends AbstractTermItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private CommissionType commissionType;

    public CommissionType getCommissionType() {
        return commissionType;
    }
    public void setCommissionType(CommissionType commissionType) {
        this.commissionType = commissionType;
    }
}
