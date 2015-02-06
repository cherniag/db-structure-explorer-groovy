package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import org.junit.After;
import org.junit.Assert;
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

    @Autowired
    private NZSubscriberInfoService nzService;

    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;

    @After
    public void tearDown() throws Exception {
        subscriberInfoRepository.deleteAll();
    }

    @Test
    public void testNZService() throws Exception {
        NZSubscriberInfo si = nzService.getSubscriberInfo(777, "6412121212");
        subscriberInfoRepository.saveAndFlush(si);

        NZSubscriberInfo savedSubscriberInfo = subscriberInfoRepository.findOne(si.getId());

        Assert.assertNotNull(savedSubscriberInfo);
    }

}
