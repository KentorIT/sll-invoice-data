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
 * Keeps error codes and message formats.
 * 
 * @author Peter
 */
public enum InvoiceDataErrorCodeEnum {
	
    TECHNICAL_ERROR(1001, "Encountered a technical error: %s"),
    VALIDATION_ERROR(1002, "Invalid or missing input data: %s"),
    NOTFOUND_ERROR(1003, "No such %s found: %s"),
    LIMIT_ERROR(1004, "Reached maximum limit %d: %s"),
    SERVICE_AUTHORIZATION_ERROR(1005, "Requesting system has no access to the given operation: %s"),
    SUPPLIER_AUTHORIZATION_ERROR(1006, "Requesting supplier has no access to the given operation: %s"),
    EXPECTING_CREATE_INVOICE_REQUEST_BEFORE_FETCHING(1007, "Expecting Create Invoice Data request for this supplier, payment responsible and cost center: %s"),
    ILLEGAL_STATE_DUPLICATE_DRAFT_VERSIONS_OF_INVOICES(1008, "Illegal state, system has two draft versions of Invoices for given consumer: %s"),
    NO_PENDING_INVOICES_TO_BE_GENERATED(1009, "System has no pending invoices on this supplier, payment responsible and costcenter. Register an event first: %s"),
	ILLEGAL_STATE_NO_REGISTERED_EVENTS_ON_THIS_INVOICE(1010, "Illegal state, system has not registered any events for this supplier, payment responsible and costcenter: %s"),
    ILLEGAL_STATE_EVENT_ID_OR_EVENT_CANNOT_BE_NULL(1011, "Illegal state, system cannot handle a null event or eventId at this stage: %s"),
    ILLEGAL_STATE_INVALID_INVOICEDATA_REFERENCE_ID(1012, "Illegal state, invalid invoicedata referenceId: %s");
    
    private final int code;
    private final String messageFormat;
    
    private InvoiceDataErrorCodeEnum(int code, String messageFormat) {
        this.code = code;
        this.messageFormat = messageFormat;
    }

    /**
     * Returns the error code.
     * 
     * @return the error code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the message format string.
     * 
     * @return the message format.
     */
    public String getMessageFormat() {
        return messageFormat;
    }
    
    /**
     * Returns an exception.
     * 
     * @param args the message format arguments.
     * @return the corresponding exception.
     */
    public InvoiceDataServiceException createException(Object... args) {
        final String message = String.format(getMessageFormat(), args);
        
        return new InvoiceDataServiceException(this, String.format("%s", message));
        
    }
    
}
