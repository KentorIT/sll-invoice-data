/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="invoiceData", orphanRemoval=false, cascade=CascadeType.ALL)    
    private List<BusinessEventEntity> businessEventEntities = new LinkedList<BusinessEventEntity>();


    @PrePersist
    void onPrePerist() {
        setCreatedTime(new Date());
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

    protected void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * Returns if a business entity has been removed from this invoice data. <p>
     * 
     * In order to be removed the business event entity must have been previously added to this
     * invoice data object.
     * 
     * @param businessEventEntity the entity to add.
     * @return true if removed, otherwise false.
     */
    public boolean removeBusinessEventEntity(BusinessEventEntity businessEventEntity) {
        if (this.equals(businessEventEntity.getInvoiceData())) {
            businessEventEntity.setInvoiceData(null);
            return businessEventEntities.remove(businessEventEntity);
        }
        return false;
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
            return businessEventEntities.add(businessEventEntity);
        }
        return false;
    }

    public List<BusinessEventEntity> getBusinessEventEntities() {
        return Collections.unmodifiableList(businessEventEntities);
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
        BigDecimal amount = BigDecimal.valueOf(0.0);
        for (final BusinessEventEntity businessEventEntity : businessEventEntities) {
            if (businessEventEntity.isCredit()) {
                amount = amount.subtract(businessEventEntity.getTotalAmount());
            } else {
                amount = amount.add(businessEventEntity.getTotalAmount()); 
            }
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
            return id.equals(((BusinessEventEntity)r).getEventId());
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
