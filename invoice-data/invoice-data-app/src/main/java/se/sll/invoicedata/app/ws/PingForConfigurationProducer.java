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

package se.sll.invoicedata.app.ws;

import java.text.SimpleDateFormat;
import java.util.Date;

import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.ObjectFactory;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;

/**
 * System check.
 * 
 * @author Peter
 *
 */
public class PingForConfigurationProducer extends AbstractProducer implements PingForConfigurationResponderInterface {
    
    private static ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };    
    private static ObjectFactory objectFactory = new ObjectFactory();
    
    @Override
    public PingForConfigurationResponseType pingForConfiguration(
            final String logicalAddress, final PingForConfigurationType parameters) {      

        
        final PingForConfigurationResponseType response = objectFactory.createPingForConfigurationResponseType();
        
        final ResultCode rc = fulfill(new Runnable() {
            @Override
            public void run() {
                response.setVersion("1.0");
                response.setPingDateTime(formatter.get().format(new Date()));
                getStatusBean().healthCheck();
            }
        });
        
        if (rc.getCode() == ResultCodeEnum.ERROR) {
            throw createSoapFault(rc.getMessage());
        }
        
        return response;
    }
}
