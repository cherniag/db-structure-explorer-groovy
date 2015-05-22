package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
public class IsInCampaignTableUserMatcherIT {

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    private IsInCampaignTableUserMatcher isInCampaignTableUserMatcher;

    @Before
    public void setUp() throws Exception {
        isInCampaignTableUserMatcher = new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId");
    }

    @After
    public void tearDown() throws Exception {
        subscriptionCampaignRepository.deleteAll();
    }

    @Test
    public void testMatch() throws Exception {
        String mobile = "+447123456789";
        createAndSaveSubscriptionCampaignRecord(mobile, "campaignId");
        createAndSaveSubscriptionCampaignRecord(mobile, "otherCampaign");
        User user = new User();
        user.setMobile(mobile);
        assertThat(isInCampaignTableUserMatcher.match(user), is(true));
    }

    @Test
    public void testCampaignIdNotMatch() throws Exception {
        String mobile1 = "+447123456789";
        String mobile2 = "+447111111111";
        String mobile3 = "+447000000000";
        createAndSaveSubscriptionCampaignRecord(mobile1, "campaignId");
        createAndSaveSubscriptionCampaignRecord(mobile2, "campaignId");
        createAndSaveSubscriptionCampaignRecord(mobile3, "otherCampaign");
        User user = new User();
        user.setMobile(mobile3);
        assertThat(isInCampaignTableUserMatcher.match(user), is(false));
    }

    @Test
    public void testMobileNotMatch() throws Exception {
        String mobile1 = "+447123456789";
        String mobile2 = "+447111111111";
        String mobile3 = "+447000000000";
        createAndSaveSubscriptionCampaignRecord(mobile1, "campaignId");
        createAndSaveSubscriptionCampaignRecord(mobile2, "campaignId");
        User user = new User();
        user.setMobile(mobile3);
        assertThat(isInCampaignTableUserMatcher.match(user), is(false));
    }

    @Test
    public void testMatchWithMobileNull() throws Exception {
        String mobile = null;
        User user = new User();
        user.setMobile(mobile);
        assertThat(isInCampaignTableUserMatcher.match(user), is(false));
    }

    private void createAndSaveSubscriptionCampaignRecord(String mobile, String campaignId) {
        SubscriptionCampaignRecord subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(mobile);
        subscriptionCampaignRecord.setCampaignId(campaignId);
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);
    }
}
