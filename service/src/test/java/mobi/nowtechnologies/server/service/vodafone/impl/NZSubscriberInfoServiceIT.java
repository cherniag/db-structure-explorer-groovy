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

import javax.annotation.Resource;

import static org.junit.Assert.*;

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

    @Test
    public void testConfirm() throws Exception {
        final int userId = 777;
        final String msisdn = "642101838801";

        nzService.belongs(msisdn);
        nzService.confirm(userId, msisdn);

        NZSubscriberInfo savedSubscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn);

        assertEquals(userId, savedSubscriberInfo.getUserId().intValue());
    }

    @Test(expected = SubscriberServiceException.ServiceNotAvailable.class)
    public void testNZServiceFault() throws Exception {
        nzService.belongs("64" + NZSubscriberInfoGatewayMock.notAvailablePrefix + "101838801");
    }

    @After
    public void tearDown() throws Exception {
        subscriberInfoRepository.deleteAll();
    }

}
