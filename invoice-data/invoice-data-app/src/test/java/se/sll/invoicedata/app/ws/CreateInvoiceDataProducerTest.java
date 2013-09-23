/**
 * 
 */
package se.sll.invoicedata.app.ws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;

/**
 * @author muqkha
 *
 */
public class CreateInvoiceDataProducerTest {
    
    static CreateInvoiceDataResponderInterface getCreateInvoiceDataService() {
        CreateInvoiceDataResponderInterface iCreateInvoiceDataResponder = null;

        final String URL = "http://localhost:8080/invoice-data-app/ws/createInvoiceData";
        // Endpoint.publish(URL, new RegisterInvoiceDataProducer());

        try {
            URL wsdlURL = new URL(URL + "?wsdl");

            String namespaceURI = "http://ws.app.invoicedata.sll.se/";
            String serviceName = "CreateInvoiceDataProducerService";

            QName serviceQN = new QName(namespaceURI, serviceName);

            Service service = Service.create(wsdlURL, serviceQN);
            
            iCreateInvoiceDataResponder = service
                    .getPort(CreateInvoiceDataResponderInterface.class);
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iCreateInvoiceDataResponder;
    }

}
