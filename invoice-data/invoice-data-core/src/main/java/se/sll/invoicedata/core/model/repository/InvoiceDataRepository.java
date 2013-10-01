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
	 * 
	 * @param id
	 * @return
	 */
	InvoiceDataEntity findById(Long id);
	
    /**
     * Returns all invoice data entities for a particular supplier.
     * 
     * @param supplierId the supplier id.
     * @return the list of invoice data entities.
     */
    List<InvoiceDataEntity> findBySupplierId(String supplierId);
    
    /**
     * Returns all invoiced data for a particular suppier and payee
     * @param supplierId
     * @param paymentResponsible
     * @return
     */
    List<InvoiceDataEntity> findBySupplierIdAndPaymentResponsible(String supplierId, String paymentResponsible);
}
