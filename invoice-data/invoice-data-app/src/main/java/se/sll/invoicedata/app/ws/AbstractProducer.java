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

import java.util.Map;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;

/**
 * Abstracts generic logging and error handling for WS Producers
 * 
 * @author Peter
 */
public abstract class AbstractProducer {

    private static final Logger log = LoggerFactory.getLogger("WS-API");
    
    private static final String SERVICE_CONSUMER_HEADER_NAME = "x-rivta-original-serviceconsumer-hsaid";

    @Autowired
    private StatusBean statusBean;
    
    @Autowired
    private InvoiceDataService invoiceDataService;

    @Resource
    private WebServiceContext wsctx;
    
    /**
     * Creates a soap fault.
     * 
     * @param throwable the cause.
     * @return the soap fault object.
     */
    protected SoapFault createSoapFault(Throwable throwable) {
        final String msg = createLogMessage(throwable.toString());
        log.error(msg, throwable);
        
        final SoapFault soapFault = new SoapFault(msg, SoapFault.FAULT_CODE_SERVER);

        return soapFault;
    }
    
    
    /**
     * Returns the invoice data service.
     * 
     * @return the invoice data service.
     */
    protected InvoiceDataService getInvoiceDataService() {
        return invoiceDataService;
    }
    
    /**
     * Returns status bean.
     * 
     * @return the status bean.
     */
    protected StatusBean getStatusBean() {
        return statusBean;
    }

    
    /**
     * Returns the actual message context.
     * 
     * 
     * @return the message context.
     */
    protected MessageContext getMessageContext() {
        return wsctx.getMessageContext();
    }
    
    /**
     *
     * Logs message context information.
     * 
     * @param messageContext the context.
     */
    private void log(MessageContext messageContext) {
        final Map<?, ?> headers = (Map<?, ?>)messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        log.info(createLogMessage(headers.get(SERVICE_CONSUMER_HEADER_NAME)));
        log.debug("HTTP Headers {}", headers);
    }
    
    
    /**
     * Creates a log message.
     * 
     * @param msg the message.
     * @return the log message.
     */
    protected String createLogMessage(Object msg) {
        return String.format("%s - %s - \"%s\"", statusBean.getName(), statusBean.getGUID(), (msg == null) ? "NA" : msg);
    }
    
    /**
     * Invokes a run method in an instrumented manner.
     * 
     * @param runnable the runnable to run.
     * @return the result code.
     */
    protected ResultCode invoke(final Runnable runnable) {
        final MessageContext messageContext = getMessageContext();
        final String path = (String)messageContext.get(MessageContext.PATH_INFO);
        statusBean.start(path);
        log(messageContext);
        final ResultCode rc = new ResultCode();
        try {
            runnable.run();
            rc.setCode(ResultCodeEnum.OK);
        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnum.ERROR);
            rc.setMessage(ex.getMessage() + " (" + statusBean.getGUID() + ")");
            log.error(createLogMessage(ex.getMessage()));
        } catch (Throwable throwable) {
            throw createSoapFault(throwable);
        } finally {
            statusBean.stop();
        }
        return rc;
    }
    
}