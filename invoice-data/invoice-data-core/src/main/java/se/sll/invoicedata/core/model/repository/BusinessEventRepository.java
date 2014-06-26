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

import org.springframework.data.domain.Pageable;
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
     * Returns entities matching a list of acknowledgment identities.
     * 
     * @param acknowledgementId the list of acknowledgment identities.
     * @return the list of matching events, might be empty when none matches the acknowledgementId.
     */
    List<BusinessEventEntity> findByAcknowledgementIdInAndPendingIsTrue(List<String> acknowledgementId);
    
    /**
     * Returns entities for a supplier and where start time is within a period of time.
     * 
     * @param supplierId the supplier id.
     * @param startTime the period start time.
     * @param endTime the period end time.
     * @param pageable page info.
     * 
     * @return the list of matching events, might be empty when none matches the criteria.
     */
    List<BusinessEventEntity> findBySupplierIdAndPendingIsTrueAndStartTimeBetween(String supplierId, Date startTime, Date endTime, Pageable pageable);
    
    /**
     * Returns entities for a supplier and payment responsible, and where start time is within a period of time.
     * 
     * @param supplierId the supplier id.
     * @param paymentResponsible the payment responsible.
     * @param startTime the period start time.
     * @param endTime the period end time.
     * @param pageable page info.
     * 
     * @return the list of matching events, might be empty when none matches the criteria.
     */
    List<BusinessEventEntity> findBySupplierIdAndPendingIsTrueAndPaymentResponsibleAndStartTimeBetween(String supplierId, String paymentResponsible, Date startTime, Date endTime, Pageable pageable);
    
}
