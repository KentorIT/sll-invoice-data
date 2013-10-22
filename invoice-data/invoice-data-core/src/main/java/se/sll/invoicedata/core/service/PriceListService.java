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

public interface PriceListService {

    List<PriceList> getPriceLists();

    List<ServiceResponse>  savePriceLists(List<PriceList> priceLists);

    ServiceResponse deletePriceList(Long id);

    ServiceResponse savePriceList(PriceList priceList);

    PriceList getPriceList(Long id);
    
}
