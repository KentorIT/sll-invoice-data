/**
 * Copyright (c) 2013 SLL. <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice-Data is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Invoice-Data is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Invoice-Data.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */

package se.sll.invoicedata.core.model.entity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Persistent invoice data information.
 *
 * @author Peter
 */
@Entity
@Table(name="invoice_data")
public class InvoiceDataEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name="supplier_id", length=64, nullable=false, updatable=false)
    private String supplierId;

    @Column(name="payment_responsible", length=64, nullable=false, updatable=false)
    private String paymentResponsible;

    @Column(name="created_by", length=64, nullable=false, updatable=false)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable=false, updatable=false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable=false, updatable=false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable=false, updatable=false)
    private Date endDate;

    @Column(name="total_amount", precision=8, scale=2, updatable=false)
    private BigDecimal totalAmount;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="invoiceData", orphanRemoval=false, cascade=CascadeType.ALL)    
    private List<BusinessEventEntity> businessEventEntities = new LinkedList<BusinessEventEntity>();


    @PrePersist
    void onPrePerist() {
        setCreatedTime(new Date());
        calcDerivedValues();
    }


    /**
     * Calculates derived property values, and stores them into database.
     */
    void calcDerivedValues() {

        if (businessEventEntities.size() == 0) {
            return;
        }

        Date start = new Date(Long.MAX_VALUE);
        Date end = new Date(0L);
        BigDecimal amount = BigDecimal.valueOf(0.0);

        for (final BusinessEventEntity e : businessEventEntities) {
            if (e.getStartTime().before(start)) {
                start = e.getStartTime();
            }
            if (e.getEndTime().after(end)) {
                end = e.getEndTime();
            }
            if (e.isCredit()) {
                amount = amount.subtract(e.getTotalAmount());
            } else {
                amount = amount.add(e.getTotalAmount()); 
            }
        }
        
        setStartDate(start);
        setEndDate(end);
        setTotalAmount(amount);
    }

    public Long getId() {
        return id;
    }

    /**
     * Returns invoice reference id.
     * 
     * @return the reference id.
     */
    public String getReferenceId() {
        if (getId() == null) {
            throw new IllegalStateException("A valid reference can only be retrieved after saving invoice data to database");
        }
        return String.format("%s.%06d", getSupplierId(), getId());
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getPaymentResponsible() {
        return paymentResponsible;
    }

    public void setPaymentResponsible(String paymentResponsible) {
        this.paymentResponsible = paymentResponsible;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    protected void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }


    public Date getEndDate() {
        return endDate;
    }

    protected void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    protected void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    //    /** XXX: Not needed.
    //     * Returns if a business entity has been removed from this invoice data. <p>
    //     * 
    //     * In order to be removed the business event entity must have been previously added to this
    //     * invoice data object.
    //     * 
    //     * @param businessEventEntity the entity to add.
    //     * @return true if removed, otherwise false.
    //     */
    //    public boolean removeBusinessEventEntity(BusinessEventEntity businessEventEntity) {
    //        if (this.equals(businessEventEntity.getInvoiceData())) {
    //            businessEventEntity.setInvoiceData(null);
    //            return businessEventEntities.remove(businessEventEntity);
    //        }
    //        return false;
    //    }

    /**
     * Returns if a business entity has been added/assign to this invoice data. <p>
     * 
     * In order to be added the business entity must be assigned to another invoice data object, nor have 
     * a reference to a different supplier identity.
     * 
     * @param businessEventEntity the entity to add.
     * @return true if added, otherwise false.
     */
    public boolean addBusinessEventEntity(BusinessEventEntity businessEventEntity) {
        if (businessEventEntity.getInvoiceData() == null && getSupplierId().equals(businessEventEntity.getSupplierId())) {
            businessEventEntity.setInvoiceData(this);
            return businessEventEntities.add(businessEventEntity);
        }
        return false;
    }

    public List<BusinessEventEntity> getBusinessEventEntities() {
        Collections.sort(businessEventEntities);
        return businessEventEntities;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns the total amount for all events.
     * 
     * @return the total amount for all events.
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    //
    protected void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        final Long id = getId();
        if (id != null && r instanceof InvoiceDataEntity) {
            return id.equals(((InvoiceDataEntity)r).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        final Long id = getId();
        return (id == null) ? super.hashCode() : id.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
