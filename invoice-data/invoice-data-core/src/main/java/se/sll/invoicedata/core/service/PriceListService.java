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

package se.sll.invoicedata.core.service;

import java.util.List;

import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;

/**
 * Manages price-lists.
 * 
 * @author Peter
 *
 */
public interface PriceListService {

    /**
     * Returns all price lists.
     * 
     * @return all price-lists.
     */
    List<PriceList> getPriceLists();

    /**
     * Saves a list of price-lists.
     * 
     * @param priceLists the list.
     * @return the response.
     */
    List<ServiceResponse>  savePriceLists(List<PriceList> priceLists);

    /**
     * Deletes a price-list identified by the internal database id.
     * 
     * @param id the id.
     * @return the response.
     */
    ServiceResponse deletePriceList(Long id);

    /**
     * Saves a price-list.
     * 
     * @param priceList the price-list to save.
     * @return the response.
     */
    ServiceResponse savePriceList(PriceList priceList);

    /**
     * Returns a single price-list by the internal database id.
     * 
     * @param id the id.
     * @return the price-list, or null if none found.
     */
    PriceList getPriceList(Long id);
    
}
