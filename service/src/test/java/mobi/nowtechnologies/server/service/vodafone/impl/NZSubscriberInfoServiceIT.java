package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.apptests.NZSubscriberInfoGatewayMock;
import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Anton Zemliankin
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
public class NZSubscriberInfoServiceIT {

    @Resource
    @Qualifier("service.NZSubscriberInfoService")
    NZSubscriberInfoService nzService;
    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;

    @Test
    public void testNZService() throws Exception {
        boolean isVodafone = nzService.belongs("642101838801");

        NZSubscriberInfo savedSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn("642101838801");

        assertTrue(isVodafone);
        assertNotNull(savedSubscriberInfo);
        assertEquals("642101838801", savedSubscriberInfo.getMsisdn());
        assertEquals("Prepay", savedSubscriberInfo.getPayIndicator());
        assertEquals("Vodafone", savedSubscriberInfo.getProviderName());
        assertEquals("300001121", savedSubscriberInfo.getBillingAccountNumber());
        assertEquals("Simplepostpay_CCRoam", savedSubscriberInfo.getBillingAccountName());
    }

    @Test(expected = SubscriberServiceException.ServiceNotAvailable.class)
    public void testNZServiceFault() throws Exception {
        nzService.belongs("64" + NZSubscriberInfoGatewayMock.notAvailablePrefix + "101838801");
    }

    @Test
    public void testNZServiceFaultButMSISDNAlreadyInDB() throws Exception {
        String msisdn = "64" + NZSubscriberInfoGatewayMock.notAvailablePrefix + "101838801";
        NZSubscriberInfo subscriberInfo = new NZSubscriberInfo(msisdn);
        subscriberInfo.setPayIndicator("Prepay");
        subscriberInfo.setBillingAccountNumber("300001121");
        subscriberInfo.setProviderName("Vodafone");
        subscriberInfoRepository.save(subscriberInfo);

        boolean isVodafone = nzService.belongs(msisdn);
        assertTrue(isVodafone);
    }

    @Test
    public void testNZServiceUpdateTimestamp() throws Exception {
        String msisdn = "641101838801";

        nzService.belongs(msisdn);
        NZSubscriberInfo subscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);
        Date updateTimestamp = (Date)ReflectionTestUtils.getField(subscriberInfo, "updateTimestamp");

        assertEquals(1, ReflectionTestUtils.getField(subscriberInfo, "wsCallCount"));
        assertTrue((Long)ReflectionTestUtils.getField(subscriberInfo, "wsCallMillis") > 0);

        Thread.sleep(2);

        nzService.belongs(msisdn);
        subscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);

        assertEquals(2, ReflectionTestUtils.getField(subscriberInfo, "wsCallCount"));
        assertTrue((Long)ReflectionTestUtils.getField(subscriberInfo, "wsCallMillis") > 0);
        assertTrue(updateTimestamp.before((Date)ReflectionTestUtils.getField(subscriberInfo, "updateTimestamp")));
    }

    @After
    public void tearDown() throws Exception {
        subscriberInfoRepository.deleteAll();
    }

}
