package mobi.nowtechnologies.server.service.nz.impl;

import mobi.nowtechnologies.server.apptests.NZSubscriberInfoGatewayMock;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * @author Anton Zemliankin
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
public class NZSubscriberInfoServiceConcurrentIT {

    NZSubscriberInfoServiceForTest nzService;

    @Resource
    NZSubscriberInfoRepository subscriberInfoRepository;

    @Resource
    NZSubscriberInfoGatewayMock subscriberInfoGatewayMock;

    @Test
    public void testNZServiceConcurrentBelongs() throws Exception {
        boolean belongs1 = nzService.belongs("6410183880");
        boolean belongs2 = nzService.belongs("6410183880");

        assertEquals(belongs1, belongs2);
    }

    @Before
    public void init(){
        nzService = new NZSubscriberInfoServiceForTest();
        nzService.setSubscriberInfoRepository(subscriberInfoRepository);
        nzService.setSubscriberInfoGateway(subscriberInfoGatewayMock);
    }

    @After
    public void tearDown() throws Exception {
        subscriberInfoRepository.deleteAll();
    }
}
