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

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;

/**
 * Business event repository functions.
 * 
 * @author Peter
 *
 */
public interface BusinessEventRepository extends JpaRepository<BusinessEventEntity, Long> {
    
    /**
     * Returns all pending events for a particular supplier.
     * 
     * @param supplierId the supplier id.
     * @return the list of pending/unprocessed events.
     */
    List<BusinessEventEntity> findBySupplierIdAndPendingIsTrue(String supplierId);
    
  
    /**
     * Returns a pending, non-credit event by eventId. <p>
     * 
     * Please note: Maximum one (1) only event is expected to match the criteria.
     * 
     * @param eventId the event id.
     * @return the event or an empty list when none matches the criteria.
     */
    BusinessEventEntity findByEventIdAndPendingIsTrueAndCreditIsNull(String eventId);


    /**
     * Returns a non-pending, non-credit, non-credited event by eventId. <p>
     *
     * Please note: Maximum one (1) only event is expected to match the criteria.
     *   
     * @param eventId the event id.
     * @return the event or an empty list when none matches the criteria.
     */
    BusinessEventEntity findByEventIdAndPendingIsNullAndCreditedIsNullAndCreditIsNull(String eventId);

    /**
     * Returns all pending events for a particular supplier.
     * 
     * @param supplierId the supplier id.
     * @param eventIdList the list of event ids.
     * @return the list of pending/unprocessed events.
     */
    List<BusinessEventEntity> findBySupplierIdAndEventIdInAndPendingIsTrue(String supplierId, List<String> eventIdList);
    
    /**
     * Returns all pending events for a particular supplier and payment responsible.
     * 
     * @param supplierId the suppler id.
     * @param paymentResponsible the payment responsible.
     * @return the list of event, might be empty when no events matches the criteria.
     */
    List<BusinessEventEntity> findBySupplierIdAndPaymentResponsibleAndPendingIsTrue(String supplierId, String paymentResponsible);
    
    /**
     * Returns entities matching an acknowledgment id.
     * @param acknowledgementId the id.
     * @return the matching entity.
     */
    List<BusinessEventEntity> findByAcknowledgementIdInAndPendingIsTrue(List<String> acknowledgementId);
}
