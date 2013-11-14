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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.NaturalId;


@Entity(name="invoice_data_pricelist")
public class PriceListEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NaturalId(mutable=false)
    @Column(name="supplier_id", length=64, nullable=false, updatable=false)
    private String supplierId;

    @NaturalId(mutable=false)
    @Column(name="service_code", length=64, nullable=false, updatable=false)
    private String serviceCode;
    
    @NaturalId(mutable=false)
    @Temporal(TemporalType.DATE)
    @Column(name = "valid_from", nullable=false, updatable=false)
    private Date validFrom;

    @Column(name="supplier_name", length=64, nullable=true, updatable=true)
    private String supplierName;


    @OneToMany(fetch=FetchType.EAGER, mappedBy="priceList", orphanRemoval=true, cascade=CascadeType.ALL)    
    private List<PriceEntity> priceEntities = new LinkedList<PriceEntity>();

    public Long getId() {
        return id;
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

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public boolean addPriceEntity(PriceEntity priceEntity) {
        if (priceEntity.getPriceList() == null) {
            priceEntity.setPriceList(this);
            return priceEntities.add(priceEntity);
        }
        return false;
    }
    
    public boolean removePriceEntity(PriceEntity priceEntity) {
        if (this.equals(priceEntity.getPriceList())) {
            priceEntity.setPriceList(null);
            return priceEntities.remove(priceEntity);
        }
        return false;
    }
    
    public List<PriceEntity> getPriceEntities() {
        return Collections.unmodifiableList(priceEntities);
    }
    
    public void clearPriceEntities() {
        priceEntities.clear();
    }
    
    @Override
    public boolean equals(Object r) {
        if (this == r) {
            return true;
        }
        final Long id = getId();
        if (id != null && r instanceof PriceListEntity) {
            return id.equals(((PriceListEntity)r).getId());
        }
        return false;
    }
 
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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
