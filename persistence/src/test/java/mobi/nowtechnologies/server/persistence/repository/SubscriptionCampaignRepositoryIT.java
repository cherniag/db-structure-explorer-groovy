package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev Date: 4/8/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml"})
public class SubscriptionCampaignRepositoryIT {

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Before
    public void setUp() throws Exception {
        subscriptionCampaignRepository.deleteAll();
    }

    @Test
    public void testGetCountForMobile() throws Exception {
        createAndSaveRecord("+447123456789", "campaignId");
        createAndSaveRecord("+447123456789", "campaignId");
        createAndSaveRecord("+447123456789", "other");
        createAndSaveRecord("+440000000000", "campaignId");
        long countForMobile = subscriptionCampaignRepository.countForMobile("+447123456789", "campaignId");
        assertThat(countForMobile, is(2L));
    }

    private void createAndSaveRecord(String mobile, String campaignId) {
        SubscriptionCampaignRecord subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(mobile);
        subscriptionCampaignRecord.setCampaignId(campaignId);
        subscriptionCampaignRepository.saveAndFlush(subscriptionCampaignRecord);
    }
}
