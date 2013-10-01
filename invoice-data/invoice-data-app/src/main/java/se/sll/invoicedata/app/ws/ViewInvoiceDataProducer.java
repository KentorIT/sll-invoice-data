/**
 * 
 */
package se.sll.invoicedata.app.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.viewinvoicedata._1.rivtabp21.ViewInvoiceDataResponderInterface;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ViewInvoiceDataRequest;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ViewInvoiceDataResponse;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;



/**
 * @author muqkha
 * 
 */
public class ViewInvoiceDataProducer extends AbstractProducer implements
		ViewInvoiceDataResponderInterface {

	private static final Logger log = LoggerFactory.getLogger(ViewInvoiceDataProducer.class);

	@Autowired
	private InvoiceDataService invoiceDataService;

	/**
	 * 
	 */
	@Override
	public ViewInvoiceDataResponse viewInvoiceData(String logicalAddress,
			ViewInvoiceDataRequest parameters) {		
		log("viewInvoiceData");
        log.info("logicalAddress: {}", logicalAddress);
		
        final ObjectFactory oFactory = new ObjectFactory();
        final ResultCode rc = new ResultCode();
        final ViewInvoiceDataResponse viewIDataResponse = oFactory.createViewInvoiceDataResponse();
        
        try {
        	viewIDataResponse.setInvoiceData(invoiceDataService.getInvoiceDataByReferenceId(parameters.getReferenceId()));
        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnum.ERROR);
            rc.setMessage(ex.getMessage());
            log.error(ex.getMessage());
        }
        rc.setCode(ResultCodeEnum.OK);
        return viewIDataResponse;
	}

}
