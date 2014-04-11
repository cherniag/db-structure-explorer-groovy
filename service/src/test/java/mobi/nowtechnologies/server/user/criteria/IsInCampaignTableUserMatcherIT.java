package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PromoCodeRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
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
 * Date: 4/10/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class IsInCampaignTableUserMatcherIT {

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    private IsInCampaignTableUserMatcher isInCampaignTableUserMatcher;

    @Before
    public void setUp() throws Exception {
        subscriptionCampaignRepository.deleteAll();
        isInCampaignTableUserMatcher = new IsInCampaignTableUserMatcher(subscriptionCampaignRepository);
    }

    @Test
    public void testMatch() throws Exception {
        String mobile = "+447123456789";
        createAndSaveSubscriptionCampaignRecord(mobile, 1);
        createAndSaveSubscriptionCampaignRecord(mobile, 2);
        User user  = new User();
        user.setMobile(mobile);
        assertThat(isInCampaignTableUserMatcher.match(user), is(true));
    }

    @Test
    public void testNotMatch() throws Exception {
        String mobile1 = "+447123456789";
        String mobile2 = "+447111111111";
        String mobile3 = "+447000000000";
        createAndSaveSubscriptionCampaignRecord(mobile1, 1);
        createAndSaveSubscriptionCampaignRecord(mobile2, 1);
        User user  = new User();
        user.setMobile(mobile3);
        assertThat(isInCampaignTableUserMatcher.match(user), is(false));
    }

    @Test
    public void testMatchWithMobileNull() throws Exception {
        String mobile = null;
        User user  = new User();
        user.setMobile(mobile);
        assertThat(isInCampaignTableUserMatcher.match(user), is(false));
    }

    private void createAndSaveSubscriptionCampaignRecord(String mobile, int promo_id) {
        SubscriptionCampaignRecord subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(mobile);
        PromoCode promoCode = promoCodeRepository.findOne(promo_id);
        subscriptionCampaignRecord.setPromoCode(promoCode);
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);
    }
}
