/**
 * 
 */
package se.sll.invoicedata.app.ws;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceData;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.getinvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
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
			GetInvoiceData parameters) {
		log("getInvoiceData");
		log.info("logicalAdress: {}", logicalAddress);

		final ObjectFactory oFactory = new ObjectFactory();
		final GetInvoiceDataResponse response = oFactory
				.createGetInvoiceDataResponse();
		final ResultCode rc = new ResultCode();
		
		List<Event> eventEntityList = new ArrayList<Event>();
		try {
			
			if (parameters.getGetInvoices().getEventId() != null) {
				final Event event = fromEntity(invoiceDataService.getBusinessEvent(
						parameters.getGetInvoices().getEventId()));				
				eventEntityList.add(event);
			} else if (parameters.getGetInvoices().getSupplierName() != null) {
				final List<BusinessEventEntity> tmpEEntityList = invoiceDataService.
						getAllUnprocessedBusinessEvents(parameters.getGetInvoices().getSupplierName());
				
				for (BusinessEventEntity bEE : tmpEEntityList) {
					eventEntityList.add(fromEntity(bEE));
				}				
			}
			
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
