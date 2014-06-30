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

/**
 * 
 */
package se.sll.invoicedata.core.model.entity;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author muqkha
 * 
 */
@Entity
@Table(name = "invoice_data_event_discount_item")
public class DiscountItemEntity implements Comparable<DiscountItemEntity> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="description", length=256, nullable=false, updatable=false)
    private String description;

	@Column(name="discount_in_percentage", nullable=false, updatable=false)
    private int discountInPercentage;
	
	@Column(name="order_of_discount", nullable=false, updatable=false)
    private int orderOfDiscount;
    
    @ManyToOne(optional=false)
    @JoinColumn(name="event_id", updatable=false)
    private BusinessEventEntity event;
    
    @OneToMany(fetch=FetchType.EAGER, mappedBy="id", orphanRemoval=true, cascade=CascadeType.ALL)    
    private List<ReferenceItemEntity> referenceItemEntities = new LinkedList<ReferenceItemEntity>();
    
    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getOrderOfDiscount() {
		return orderOfDiscount;
	}

	public void setOrderOfDiscount(int orderOfDiscount) {
		this.orderOfDiscount = orderOfDiscount;
	}

	public int getDiscountInPercentage() {
		return discountInPercentage;
	}

	public void setDiscountInPercentage(int discountInPercentage) {
		this.discountInPercentage = discountInPercentage;
	}

	public BusinessEventEntity getEvent() {
		return event;
	}

	public void setEvent(BusinessEventEntity event) {
		this.event = event;
	}

	public List<ReferenceItemEntity> getReferenceItemEntities() {
		return referenceItemEntities;
	}

	public void setReferenceItemEntities(
			List<ReferenceItemEntity> referenceItemEntities) {
		this.referenceItemEntities = referenceItemEntities;
	}
	
	public boolean addReferenceItemEntity(ReferenceItemEntity referenceItemEntity) {
        if (referenceItemEntity.getDiscountItemEntity() == null) {
        	referenceItemEntity.setDiscountItemEntity(this);
            return referenceItemEntities.add(referenceItemEntity);
        }
        return false;
    }
	
	public BigDecimal getTotalAmount() {
		BigDecimal amount = BigDecimal.valueOf(0.0);		
		Map<String, ItemEntity> serviceItemMap = new HashMap<String, ItemEntity>();
        
        for (final ItemEntity itemEntity : event.getItemEntities()) {        	
       		serviceItemMap.put(itemEntity.getItemId(), itemEntity);
        }
        
		for (ReferenceItemEntity referenceItemEntity : getReferenceItemEntities()) {
    		ItemEntity itemEntity = serviceItemMap.get(referenceItemEntity.getRefItemId());
    		BigDecimal priceBeforeDiscount = itemEntity.getPrice().multiply(new BigDecimal(referenceItemEntity.getQty()));
    		BigDecimal priceAfterDiscount = (priceBeforeDiscount.multiply(new BigDecimal(this.getDiscountInPercentage()))).divide(new BigDecimal(100));
    		amount = amount.add(priceAfterDiscount);
    	}
		
		serviceItemMap.clear();
		return amount;
	}
	
	@Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        final Long id = getId();
        if (id != null && r instanceof DiscountItemEntity) {
            return id.equals(((DiscountItemEntity)r).getId());
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

	@Override
	public int compareTo(DiscountItemEntity discountItem) {
		return this.getOrderOfDiscount() - discountItem.getOrderOfDiscount();
	}
    
}
