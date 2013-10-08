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

package se.sll.invoicedata.app.rs;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;

@Path("/")
public class PriceListProducer {
    @Autowired
    private PriceListService priceListService;
    
    
    @GET
    @Produces("application/json")
    @Path("/pricelists")
    public List<PriceList> getPriceLists() {
        return priceListService.getPriceLists();
    }
    
    @POST
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/pricelists")
    public List<ServiceResponse> putPriceLists(final List<PriceList> priceLists) {
        return priceListService.savePriceLists(priceLists);
    }
    
    @DELETE
    @Path("/pricelists/{id}")
    @Produces("application/json")
    public Response deletePriceList(@PathParam(value = "id") Long id) {
        return Response.ok(priceListService.deletePriceList(id)).build();
    }

    @GET
    @Path("/pricelists/{id}")
    @Produces("application/json")
    public Response getPriceList(@PathParam(value = "id") Long id) {
        final PriceList priceList = priceListService.getPriceList(id);
        return (priceList == null) ? Response.status(Response.Status.NOT_FOUND).build(): Response.ok(priceList).build();
    }
   
    @POST
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/pricelist")
    public Response putPriceList(final PriceList priceList) {
        return Response.ok(priceListService.savePriceList(priceList)).build();
    }
    
}
