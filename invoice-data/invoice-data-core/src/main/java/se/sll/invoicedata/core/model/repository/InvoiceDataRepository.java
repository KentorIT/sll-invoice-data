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
	 * Returns InvoiceData with matching supplierId within date range!
     * 
	 * @param supplierId the supplier id.
	 * @param fromDate can be null so 1970 01 01 is set as from date
	 * @param toDate can also be null then the max valid date i.e 31 December 9999 is set
	 * @return List<InvoiceDataEntity> the resulting list, might be empty.
     */
	/*
	@Query("FROM InvoiceDataEntity WHERE supplierId = :supplierId "
            + "AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate))")
	List<InvoiceDataEntity> getInvoiceDataBySupplierIdBetweenDates(@Param("supplierId") String supplierId, 
            @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);*/
	
	/**
	 * Returns InvoiceData with matching paymentResponsible within date range!
     * 
	 * @param paymentResponsible the payment responsible.
	 * @param fromDate can be null so 1970 01 01 is set as from date
	 * @param toDate can also be null then the max valid date i.e 31 December 9999 is set
     * @return List<InvoiceDataEntity> the resulting list, might be empty.
	 */
	/*
	@Query("FROM InvoiceDataEntity WHERE paymentResponsible = :paymentResponsible "
            + "AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate))")
	List<InvoiceDataEntity> getInvoiceDataByPaymentResponsibleBetweenDates(@Param("paymentResponsible") String paymentResponsible, 
            @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);*/

	/**
	 * Returns InvoiceData with matching supplierId and payment responsible within date range!
     * 
     * @param supplierId the supplier id.
	 * @param paymentResponsible the payment responsible.
	 * @param fromDate can be null so 1970 01 01 is set as from date
	 * @param toDate can also be null then the max valid date i.e 31 December 9999 is set
     * @return List<InvoiceDataEntity> the resulting list, might be empty.
	 */
	/*
	@Query("FROM InvoiceDataEntity WHERE supplierId = :supplierId AND paymentResponsible = :paymentResponsible "
            + "AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate))")
	List<InvoiceDataEntity> getInvoiceDataBySupplierIdAndPaymentResponsibleBetweenDates(@Param("supplierId") String supplierId,
            @Param("paymentResponsible") String paymentResponsible, 
            @Param("fromDate") Date fromDate, 
            @Param("toDate") Date toDate);*/

	
	List<InvoiceDataEntity> findByPendingIsTrue();
	
	List<InvoiceDataEntity> findByEndDateLessThan(Date maxDate);
	
	List<InvoiceDataEntity> findBySupplierIdAndStartDateBetween(String supplierId, Date startDate, Date endDate);
	
	List<InvoiceDataEntity> findBySupplierIdAndPendingIsFalseAndStartDateBetween(String supplierId, Date startDate, Date endDate);
	
	List<InvoiceDataEntity> findByPaymentResponsibleAndPendingIsFalseAndStartDateBetween(String supplierId, Date startDate, Date endDate);
	
	List<InvoiceDataEntity> findBySupplierIdAndPaymentResponsibleAndCostCenterAndPendingIsTrue(String supplierId, String paymentResponsible, String costCenter);
}
