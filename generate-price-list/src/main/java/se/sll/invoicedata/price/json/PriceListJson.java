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

/**
 * 
 */
package se.sll.invoicedata.price.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muqkha
 *
 */
public class PriceListJson {
	
	private List<Service> priceList = new ArrayList<Service>();

	public List<Service> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Service> priceList) {
		this.priceList = priceList;
	}
	
	public void add(Service service) {
		priceList.add(service);
	}

}
