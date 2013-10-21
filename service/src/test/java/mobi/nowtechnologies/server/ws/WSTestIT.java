package mobi.nowtechnologies.server.ws;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import uk.co.o2.soa.subscriberdata.SubscriberProfileType;
import uk.co.o2.soa.subscriberservice.SubscriberService;
import uk.co.o2.soa.utils.SOAPLoggingHandler;
import uk.co.o2.soa.utils.SecurityHandler;
import uk.co.o2.soa.utils.SubscriberPortDecorator;

public class WSTestIT {

    @Test
    @Ignore
    public void pingO2() throws Exception {
        ClassLoader loader = SubscriberService.class.getClassLoader();
        URL resource = loader.getResource("META-INF/keystore.jks");

        File cert = new File(resource.toURI());
        Assert.assertTrue("No certification.", cert.exists());
        System.setProperty("javax.net.ssl.keyStore", cert.getAbsolutePath());
        System.setProperty("javax.net.ssl.keyStorePassword", "Fb320p007++");
        //System.setProperty("javax.net.debug", "ssl");

        SubscriberService service = new SubscriberService(loader.getResource("META-INF/wsdl/subscriberservice_2_0.xml"));
        SubscriberPortDecorator port = new SubscriberPortDecorator(service.getSubscriberPort());

        port.setEndpoint("https://sdpapi.ref.o2.co.uk/services/Subscriber_2_0");
        port.setHandler(new SOAPLoggingHandler());
        port.setHandler(new SecurityHandler("musicQubed_1001", "BA4sWteQ"));

        SubscriberProfileType profile = port.getSubscriberProfile("447742166053");
        System.out.println(profile);
    }
}
