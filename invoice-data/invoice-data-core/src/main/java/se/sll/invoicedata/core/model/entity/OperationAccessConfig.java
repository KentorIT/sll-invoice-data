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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import se.sll.invoicedata.core.access.Operation;

/**
 * @author muqkha
 *
 */
@Entity
@Table(name = "operation_access_config", 
uniqueConstraints= @UniqueConstraint(columnNames={"operation_enum", "hsa_id"}))
public class OperationAccessConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="operation_enum", nullable=false, updatable=true)
	@Enumerated(EnumType.STRING)
    private Operation operationEnum;
	
	@Column(name="hsa_id", nullable=false, updatable=true)
    private String hsaId;
	
	@Column(name="supplier_id_config", nullable=false, updatable=true)
    private String supplierIdConfig;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Operation getOperationEnum() {
		return operationEnum;
	}

	public void setOperationEnum(Operation operationEnum) {
		this.operationEnum = operationEnum;
	}

	public String getHsaId() {
		return hsaId;
	}

	public void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}

	public String getSupplierIdConfig() {
		return supplierIdConfig;
	}

	public void setSupplierIdConfig(String supplierIdConfig) {
		this.supplierIdConfig = supplierIdConfig;
	}
	
}
