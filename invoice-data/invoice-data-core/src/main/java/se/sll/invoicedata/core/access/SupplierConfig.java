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

package se.sll.invoicedata.core.access;

import java.util.Arrays;
import java.util.List;


public class SupplierConfig {
	
	private boolean hasSupplierFullAccess;
	
	private List<String> supplierList;
	
	private void setFullAccessForSupplier() {
		this.hasSupplierFullAccess = true;
	}
	
	public boolean hasSupplierAccess(String supplierId) {
		return hasSupplierFullAccess || supplierList.contains(supplierId);
	}
	
	@SuppressWarnings("unused")
	private SupplierConfig() {
		super();
	}
	
	public SupplierConfig(String supplierIdConfig) {
		if (hasFullAccess(supplierIdConfig)) {
			setFullAccessForSupplier();
		} else {
			supplierList = Arrays.asList(org.apache.commons.lang3.StringUtils.split(supplierIdConfig, ','));
		}
	}
	
	private boolean hasFullAccess(String config) {
		return config.equals("*");
	}
	
}
