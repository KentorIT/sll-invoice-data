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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.getinvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;

/**
 * @author muqkha
 * 
 */
public class GetInvoiceDataProducer extends AbstractProducer implements
		GetInvoiceDataResponderInterface {

	private static final Logger log = LoggerFactory
			.getLogger(GetInvoiceDataProducer.class);

	@Autowired
	private InvoiceDataService invoiceDataService;

    @Override
    public GetInvoiceDataResponse getInvoiceData(String logicalAddress,
            GetInvoiceDataRequest request) {
        log("getInvoiceData");
        log.info("logicalAddress: {}", logicalAddress);

        final ObjectFactory oFactory = new ObjectFactory();
        final GetInvoiceDataResponse response = oFactory
                .createGetInvoiceDataResponse();
        final ResultCode rc = new ResultCode();
        
        try {
            //Fetching unprocessed events with price
            List<BusinessEventEntity> bEEntityList = invoiceDataService
                    .getAllUnprocessedBusinessEvents(request.getSupplierId(),
                            request.getPaymentResponsible());
            response.getRegisteredEventList().addAll(fromBEntity(bEEntityList));
            
            //Fetching invoiced data
            List<InvoiceDataEntity> iDEntityList = invoiceDataService
                    .getAllInvoicedData(request.getSupplierId(), request.getPaymentResponsible());
            response.getInvoiceDataList().addAll(fromIEntity(iDEntityList));
            rc.setCode(ResultCodeEnum.OK);

        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnum.ERROR);
            rc.setMessage(ex.getMessage());
            log.error(ex.getMessage());
        }
        response.setResultCode(rc);
        return response;
    }

}
