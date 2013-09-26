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

/**
 * 
 */
package se.sll.invoicedata.app.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataResponse;
import riv.sll.invoicedata.createinvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;

/**
 * @author muqkha
 *
 */
public class CreateInvoiceDataProducer extends AbstractProducer implements CreateInvoiceDataResponderInterface {

	private static final Logger log = LoggerFactory.getLogger(CreateInvoiceDataProducer.class);
	
    @Autowired
    private InvoiceDataService invoiceDataService;

    @Override
    public CreateInvoiceDataResponse createInvoiceData(String logicalAddress,
            CreateInvoiceDataRequest parameters) {
        log("createInvoiceData");
        log.info("logicalAddress: {}", logicalAddress);
        
        final ObjectFactory oFactory = new ObjectFactory();
        final ResultCode rc = new ResultCode();
        
        try {
            invoiceDataService.createInvoiceData(parameters.getSupplierId());
            rc.setCode(ResultCodeEnum.OK);
        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnum.ERROR);
            rc.setMessage(ex.getMessage());
            log.error(ex.getMessage());
        }
        
        CreateInvoiceDataResponse createInvoiceDataResp = oFactory.createCreateInvoiceDataResponse();
        createInvoiceDataResp.setResultCode(rc);
        
        return createInvoiceDataResp;        
    }

}
