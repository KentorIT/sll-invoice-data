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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Placeholder
 *
 * @author Peter
 */
@Entity
@Table(name="invoice_data_event")
public class BusinessEventEntity {
    @Id
    @Column(name="event_id", length=64)
    private String id;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private Date createdTimestamp;
    
    @Column(name="supplier_id", length=64, nullable=false, updatable=false)
    private String supplierId;
    
    @Column(name="supplier_name", length=256, nullable=false, updatable=false)
    private String supplierName;

    @Column(name="service_code", length=64, nullable=false, updatable=false)
    private String serviceCode;
    
    @Column(name="signed_by", length=64, nullable=false, updatable=false)
    private String signedBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "signed_timestamp", nullable=false, updatable=false)
    private Date signedTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_timestamp", nullable=false, updatable=false)
    private Date startTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_timestamp", nullable=false, updatable=false)
    private Date endTimestamp;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)    
    private List<ItemEntity> itemEntities = new LinkedList<ItemEntity>();
    


    @PrePersist
    void onPrePerist() {
        setCreatedTimestamp(new Date());
    }



    public String getId() {
        return id;
    }

    
    @Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        if (r instanceof BusinessEventEntity) {
            return getId().equals(((BusinessEventEntity)r).getId());
        }
        return false;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getSupplierName() {
        return supplierName;
    }



    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }



    public String getSignedBy() {
        return signedBy;
    }



    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }



    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }



    public void setCreatedTimestamp(Date createdTimestamp) {
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



    public Date getSignedTimestamp() {
        return signedTimestamp;
    }



    public void setSignedTimestamp(Date signedTimestamp) {
        this.signedTimestamp = signedTimestamp;
    }



    public Date getStartTimestamp() {
        return startTimestamp;
    }



    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }



    public Date getEndTimestamp() {
        return endTimestamp;
    }



    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
    }


    public boolean removeItemEntity(ItemEntity itemEntity) {
        if (this.equals(itemEntity.getEvent())) {
            itemEntity.setEvent(null);
            return itemEntities.remove(itemEntity);
        }
        return false;
    }

    public boolean addItemEntity(ItemEntity itemEntity) {
        itemEntity.setEvent(this);
        return itemEntities.add(itemEntity);
    }
    
    public List<ItemEntity> getItemEntities() {
        return Collections.unmodifiableList(itemEntities);
    }


}