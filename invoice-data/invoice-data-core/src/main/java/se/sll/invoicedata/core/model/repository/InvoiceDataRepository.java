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
 * 
 * @author Peter
 *
 */
public interface InvoiceDataRepository  extends JpaRepository<InvoiceDataEntity, Long> {
    
	/**
	 * Fetches InvoiceData with matching within date range!
     * Dates: fromDate date can be null so 1970 01 01 is taken as from date
     * toDate can also be empty then the current date + 100 years is taken 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	List<InvoiceDataEntity> findByCreatedTimeBetween(Date fromDate, Date toDate);
	
	/**
	 * Fetches InvoiceData with matching supplierId within date range!
     * Dates: fromDate date can be null so 1970 01 01 is taken as from date
     * toDate can also be empty then the current date + 100 years is taken
	 * @param supplierId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	List<InvoiceDataEntity> findBySupplierIdAndCreatedTimeBetween(String supplierId, Date fromDate, Date toDate);
	
	/**
	 * Fetches InvoiceData with matching paymentResponsible within date range!
     * Dates: fromDate date can be null so 1970 01 01 is taken as from date
     * toDate can also be empty then the current date + 100 years is taken
	 * @param supplierId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	List<InvoiceDataEntity> findByPaymentResponsibleAndCreatedTimeBetween(String paymentResponsible, Date fromDate, Date toDate);

	/**
	 * Fetches InvoiceData with matching supplierId And payment responsible within date range!
     * Dates: fromDate date can be null so 1970 01 01 is taken as from date
     * toDate can also be empty then the current date + 100 years is taken     * 
	 * @param supplierId
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	List<InvoiceDataEntity> findBySupplierIdAndPaymentResponsibleAndCreatedTimeBetween(String supplierId, String paymentResponsible, Date fromDate, Date toDate);
	
}
