/**
 *
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Index;

/**
 * Business event information.
 *
 * @author Peter
 */
@Entity(name=BusinessEventEntity.TABLE_NAME)
@javax.persistence.Table(name = BusinessEventEntity.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo=BusinessEventEntity.TABLE_NAME, indexes = { 
@Index(name=BusinessEventEntity.INDEX_NAME_1, columnNames = { BusinessEventEntity.SUPPLIER_ID, BusinessEventEntity.PENDING } ),
@Index(name=BusinessEventEntity.INDEX_NAME_2, columnNames = { BusinessEventEntity.EVENT_ID }),
@Index(name=BusinessEventEntity.INDEX_NAME_3, columnNames = { BusinessEventEntity.ACKNOWLEDGEMENT_ID }) })

public class BusinessEventEntity {
    static final String TABLE_NAME = "invoice_data_event";
    static final String INDEX_NAME_1 = "invoice_data_event_query_ix_1";
    static final String INDEX_NAME_2 = "invoice_data_event_query_ix_2";
    static final String INDEX_NAME_3 = "invoice_data_event_query_ix_3";
    static final String SUPPLIER_ID = "supplier_id";
    static final String PENDING = "pending";
    static final String EVENT_ID = "event_id";
    static final String ACKNOWLEDGEMENT_ID = "acknowledgement_id";
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name=EVENT_ID, nullable=false, updatable=false, length=64)
    private String eventId;
    
    @Column(name="credit", updatable=false, length=64)
    private Boolean credit;

    @Column(name="credited", updatable=true, length=64)
    private Boolean credited;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable=false, updatable=false)
    private Date createdTimestamp;
    
    @Column(name="healthcare_facility", length=64, nullable=false, updatable=false)
    private String healthcareFacility;
    
    @Column(name="ref_contract_id", length=64, nullable=false, updatable=false)
    private String refContractId;
    
    @Column(name=SUPPLIER_ID, length=64, nullable=false, updatable=false)
    private String supplierId;
    
    @Column(name="supplier_name", length=256, nullable=false, updatable=false)
    private String supplierName;

    @Column(name="service_code", length=64, nullable=false, updatable=false)
    private String serviceCode;
    
    @Column(name=ACKNOWLEDGEMENT_ID, length=64, nullable=false, updatable=false)
    private String acknowledgementId;
    
    @Column(name="acknowledged_by", length=64, nullable=false, updatable=false)
    private String acknowledgedBy;
    
    @Column(name="payment_responsible", length=64, nullable=false, updatable=false)
    private String paymentResponsible;
    
    @Column(name="healthcare_commission", length=64, nullable=false, updatable=false)
    private String healthCareCommission;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "acknowledged_time", nullable=false, updatable=false)
    private Date acknowledgedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time", nullable=false, updatable=false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", nullable=false, updatable=false)
    private Date endTime;
    
    @OneToMany(fetch=FetchType.EAGER, mappedBy="event", orphanRemoval=true, cascade=CascadeType.ALL)    
    private List<ItemEntity> itemEntities = new LinkedList<ItemEntity>();

    @ManyToOne(optional=true)
    @JoinColumn(name="invoice_data_id")
    private InvoiceDataEntity invoiceData;

    /** Derived field enabling indexed queries on supplier and pending events.
     * 
     * If pending is true the event is not yet assigned to any invoice data.
     */
    @Column(name=PENDING, nullable=true, updatable=true)
    private Boolean pending;

    @PrePersist
    void onPrePerist() {
        updatePending();
        setCreatedTimestamp(new Date());
    }
    
    @PreUpdate
    void onPreUpdate() {
        updatePending();
    }

    /**
     * Updates the pending value (derived).
     */
    private void updatePending() {
        setPending((getInvoiceData() == null) ? Boolean.TRUE : null);        
    }

    public Long getId() {
        return id;
    }
    
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getHealthcareFacility() {
		return healthcareFacility;
	}

	public void setHealthcareFacility(String healthcareFacility) {
		this.healthcareFacility = healthcareFacility;
	}

	public String getRefContractId() {
		return refContractId;
	}

	public void setRefContractId(String refContractId) {
		this.refContractId = refContractId;
	}

	public Boolean getCredit() {
        return credit;
    }

    public void setCredit(Boolean credit) {
        this.credit = credit;
    }
    
    public boolean isCredit() {
        return (this.credit == Boolean.TRUE);
    }

    public Boolean getCredited() {
        return credited;
    }

    public void setCredited(Boolean credited) {
        this.credited = credited;
    }

    public boolean isCredited() {
        return (this.credited == Boolean.TRUE);
    }
    
    public String getSupplierName() {
        return supplierName;
    }



    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }


    public String getAcknowledgementId() {
        return acknowledgementId;
    }



    public void setAcknowledgementId(String acknowledgementId) {
        this.acknowledgementId = acknowledgementId;
    }
    
    

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }



    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }



    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }



    protected void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }



    public String getSupplierId() {
        return supplierId;
    }



    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }



    public String getServiceCode() {
        return serviceCode;
    }



    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }



    public Date getAcknowledgedTime() {
        return acknowledgedTime;
    }



    public void setAcknowledgedTime(Date acknowledgedTime) {
        this.acknowledgedTime = acknowledgedTime;
    }



    public Date getStartTime() {
        return startTime;
    }



    public void setStartTime(Date startTimestamp) {
        this.startTime = startTimestamp;
    }



    public Date getEndTime() {
        return endTime;
    }



    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public boolean removeItemEntity(ItemEntity itemEntity) {
        if (this.equals(itemEntity.getEvent())) {
            itemEntity.setEvent(null);
            return itemEntities.remove(itemEntity);
        }
        return false;
    }

    public boolean addItemEntity(ItemEntity itemEntity) {
        if (itemEntity.getEvent() == null) {
            itemEntity.setEvent(this);
            return itemEntities.add(itemEntity);
        }
        return false;
    }
    
    public List<ItemEntity> getItemEntities() {
        return Collections.unmodifiableList(itemEntities);
    }

    public InvoiceDataEntity getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(InvoiceDataEntity invoiceData) {
        this.invoiceData = invoiceData;
    }
    
    public String getPaymentResponsible() {
        return paymentResponsible;
    }

    public void setPaymentResponsible(String paymentResponsible) {
        this.paymentResponsible = paymentResponsible;
    }

    public String getHealthCareCommission() {
        return healthCareCommission;
    }

    public void setHealthCareCommission(String healthCareCommission) {
        this.healthCareCommission = healthCareCommission;
    }

    public boolean isPending() {
        return (pending == Boolean.TRUE);
    }

    protected void setPending(Boolean pending) {
        this.pending = pending;
    }
 
    /**
     * Returns the total amount for all items.
     * 
     * @return the total amount for all items.
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = BigDecimal.valueOf(0.0);
        for (final ItemEntity itemEntity : itemEntities) {
           amount = amount.add(itemEntity.getPrice().multiply(itemEntity.getQty())); 
        }
        return amount;
    }

    @Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        final Long id = getId();
        if (id != null && r instanceof BusinessEventEntity) {
            return id.equals(((BusinessEventEntity)r).getId());
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