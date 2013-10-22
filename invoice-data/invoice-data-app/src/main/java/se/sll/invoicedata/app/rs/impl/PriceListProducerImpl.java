/**
 * Copyright (c) 2013 SLL, <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice Data is free software: you can redistribute it and/or modify
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

package se.sll.invoicedata.app.rs.impl;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import se.sll.invoicedata.app.rs.PriceListProducer;
import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;

/**
 * Implements admin REST services to handle price lists.
 * 
 * @author Peter
 *
 */
public class PriceListProducerImpl implements PriceListProducer {
    @Autowired
    private PriceListService priceListService;


    @Override
    public List<PriceList> getPriceLists() {
        return priceListService.getPriceLists();
    }

    @Override
    public List<ServiceResponse> putPriceLists(final List<PriceList> priceLists) {
        return priceListService.savePriceLists(priceLists);
    }

    @Override
    public Response deletePriceList(Long id) {
        return Response.ok(priceListService.deletePriceList(id)).build();
    }

    @Override
    public Response getPriceList(Long id) {
        final PriceList priceList = priceListService.getPriceList(id);
        return (priceList == null) ? Response.status(Response.Status.NOT_FOUND).build(): Response.ok(priceList).build();
    }

    @Override
    public Response putPriceList(final PriceList priceList) {
        return Response.ok(priceListService.savePriceList(priceList)).build();
    }
}
