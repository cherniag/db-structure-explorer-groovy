
package uk.co.o2.soa.subscriberservice;

import uk.co.o2.soa.utils.SubscriberPortDecorator;

import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


@WebServiceClient(name = "SubscriberService", targetNamespace = "http://soa.o2.co.uk/subscriberservice_2")
public class SubscriberService extends Service {

    private final static Logger logger = Logger.getLogger(uk.co.o2.soa.subscriberservice.SubscriberService.class.getName());


    public SubscriberService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SubscriberService(URL wsdl){
        super(wsdl, new QName("http://soa.o2.co.uk/subscriberservice_2", "SubscriberService"));
    }

    @WebEndpoint(name = "SubscriberPort")
    public SubscriberPort getSubscriberPort() {
        return super.getPort(new QName("http://soa.o2.co.uk/subscriberservice_2", "SubscriberPort"), SubscriberPort.class);
    }

    public SubscriberPortDecorator getSubscriberPortDecorator(){
        return new SubscriberPortDecorator(getSubscriberPort());
    }

    @WebEndpoint(name = "SubscriberPort")
    public SubscriberPort getSubscriberPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://soa.o2.co.uk/subscriberservice_2", "SubscriberPort"), SubscriberPort.class, features);
    }

}
