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

import se.sll.invoicedata.core.model.entity.PriceListEntity;

/**
 * Price list repository functions.
 * 
 * @author Peter
 */
public interface PriceListRepository extends JpaRepository<PriceListEntity, Long> {
    
    /**
     * Returns price lists.
     * 
     * @param supplierId the supplier id.
     * @param serviceCode the service code.
     * @param actual actual time for the event.
     * @return matching price lists.
     */
    List<PriceListEntity> findBySupplierIdAndServiceCodeAndValidFromLessThanEqualOrderByValidFromDesc(String supplierId, String serviceCode, Date actual);
    
    /**
     * 
     * @param supplierId
     * @param serviceCode
     * @param valdiFrom
     * @return
     */
    PriceListEntity findBySupplierIdAndServiceCodeAndValidFrom(String supplierId, String serviceCode, Date valdiFrom);
    
}
