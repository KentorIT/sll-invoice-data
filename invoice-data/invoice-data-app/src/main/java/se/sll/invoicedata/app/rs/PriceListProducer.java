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

import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;

/**
 * Admin REST services to manage price lists.
 * 
 * @author Peter
 *
 */
@Path("/")
public interface PriceListProducer {  
    /**
     * Returns all price lists.
     *  
     * @return all price lists, or emoty if none exists.
     */
    @GET
    @Produces("application/json")
    @Path("/pricelists")
    List<PriceList> getPriceLists();

    /**
     * Updates a list/array of price lists.
     * 
     * @param priceLists the price lists to be updated.
     * @return a service response on success.
     */
    @POST
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/pricelists")
    List<ServiceResponse> putPriceLists(final List<PriceList> priceLists);

    /**
     * Deletes a price list.
     * 
     * @param id the internal id of the actual price list to delete.
     * @return a service response on success.
     */
    @DELETE
    @Path("/pricelists/{id}")
    @Produces("application/json")
    public Response deletePriceList(@PathParam(value = "id") Long id);

    /**
     * Returns a single price list by an internal id.
     * 
     * @param id the id, previously returned by getter methods.
     * @return a service response with the entity set to the actual price list on success, 
     * or a response indicating not found when no such price list exists..
     */
    @GET
    @Path("/pricelists/{id}")
    @Produces("application/json")
    public Response getPriceList(@PathParam(value = "id") Long id);

    /**
     * Updates a single price list.
     * 
     * @param priceList the price list to update.
     * @return a service response on success.
     */
    @POST
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/pricelist")
    public Response putPriceList(final PriceList priceList);
}
