/**
 * 
 */
package se.sll.invoicedata.app.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceData;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataResponse;
import se.sll.invoicedata.core.service.InvoiceDataService;

/**
 * @author muqkha
 *
 */
public class CreateInvoiceDataProducer extends AbstractProducer implements CreateInvoiceDataResponderInterface {

	private static final Logger log = LoggerFactory.getLogger(RegisterInvoiceDataProducer.class);
    
    @Autowired
    private InvoiceDataService invoiceDataService;
    
	@Override
	public CreateInvoiceDataResponse createInvoiceData(String logicalAddress,
			CreateInvoiceData parameters) {
		//TODO: 
		return null;
	}

}
