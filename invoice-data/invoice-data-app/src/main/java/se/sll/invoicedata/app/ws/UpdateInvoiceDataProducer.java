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

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata.updateinvoicedata._1.rivtabp21.UpdateInvoiceDataResponderInterface;
import riv.sll.invoicedata.updateinvoicedataresponder._1.UpdateInvoiceDataResponseType;
import riv.sll.invoicedata.updateinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnumType;

public class UpdateInvoiceDataProducer extends AbstractProducer implements UpdateInvoiceDataResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateInvoiceDataProducer.class);
    @Resource
    WebServiceContext wsctx;
            
      
    @Override      
    public UpdateInvoiceDataResponseType updateInvoiceData(
        @WebParam(partName = "LogicalAddress", name = "LogicalAddress", targetNamespace = "urn:riv:itintegration:registry:1", header = true)
        java.lang.String logicalAddress,
        @WebParam(partName = "parameters", name = "UpdateInvoiceData", targetNamespace = "urn:riv:sll:invoicedata:UpdateInvoiceDataResponder:1")
        riv.sll.invoicedata.updateinvoicedataresponder._1.UpdateInvoiceDataType parameters
    ) {
    	ObjectFactory f = new ObjectFactory();
    	UpdateInvoiceDataResponseType ur = f.createUpdateInvoiceDataResponseType();
    	
    	ResultCode rc = new ResultCode();
    	rc.setCode(ResultCodeEnumType.OK);
    	
    	ur.setResultCode(rc);
    	
    	return ur;
    	
    }

}
