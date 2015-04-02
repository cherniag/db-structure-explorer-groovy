package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import org.junit.After;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * @author Anton Zemliankin
 */

public class NZSubscriberInfoRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private NZSubscriberInfoRepository subscriberInfoRepository;

    @Test
    public void findSubscriberInfoByMsisdnTest() {
        NZSubscriberInfo subscriberInfo = getNzSubscriberInfo("12345");
        subscriberInfoRepository.save(subscriberInfo);

        subscriberInfo = getNzSubscriberInfo("67890");
        subscriberInfoRepository.save(subscriberInfo);

        subscriberInfo = getNzSubscriberInfo("333333");
        subscriberInfoRepository.save(subscriberInfo);

        subscriberInfo = subscriberInfoRepository.findSubscriberInfoByMsisdn("67890");

        assertEquals("67890", subscriberInfo.getMsisdn());
    }

    @Test
    public void findSubscriberInfoByUserId() throws Exception {
        NZSubscriberInfo subscriberInfo1 = getNzSubscriberInfo("12345");
        subscriberInfo1.setUserId(10);
        subscriberInfoRepository.save(subscriberInfo1);

        NZSubscriberInfo subscriberInfo2 = getNzSubscriberInfo("67890");
        subscriberInfo2.setUserId(20);
        subscriberInfoRepository.save(subscriberInfo2);

        NZSubscriberInfo infoByUserId = subscriberInfoRepository.findSubscriberInfoByUserId(10);
        assertEquals("12345", infoByUserId.getMsisdn());
    }

    @After
    public void tearDown() throws Exception {
        subscriberInfoRepository.deleteAll();
    }

    private NZSubscriberInfo getNzSubscriberInfo(String msisdn) {
        NZSubscriberInfo subscriberInfo = new NZSubscriberInfo(msisdn);
        subscriberInfo.setPayIndicator("Prepayed");
        subscriberInfo.setProviderName("Vodafone");
        subscriberInfo.setBillingAccountNumber("12334");
        return subscriberInfo;
    }
}
