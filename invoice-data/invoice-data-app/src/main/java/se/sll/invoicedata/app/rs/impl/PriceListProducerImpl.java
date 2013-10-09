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
