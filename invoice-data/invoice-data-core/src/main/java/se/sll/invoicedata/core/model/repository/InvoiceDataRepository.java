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
