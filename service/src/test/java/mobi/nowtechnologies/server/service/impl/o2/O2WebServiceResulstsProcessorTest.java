package mobi.nowtechnologies.server.service.impl.o2;

import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2WebServiceResultsProcessor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;

import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse;

import org.junit.*;
import static org.junit.Assert.*;

public class O2WebServiceResulstsProcessorTest {

    private static final String XML_DIR = "./src/test/resources/ws-samplexml/";

    private O2WebServiceResultsProcessor p = new O2WebServiceResultsProcessor();

    @Test
    public void testPostPayTarifContract4G() throws Exception {
        GetContractResponse r = readObject("managePostPayTariff20_getContract_paym_4g_result.xml", GetContractResponse.class);
        assertTrue(p.isPostPayContract4G(r));
    }

    @Test
    public void testPostPayTarifContractNon4G() throws Exception {
        GetContractResponse r = readObject("managePostPayTariff20_getContract_paym_no4g_result.xml", GetContractResponse.class);
        assertFalse(p.isPostPayContract4G(r));
    }

    @Test
    public void testGetSubscriberProfile() throws Exception {
        GetSubscriberProfileResponse r = readObject("subscriberservice20_getSubsProfile_result.xml", GetSubscriberProfileResponse.class);
        O2SubscriberData data = p.getSubscriberData(r);
        assertFalse(data.isBusinessOrConsumerSegment());
        assertTrue(data.isContractPostPayOrPrePay());
        assertTrue(data.isProviderO2());
    }

    @Test
    public void testPostPay4GBolton() throws Exception {
        GetCurrentBoltonsResponse r = readObject("managePostPayBoltons_getCurrentBoltons_4G_response.xml", GetCurrentBoltonsResponse.class);
        assertTrue(p.isPostPay4GBoltonPresent(r));
    }


    private <T> T readObject(String fileName, Class<?> clazz) throws JAXBException {
        File file = new File(XML_DIR + fileName);

        JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getPackage().getName());
        Unmarshaller unmarchaller = jaxbContext.createUnmarshaller();
        @SuppressWarnings("unchecked") T r = ((JAXBElement<T>) unmarchaller.unmarshal(file)).getValue();
        return r;
    }

    @Test
    public void testGetSubscriberProfileBusiness() throws Exception {
        GetSubscriberProfileResponse r = readObject("subscriberservice20_getSubsProfileBusiness_result.xml", GetSubscriberProfileResponse.class);
        O2SubscriberData data = p.getSubscriberData(r);
        assertTrue(data.isBusinessOrConsumerSegment());
        assertTrue(data.isContractPostPayOrPrePay());
        assertTrue(data.isProviderO2());
    }

}
