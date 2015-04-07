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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author muqkha
 *
 */
@Entity
@Table(name = "invoice_data_event_reference_item")
public class ReferenceItemEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="reference_item_id", nullable=false, updatable=false)
    private String refItemId;
	
	@Column(name="qty", nullable=false, updatable=false)
    private int qty;
	
	@ManyToOne(optional=false)
    @JoinColumn(name="discount_item_id", updatable=false)
    private DiscountItemEntity discountItemEntity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRefItemId() {
		return refItemId;
	}

	public void setRefItemId(String refItemId) {
		this.refItemId = refItemId;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public DiscountItemEntity getDiscountItemEntity() {
		return discountItemEntity;
	}

	public void setDiscountItemEntity(DiscountItemEntity discountItemEntity) {
		this.discountItemEntity = discountItemEntity;
	}

	@Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        final Long id = getId();
        if (id != null && r instanceof ReferenceItemEntity) {
            return id.equals(((ReferenceItemEntity)r).getId());
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
