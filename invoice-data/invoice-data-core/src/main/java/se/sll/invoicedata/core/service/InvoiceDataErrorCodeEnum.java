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

package se.sll.invoicedata.core.service;

/**
 * Keeps error codes and message formats.
 * 
 * @author Peter
 */
public enum InvoiceDataErrorCodeEnum {
    TECHNICAL_ERROR("1001", "Encountered a technical error: %s"),
    VALIDATION_ERROR("1002", "Invalid or missing input data: %s"),
    NOTFOUND_ERROR("1003", "No such %s found: %s");
    
    private final String code;
    private final String messageFormat;
    
    //
    private InvoiceDataErrorCodeEnum(String code, String messageFormat) {
        this.code = code;
        this.messageFormat = messageFormat;
    }

    /**
     * Returns the error code.
     * 
     * @return the error code.
     */
    public String getCode() {
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
        
        return new InvoiceDataServiceException(String.format("%s: %s", getCode(), message));
        
    }
    
}
