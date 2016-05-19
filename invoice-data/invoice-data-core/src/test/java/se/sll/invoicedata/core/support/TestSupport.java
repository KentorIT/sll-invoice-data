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

package se.sll.invoicedata.core.support;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import riv.sll.invoicedata._1.DiscountItem;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.ReferenceItem;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.PriceListEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * Abstracts JUnit and Spring configuration stuff, and is intended to extend
 * all test classes.
 * 
 * @author Peter
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/invoice-data-service.xml")
@ActiveProfiles(profiles={"test"}, inheritProfiles=true)
public abstract class TestSupport {

    @Autowired
    private BusinessEventRepository businessEventRepository;
    
    @Autowired
    private InvoiceDataRepository invoiceDataRepository;
    
	@Autowired
    private InvoiceDataService invoiceDataService;
	
	@Rule 
    public ExpectedException thrown = ExpectedException.none();
	
    protected BusinessEventRepository getBusinessEventRepository() {
        return businessEventRepository;
    }

    protected InvoiceDataRepository getInvoiceDataRepository() {
        return invoiceDataRepository;
    }
    
	protected Event createSampleEvent() {
		final BusinessEventEntity e = createSampleBusinessEventEntity();
		e.addItemEntity(createSampleItemEntity());

		final Event event = CoreUtil.copyProperties(e, Event.class);

		CoreUtil.copyGenericLists(event.getItemList(), e.getItemEntities(),
				Item.class);

		return event;
	}
	
    protected ItemEntity createSampleItemEntity() {
    	ItemEntity i = new ItemEntity();
    	i.setDescription("Item is kind of a product");
		i.setItemId("IT101");
		i.setQty(BigDecimal.valueOf(2.0));
		i.setPrice(BigDecimal.valueOf(350.0));
		
		return i;
	
    }
    
    protected DiscountItem createDiscountItem() {
    	DiscountItem discountItem = new DiscountItem();
    	discountItem.setDescription("A discount item shall have same id as service item");
    	discountItem.setDiscountInPercentage(50);
    	discountItem.setOrderOfDiscount(1);
    	
    	ReferenceItem refItem = new ReferenceItem();
    	refItem.setQty(1);
    	refItem.setRefItemId("IT101");    	
    	discountItem.getReferenceItemList().add(refItem);
    	return discountItem;
    }
    
    protected BusinessEventEntity createSampleBusinessEventEntity() {
    	BusinessEventEntity e = new BusinessEventEntity();
        e.setEventId("event-123");
        e.setHealthcareFacility("HSA_ID_001");
        e.setRefContractId("Contract_1");
        e.setAcknowledgementId(UUID.randomUUID().toString());
        e.setAcknowledgedBy("Peter Larsson");
        e.setSupplierName("Dummy");
        e.setAcknowledgedTime(new Date());
        e.setHealthCareCommission("BVC");
        e.setServiceCode("XYZ");
        e.setPaymentResponsible("HSF");
        e.setCostCenter("cost-center");
        e.setSupplierId("12342");
        e.setStartTime(new Date());
        e.setEndTime(new Date());
        
        return e;
    }

    protected InvoiceDataEntity createSampleInvoiceDataEntity() {
        final InvoiceDataEntity e = new InvoiceDataEntity();
        
        e.setSupplierId("supplierId");
        e.setCreatedBy("createdBy");
        e.setPaymentResponsible("HSF");
        e.setCostCenter("cost-center");
        e.setPending(false);
        
        return e;
    }
    
    protected InvoiceDataEntity createSamplePendingInvoiceDataEntity() {
        final InvoiceDataEntity e = new InvoiceDataEntity();
        
        e.setSupplierId("supplierId");
        e.setCreatedBy("createdBy");
        e.setPaymentResponsible("HSF");
        e.setCostCenter("cost-center");
        e.setPending(true);
        
        return e;
    }
    
    protected PriceListEntity createSamplePriceListEntity() {
        final PriceListEntity priceListEntity = new PriceListEntity();
        priceListEntity.setSupplierId("Tolk.001");
        priceListEntity.setServiceCode("Språktolk");
        priceListEntity.setValidFrom(today().getTime());
        return priceListEntity;
    }
    
    protected Calendar today() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
    
    protected CreateInvoiceDataRequest createInvoiceData(String supplierId) {
        final List<String> ids = Arrays.asList(new String[] { "event-1",
                "event-2", "event-3" });

        registerEvents(supplierId, ids);
        
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        final CreateInvoiceDataRequest ie = new CreateInvoiceDataRequest();
        ie.setSupplierId(supplierId);
        ie.setPaymentResponsible(e.getPaymentResponsible());
        ie.setCostCenter(e.getCostCenter());
        ie.setCreatedBy("test-auto");

        invoiceDataService.createInvoiceData(ie);
        return ie;
    }
    
    protected void registerEvents(String supplierId, List<String> ids) {
        for (final String id : ids) {
            final Event e = createSampleEvent();
            e.setEventId(id);
            e.setSupplierId(supplierId);

            invoiceDataService.registerEvent(e);
        }
    }
    
    public CreateInvoiceDataRequest getCreateInvoiceDataRequestFromPassedEvent(final Event e) {
        final CreateInvoiceDataRequest createReq = new CreateInvoiceDataRequest();
        createReq.setSupplierId(e.getSupplierId());
        createReq.setPaymentResponsible(e.getPaymentResponsible());
        createReq.setCostCenter(e.getCostCenter());
        createReq.setCreatedBy("getCreateInvoiceDataRequestFromPassedEvent");
        return createReq;        
    }
    
    protected List<RegisteredEvent> getRegisteredEventList(Event e) {
    	GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        return invoiceDataService.getAllPendingBusinessEvents(getIDRequest);
    }
    
    protected GetInvoiceDataRequest getInvoiceDataRequest(final Event e) {
    	GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        getIDRequest.setCostCenter(e.getCostCenter());
        return getIDRequest;
    }

}
