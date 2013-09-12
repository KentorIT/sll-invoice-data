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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="invoice_data_item")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;
    
    @Column(name="item_id", length=64, nullable=false, updatable=false)
    private String itemId;

    @Column(name="description", length=256, nullable=false, updatable=false)
    private String description;

    @Column(name="qty", updatable=false)
    private float qty;
    
    @ManyToOne(optional=false)
    @JoinColumn(name="event_id")
    private BusinessEventEntity event;

    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public BusinessEventEntity getEvent() {
        return event;
    }

    public void setEvent(BusinessEventEntity event) {
        this.event = event;
    }
    
}