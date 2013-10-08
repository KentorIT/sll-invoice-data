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

package se.sll.invoicedata.core.model.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;

/**
 * Invoice data repository functions.
 * 
 * @author Peter
 *
 */
public interface InvoiceDataRepository  extends JpaRepository<InvoiceDataEntity, Long> {
    
	    
    /**
     * Returns all invoiced data for a particular supplier and payment responsible.
     * 
     * @param supplierId the supplier id.
     * @param paymentResponsible the payment responsible
     * @return the list of invoice data, might be empty when none matches the criteria.
     */
    List<InvoiceDataEntity> findBySupplierIdAndPaymentResponsible(String supplierId, String paymentResponsible);
    
    @Query("FROM InvoiceDataEntity WHERE supplierId = :supplierId OR paymentResponsible = :paymentResponsible "
    		+ "OR createdTime BETWEEN :fromDate AND :toDate")
    List<InvoiceDataEntity> findBetweenDates(@Param("supplierId") String supplierId,
    		@Param("paymentResponsible") String paymentResponsible, 
    		@Param("fromDate") Date fromDate, 
    		@Param("toDate") Date toDate);
    
    @Query("FROM InvoiceDataEntity WHERE supplierId = :supplierId")
    List<InvoiceDataEntity> findBetweenDates(@Param("supplierId") String supplierId);
    
}
