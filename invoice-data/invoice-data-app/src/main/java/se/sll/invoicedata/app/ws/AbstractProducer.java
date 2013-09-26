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

import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.app.AppUtil;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;

/**
 * Abstract class used by WS Producers
 *
 */
public abstract class AbstractProducer {

    private static final Logger logger = LoggerFactory.getLogger("ws-api");
    
    private static final String SERVICE_CONSUMER_HEADER_NAME = "x-rivta-original-serviceconsumer-hsaid";

    @Resource
    private WebServiceContext wsctx;
    
    protected void throwInternalServerError(Throwable t) {
        String errorText = "Internal server error (reference: " + UUID.randomUUID().toString() + ")";
        logger.error(errorText, t);
        throw new SoapFault(errorText, SoapFault.FAULT_CODE_SERVER);
    }

    /**
     *
     * @param msg
     */
    protected void log(String msg) {
        MessageContext mctx = (wsctx == null) ? null : wsctx.getMessageContext();
        if (mctx == null) {
            logger.error("MessageContext is null, WebServiceContext is {}", wsctx);
            return;
        }
        
        @SuppressWarnings (value="rawtypes")
        Map headers = (Map)mctx.get(MessageContext.HTTP_REQUEST_HEADERS);      
        Object serviceConsumer = headers.get(SERVICE_CONSUMER_HEADER_NAME);

        logger.info("{} invoked by HSAid: {}", msg, (serviceConsumer == null) ? "<NOT FOUND>" : serviceConsumer);
    }
    
    /**
     * Maps XML JAXB object to an entity bean.
     * 
     * @param event the JAXB object.
     * @return the entity bean.
     */
    static BusinessEventEntity toEntity(final Event event) {
        final BusinessEventEntity entity = copyProperties(new BusinessEventEntity(), event, Event.class);
        for (final Item item : event.getItemList()) {     
            entity.addItemEntity(copyProperties(new ItemEntity(), item, Item.class));
        }
        
        return entity;
    }
    
    /**
     * Maps XML JAXB object to an entity bean.
     * 
     * @param event the JAXB object.
     * @return the entity bean.
     */
    static InvoiceDataHeader fromEntity(final InvoiceDataEntity entity) {
    	InvoiceDataHeader iDHeader = new InvoiceDataHeader();
        copyProperties(iDHeader, entity, InvoiceDataEntity.class);
        //Need to manually set reference id since entity has no setter method for referenceId
        iDHeader.setReferenceId(entity.getReferenceId());
        iDHeader.setTotalAmount(entity.getTotalAmount());
        
        return iDHeader;
    }
    
    List<InvoiceDataHeader> fromIEntity(final List<InvoiceDataEntity> entityList) {
        List<InvoiceDataHeader> iDHeaderList = new ArrayList<InvoiceDataHeader>();
        for (final InvoiceDataEntity iDE : entityList) {
            iDHeaderList.add(fromEntity(iDE));
        }        
        return iDHeaderList;
    }
    
    RegisteredEvent fromEntity(final BusinessEventEntity bEEntity) {
        RegisteredEvent rEvent = new RegisteredEvent();
        AppUtil.copyProperties(rEvent, bEEntity, RegisteredEvent.class);
        
        rEvent.setEventId(bEEntity.getEventId());
        List<Item> itemList = new ArrayList<Item>();
        AppUtil.copyGenericLists(itemList, bEEntity.getItemEntities(), Item.class, ItemEntity.class);
        
        rEvent.setItemList(itemList);
        
        return rEvent;
    }
    
    List<RegisteredEvent> fromBEntity(final List<BusinessEventEntity> bEEntityList) {
        List<RegisteredEvent> registeredEventList = new ArrayList<RegisteredEvent>();
        for (final BusinessEventEntity bEEntity : bEEntityList) {
            registeredEventList.add(fromEntity(bEEntity));
            
        }
        return registeredEventList;
    }
}