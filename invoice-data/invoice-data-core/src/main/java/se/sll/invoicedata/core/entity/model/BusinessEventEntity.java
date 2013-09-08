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

package se.sll.invoicedata.core.entity.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name="id_business_entity")
public class BusinessEventEntity {
    @Id
    @Column(name="id", length=64)
    private String id;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time", nullable = false, updatable = false)
    private Date createdTime;
    
    @Column(name="supplier_name", length=256, nullable=false, updatable=false)
    private String supplierName;
    
    @Column(name="signed_by", length=64, nullable=false, updatable=false)
    private String signedBy;
    
    

    @PrePersist
    void onPrePerist() {
        setCreatedTime(new Date());
    }



    public String getId() {
        return id;
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



    public Date getCreatedTime() {
        return createdTime;
    }



    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

}