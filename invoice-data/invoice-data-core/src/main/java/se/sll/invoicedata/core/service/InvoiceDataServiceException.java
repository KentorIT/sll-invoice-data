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

/**
 * Invoice data service layer exception.
 * 
 * @author Peter
 */
public class InvoiceDataServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;   

    private InvoiceDataErrorCodeEnum code;
    /**
     * Creates an exception.
     * 
     * @param message the user message in plain text.
     */
    protected InvoiceDataServiceException(InvoiceDataErrorCodeEnum code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * Returns the causing code.
     * 
     * @return the code.
     */
    public InvoiceDataErrorCodeEnum getCode() {
        return this.code;
    }

}
