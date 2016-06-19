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

package se.sll.invoicedata.core.model.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;

/**
 * Invoice data repository functions.
 * Carries out DAO operations on InvoiceDataEntity
 *  
 * @see InvoiceDataEntity
 * @author Peter 
 */
public interface InvoiceDataRepository extends JpaRepository<InvoiceDataEntity, Long> {
    
	/**
	 * Used to find GetPendingInvoices
	 * @return
	 */
	List<InvoiceDataEntity> findByPendingIsTrue();
	
	/**
	 * Removing old events if configured
	 * @param maxDate
	 * @return
	 */
	List<InvoiceDataEntity> findByEndDateLessThan(Date maxDate);
	
	/**
	 * Used only in tests
	 * @param supplierId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceDataEntity> findBySupplierIdAndStartDateBetween(String supplierId, Date startDate, Date endDate);
	
	/**
	 * Lists invoice data
	 * @param supplierId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceDataEntity> findBySupplierIdAndPendingIsNullAndStartDateBetween(String supplierId, Date startDate, Date endDate);
	
	/**
	 * Lists invoice data
	 * @param paymentResponsible
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<InvoiceDataEntity> findByPaymentResponsibleAndPendingIsNullAndStartDateBetween(String paymentResponsible, Date startDate, Date endDate);
	
	/**
	 * Used when registering event
	 * @param supplierId
	 * @param paymentResponsible
	 * @param costCenter
	 * @return
	 */
	List<InvoiceDataEntity> findBySupplierIdAndPaymentResponsibleAndCostCenterAndPendingIsTrue(String supplierId, String paymentResponsible, String costCenter);
}
