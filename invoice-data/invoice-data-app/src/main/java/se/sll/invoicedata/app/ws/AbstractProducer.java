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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.Item;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
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
    BusinessEventEntity toEntity(final Event event) {
        final BusinessEventEntity entity = new BusinessEventEntity();
        
        entity.setId(event.getEventId());
        entity.setSupplierName(event.getSupplierName());
        entity.setSupplierId(event.getSupplierId());
        entity.setServiceCode(event.getServiceCode());
        entity.setPaymentResponsible(event.getPaymentResponsible());
        entity.setStartTimestamp(event.getStartTime().toGregorianCalendar().getTime());
        entity.setEndTimestamp(event.getEndTime().toGregorianCalendar().getTime());
        entity.setAcknowledgedTimestamp(event.getAcknowledgedTime().toGregorianCalendar().getTime());
        entity.setAcknowledgedBy(event.getAcknowledgedBy());
        
        for (final Item item : event.getItemList()) {
            final ItemEntity itemEntity = new ItemEntity();
            
            itemEntity.setItemId(item.getItemId());;
            itemEntity.setQty(item.getQty());
            itemEntity.setDescription(item.getDescription());
     
            entity.addItemEntity(itemEntity);
        }
        
        return entity;
    }
    
    /**
     * Maps XML JAXB object to an entity bean.
     * 
     * @param event the JAXB object.
     * @return the entity bean.
     */
    InvoiceDataHeader fromEntity(final BusinessEventEntity entity) {
        final InvoiceDataHeader invoice = new InvoiceDataHeader();
        
        invoice.setPaymentResponsible(entity.getPaymentResponsible());
        invoice.setCreatedTime(getXMLGCalendar(entity.getInvoiceData().getCreatedTimestamp()));
        invoice.setSupplierId(entity.getSupplierId());
        invoice.setCreatedBy(entity.getInvoiceData().getCreatedBy());        
        invoice.setTotalAmount(entity.getTotalAmount());
        invoice.setReferenceId(entity.getInvoiceData().getReferenceId());
        
        return invoice;
    }
    
    private XMLGregorianCalendar getXMLGCalendar(Date date) {
    	GregorianCalendar gCal = new GregorianCalendar();
    	gCal.setTime(date);
    	XMLGregorianCalendar calendar = null;
    	
    	try {
    		calendar = DatatypeFactory.newInstance().
    				newXMLGregorianCalendar(gCal);
		} catch (DatatypeConfigurationException e) {			
			e.printStackTrace();
		}
    	return calendar;
	}
}