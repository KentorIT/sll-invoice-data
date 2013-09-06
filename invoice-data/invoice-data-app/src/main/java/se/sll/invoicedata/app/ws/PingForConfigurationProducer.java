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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.ObjectFactory;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

public class PingForConfigurationProducer extends AbstractProducer implements PingForConfigurationResponderInterface {
    
    private static final Logger LOG = LoggerFactory.getLogger(PingForConfigurationProducer.class);
    
    private static ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };
    
    private static ObjectFactory objectFactory = new ObjectFactory();
    
    public PingForConfigurationResponseType pingForConfiguration(String logicalAddress, PingForConfigurationType parameters) {
        LOG.info("pingForConfiguration, logicalAdress: {}", logicalAddress);
        PingForConfigurationResponseType response = objectFactory.createPingForConfigurationResponseType();
        response.setVersion("1.0");
        response.setPingDateTime(formatter.get().format(new Date()));
        return response;
    }
}
