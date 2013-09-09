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
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class used by WS Producers
 *
 */
public abstract class AbstractProducer {

    private final Logger logger = LoggerFactory.getLogger("pdl");

    protected void throwInternalServerError(Throwable t) {
        String errorText = "Internal server error (reference: " + UUID.randomUUID().toString() + ")";
        logger.error(errorText, t);
        throw new SoapFault(errorText, SoapFault.FAULT_CODE_SERVER);
    }

    /**
     *
     * @param wsctx
     * @param msg
     */
    protected void logPDL(WebServiceContext wsctx, String msg) {
        MessageContext mctx = null;
        if (wsctx != null) {
            mctx = wsctx.getMessageContext();
        } else {
            logger.error("wsctx is null");
            return;
        }                
        @SuppressWarnings (value="unchecked")
        Map<String, Object> http_headers = (Map<String, Object>) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        if (http_headers.containsKey("x-rivta-original-serviceconsumer-hsaid")) {
            logger.info("{} invoked by HSAid: {}", msg, http_headers.get("x-rivta-original-serviceconsumer-hsaid"));
        } else {
            logger.info("{} invoked by HSAid: NOT FOUND.", msg);
        }
    }
}