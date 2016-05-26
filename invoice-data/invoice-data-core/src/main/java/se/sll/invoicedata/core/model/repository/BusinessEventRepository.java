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
     * Used in migrating event from version 1.4 to 2.0 
     * In 2.0 draft invoice is created and all events are connected to an event
     * @return
     */
    List<BusinessEventEntity> findByPendingIsTrueAndInvoiceDataIsNull();
}
