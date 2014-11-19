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
import javax.persistence.Table;

/**
 * @author muqkha
 *
 */
@Entity
@Table(name = "hsa_supplier_mapping")
public class HSAToSupplierMappingEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="hsaId", length=64, nullable=false, updatable=false, unique=true)
    private String hsaId;
	
	@Column(name="supplier_id_config", nullable=false, updatable=true)
    private String supplierIdConfig;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHSAId() {
		return hsaId;
	}

	public void setHSAId(String hsaId) {
		this.hsaId = hsaId;
	}

	public String getSupplierIdConfig() {
		return supplierIdConfig;
	}

	public void setSupplierIdConfig(String supplierIdConfig) {
		this.supplierIdConfig = supplierIdConfig;
	}
	
}
