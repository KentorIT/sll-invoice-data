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
import java.util.UUID;

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
 * Abstract class used by WS Producers
 *
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
    
    protected void throwInternalServerError(Throwable t) {
        String errorText = "Internal server error (reference: " + UUID.randomUUID().toString() + ")";
        log.error(errorText, t);
        throw new SoapFault(errorText, SoapFault.FAULT_CODE_SERVER);
    }

    /**
     *
     * Returns path name, and logs basic data.
     * 
     * @return the service path name.
     */
    protected String logConsumer() {
        final MessageContext mctx = wsctx.getMessageContext();
        @SuppressWarnings (value="rawtypes")
        final Map headers = (Map)mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        final Object serviceConsumer = (headers == null) ? null : headers.get(SERVICE_CONSUMER_HEADER_NAME);
        final String path = (String)mctx.get(MessageContext.PATH_INFO);
        
        log.info("{}, {}", path, (serviceConsumer == null) ? "NA" : serviceConsumer);
        log.debug("HTTP Headers {}", headers);
        
        return path;
    }
    
    /**
     * Invokes a run method in an instrumented manner.
     * 
     * @param runnable the runnable to run.
     * @return the result code.
     */
    protected ResultCode invoke(final Runnable runnable) {
        statusBean.start(logConsumer());
        final ResultCode rc = new ResultCode();
        try {
            runnable.run();
            rc.setCode(ResultCodeEnum.OK);
        } catch (InvoiceDataServiceException ex) {
            rc.setCode(ResultCodeEnum.ERROR);
            rc.setMessage(ex.getMessage());
            log.error(ex.getMessage());
        } finally {
            statusBean.stop();
        }
        return rc;
    }

    protected InvoiceDataService getInvoiceDataService() {
        return invoiceDataService;
    }
}