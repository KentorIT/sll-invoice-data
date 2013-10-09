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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.NaturalId;

@Entity(name="invoice_data_pricelist_item")
public class PriceEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NaturalId
    @ManyToOne(optional=false)
    @JoinColumn(name="price_list_id", updatable=false)
    private PriceListEntity priceList;
    
    @NaturalId
    @Column(name="item_id", length=64, nullable=false, updatable=false)
    private String itemId;

    @Column(name="price", precision=8, scale=2, nullable=false, updatable=false)
    private BigDecimal price;

    public Long getId() {
        return id;
    }

    public PriceListEntity getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceListEntity priceList) {
        this.priceList = priceList;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    

    @Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        final Long id = getId();
        if (id != null && r instanceof PriceEntity) {
            return id.equals(((PriceEntity)r).getId());
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
