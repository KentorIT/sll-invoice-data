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

import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponseType;
import riv.sll.invoicedata.registerinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataType;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnumType;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
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
    public RegisterInvoiceDataResponseType registerInvoiceData(String logicalAddress, RegisterInvoiceDataType registerInvoiceDataType) {
        log("registerInvoiceData");
        
        log.info("logicalAdress: {}", logicalAddress);

        final ObjectFactory f = new ObjectFactory();
    	final RegisterInvoiceDataResponseType ur = f.createRegisterInvoiceDataResponseType();

        final ResultCode rc = new ResultCode();

        try {
            invoiceDataService.registerBusinessEvent(toEntity(registerInvoiceDataType.getEvent()));
            rc.setCode(ResultCodeEnumType.OK);
            log.info("OK");
        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnumType.ERROR);
            rc.setMessage(ex.getMessage());
            log.error(ex.getMessage());
        }

        ur.setResultCode(rc);
    	
    	return ur;
    	
    }

    /**
     * Maps XML JAXB object to an entity bean.
     * 
     * @param event the JAXB object.
     * @return the entity bean.
     */
    static BusinessEventEntity toEntity(final Event event) {
        final BusinessEventEntity entity = new BusinessEventEntity();
        
        entity.setId(event.getEventId());
        entity.setSupplierName(event.getSupplierName());
        entity.setSupplierId(event.getSupplierId());
        entity.setServiceCode(event.getServiceCode());
        entity.setStartTimestamp(event.getStartTimestamp().toGregorianCalendar().getTime());
        entity.setEndTimestamp(event.getEndTimestamp().toGregorianCalendar().getTime());
        entity.setSignedTimestamp(event.getSignedTimestamp().toGregorianCalendar().getTime());
        entity.setSignedBy(event.getSignedBy());
        
        for (final Item item : event.getItems().getItem()) {
            final ItemEntity itemEntity = new ItemEntity();
            
            itemEntity.setItemId(item.getItemId());;
            itemEntity.setQty(item.getQty().floatValue());
            itemEntity.setDescription(item.getDescription());
     
            entity.addItemEntity(itemEntity);
        }
        
        return entity;
    }
}
