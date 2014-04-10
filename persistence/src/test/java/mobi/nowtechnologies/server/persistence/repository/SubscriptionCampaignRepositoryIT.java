package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
public class SubscriptionCampaignRepositoryIT{

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Before
    public void setUp() throws Exception {
        subscriptionCampaignRepository.deleteAll();
    }

    @Test
    public void testGetCountForMobile() throws Exception {
        createAndSaveRecord("+447123456789");
        createAndSaveRecord("+447123456789");
        createAndSaveRecord("+447123456789");
        createAndSaveRecord("+440000000000");
        long countForMobile = subscriptionCampaignRepository.getCountForMobile("+447123456789");
        assertThat(countForMobile, is(3L));
    }

    private void createAndSaveRecord(String mobile) {
        SubscriptionCampaignRecord subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(mobile);
        subscriptionCampaignRepository.saveAndFlush(subscriptionCampaignRecord);
    }
}
