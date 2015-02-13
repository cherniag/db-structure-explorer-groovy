package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

/**
 * @author Anton Zemliankin
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
public class NZSubscriberInfoServiceIT {

    @Resource
    private NZSubscriberInfoService nzService;

    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;


    @Test
    public void testNZService() throws Exception {
        boolean isVodafone = nzService.checkVodafone(777, "642101838801");

        NZSubscriberInfo savedSubscriberInfo = subscriberInfoRepository.findTopByUserIdAndMsisdn(777, "642101838801");

        assertTrue(isVodafone);
        assertNotNull(savedSubscriberInfo);
        assertEquals(777, savedSubscriberInfo.getUserId());
        assertEquals("642101838801", savedSubscriberInfo.getMsisdn());
        assertEquals("Prepay", savedSubscriberInfo.getPayIndicator());
        assertEquals("Vodafone", savedSubscriberInfo.getProviderName());
        assertEquals("300001121", savedSubscriberInfo.getBillingAccountNumber());
        assertEquals("Simplepostpay_CCRoam", savedSubscriberInfo.getBillingAccountName());
        assertFalse(savedSubscriberInfo.isActive());
    }

    @Test(expected = ExternalServiceException.class)
    public void testNZServiceFault() throws Exception {
        boolean isVodafone = nzService.checkVodafone(777, NZSubscriberInfoGatewayMock.FAULT_DATA);
    }

    @After
    public void tearDown() throws Exception {
        subscriberInfoRepository.deleteAll();
    }

}
