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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Index;

import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;

/**
 * Persistent invoice data information.
 *
 * @author Peter
 */
@Entity
@Table(name=InvoiceDataEntity.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo=InvoiceDataEntity.TABLE_NAME, indexes = {
@Index(name=InvoiceDataEntity.INDEX_NAME_1, columnNames = { InvoiceDataEntity.SUPPLIER_ID, InvoiceDataEntity.PENDING, 
																							InvoiceDataEntity.START_DATE, InvoiceDataEntity.END_DATE  }),
@Index(name=InvoiceDataEntity.INDEX_NAME_2, columnNames = { InvoiceDataEntity.PAYMENT_RESPONSIBLE, InvoiceDataEntity.PENDING, 
																							InvoiceDataEntity.START_DATE, InvoiceDataEntity.END_DATE  }),
@Index(name=InvoiceDataEntity.INDEX_NAME_3, columnNames = { InvoiceDataEntity.SUPPLIER_ID, InvoiceDataEntity.PAYMENT_RESPONSIBLE, 
																						InvoiceDataEntity.COST_CENTER, InvoiceDataEntity.PENDING  }) })
public class InvoiceDataEntity {
	
	static final String TABLE_NAME = "invoice_data";
	//Used in ListInvoiceData
	static final String INDEX_NAME_1 = "invoice_data_query_ix_1"; 
	//Used in ListInvoiceData
	static final String INDEX_NAME_2 = "invoice_data_query_ix_2";
	//Used in RegisterInvoiceData
	static final String INDEX_NAME_3 = "invoice_data_query_ix_3";
		
    static final String SUPPLIER_ID = "supplier_id";
    static final String COST_CENTER = "cost_center";
    static final String PAYMENT_RESPONSIBLE = "payment_responsible";
    static final String PENDING = "pending";
    static final String TOTAL_AMOUNT = "total_amount";
    static final String START_DATE = "start_date";
    static final String END_DATE = "end_date";
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name=SUPPLIER_ID, length=64, nullable=false, updatable=false)
    private String supplierId;
    
    @Column(name=COST_CENTER, length=64, nullable=false, updatable=false)
    private String costCenter;
    
    @Column(name=PAYMENT_RESPONSIBLE, length=64, nullable=false, updatable=false)
    private String paymentResponsible;

    @Column(name="created_by", length=64, nullable=false, updatable=false)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable=false, updatable=false)
    private Date createdTime;

    @Temporal(TemporalType.DATE)
    @Column(name = START_DATE, nullable=false, updatable=true)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = END_DATE, nullable=false, updatable=true)
    private Date endDate;

    @Column(name=TOTAL_AMOUNT, precision=12, scale=2, updatable=true)
    private BigDecimal totalAmount;
    
    @Column(name=PENDING, nullable=false, updatable=true)
    private Boolean pending = Boolean.TRUE;

    @OneToMany(fetch=FetchType.EAGER, mappedBy="invoiceData", orphanRemoval=false, cascade=CascadeType.ALL)    
    private List<BusinessEventEntity> businessEventEntities = new LinkedList<BusinessEventEntity>();

    @PrePersist
    void onPrePerist() {
        setCreatedTime(new Date());
        handleStartAndEndDateIfBusinessEventEntitiesListIsEmpty();
    }
    
    @PreUpdate
    void onPreUpdate() {
    	setCreatedTime(new Date());
    }
    
    private void handleStartAndEndDateIfBusinessEventEntitiesListIsEmpty() {
    	if (businessEventEntities.size() == 0) {
    		setStartDate(new Date());
        	setEndDate(new Date());
        	setTotalAmount(BigDecimal.valueOf(0.0));
        }
    }
    
    public void calculateTotalAmount(final BusinessEventEntity e) {
    	if (businessEventEntities.size() == 0) {
        	setStartDate(new Date(Long.MAX_VALUE));
        	setEndDate(new Date(0L));
        	setTotalAmount(BigDecimal.valueOf(0.0));
        }
        
        BigDecimal amount = getTotalAmount();
        if (e.getStartTime().before(getStartDate())) {
        	setStartDate(e.getStartTime());
        }
        if (e.getEndTime().after(getEndDate())) {
        	setEndDate(e.getEndTime());
        }
        if (e.isCredit()) {
            amount = amount.subtract(e.calculateTotalAmount());
        } else {
            amount = amount.add(e.calculateTotalAmount()); 
        }
        
        setTotalAmount(amount);
    }
    
    public void recalculateTotalAmount(final BusinessEventEntity e) {
    	setTotalAmount(getTotalAmount().subtract(e.calculateTotalAmount()));
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
        	throw InvoiceDataErrorCodeEnum.ILLEGAL_STATE_INVALID_INVOICEDATA_REFERENCE_ID.
        		createException("A valid reference can only be retrieved after saving invoice data to database");
        }
        return String.valueOf(getId());
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
            calculateTotalAmount(businessEventEntity);
            return businessEventEntities.add(businessEventEntity);
        }
        return false;
    }
    
    public boolean removeBusinessEventEntity(BusinessEventEntity businessEventEntity) {
    	if (getSupplierId().equals(businessEventEntity.getSupplierId())) {
    		businessEventEntity.setInvoiceData(null);
    		recalculateTotalAmount(businessEventEntity);
            return businessEventEntities.remove(businessEventEntity);
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

    protected void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public boolean isPending() {
        return (pending == Boolean.TRUE);
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
        
        if (!isPending()) {       
	        Date start = new Date(Long.MAX_VALUE);
	        Date end = new Date(0L);
	
	        for (final BusinessEventEntity e : this.getBusinessEventEntities()) {
	            if (e.getStartTime().before(start)) {
	                start = e.getStartTime();
	            }
	            if (e.getEndTime().after(end)) {
	                end = e.getEndTime();
	            }
	            e.setPending(null);
	        }        
	        setStartDate(start);
	        setEndDate(end);
        }
    }
    
    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
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
    
    public String logInfo() {
    	return new StringBuilder().append(supplierId).append("-").append(paymentResponsible).append("-").append(costCenter).toString();
    }
}
