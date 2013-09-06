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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

@WebService(serviceName = "PingForConfiguationProducer", endpointInterface = "se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface", portName = "PingForConfigurationResponderPort", targetNamespace = "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1:rivtabp21")
public class PingForConfigurationProducer implements PingForConfigurationResponderInterface {
    
    private static final Logger LOG = LoggerFactory.getLogger(PingForConfigurationProducer.class);
    
    
    @WebResult(name = "PingForConfigurationResponse", targetNamespace = "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1", partName = "parameters")
    @WebMethod(operationName = "PingForConfiguration", action = "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1:PingForConfiguration")
    public PingForConfigurationResponseType pingForConfiguration(
            @WebParam(partName = "LogicalAddress", name = "LogicalAddress", targetNamespace = "urn:riv:itintegration:registry:1", header = true) String arg0,
            @WebParam(partName = "parameters", name = "PingForConfiguration", targetNamespace = "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1") PingForConfigurationType arg1) {
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        PingForConfigurationResponseType response = new PingForConfigurationResponseType();
        response.setVersion("1.0");
        response.setPingDateTime(formatter.format(new Date()));
        return response;
    }
}
