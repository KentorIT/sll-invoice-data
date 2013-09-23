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

package se.sll.invoicedata.app.ws;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;

/**
 * Registers new business events.
 * 
 * @author Peter
 */
public class RegisterInvoiceDataProducer extends AbstractProducer implements RegisterInvoiceDataResponderInterface {

    private static final Logger log = LoggerFactory.getLogger(RegisterInvoiceDataProducer.class);
            
    @Autowired
    private InvoiceDataService invoiceDataService;
    
    @Override
    public RegisterInvoiceDataResponse registerInvoiceData(
            String logicalAddress, Event parameters) {
        log("registerInvoiceData");
        
        log.info("logicalAdress: {}", logicalAddress);

        final ObjectFactory f = new ObjectFactory();
        final RegisterInvoiceDataResponse ur = f.createRegisterInvoiceDataResponse();

        final ResultCode rc = new ResultCode();

        try {           
            invoiceDataService.registerBusinessEvent(toEntity(parameters));
            rc.setCode(ResultCodeEnum.OK);
            log.info("OK");
        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnum.ERROR);
            rc.setMessage(ex.getMessage());
            log.error(ex.getMessage());
        }

        ur.setResultCode(rc);
        
        return ur;
    }
    
}
