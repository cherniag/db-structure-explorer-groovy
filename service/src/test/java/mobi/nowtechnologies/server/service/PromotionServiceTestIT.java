package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.device.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserTransaction;
import mobi.nowtechnologies.server.persistence.domain.UserTransactionType;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserTransactionRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Matchers;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class PromotionServiceTestIT {

    private static final String MOBILE = "+447123456789";

    @Resource
    private PromotionService testInstance;

    @Resource
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Resource
    private PromotionRepository promotionRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Resource
    private UserTransactionRepository userTransactionRepository;

    private SubscriptionCampaignRecord subscriptionCampaignRecord;

    @Before
    public void setUp() throws Exception {
        initSubscriptionCampaignRecord();
    }

    @After
    public void tearDown() throws Exception {
        subscriptionCampaignRepository.deleteAll();
    }


    @Test
    public void shouldMatch3GUser() throws Exception {
        User user = createUser(Tariff._3G, Community.O2_COMMUNITY_REWRITE_URL);
        Promotion promotion = testInstance.getPromotionFromRuleForAutoOptIn(user);
        assertThat(promotion.getI(), Matchers.is(get3GPromotion().getI()));
    }


    @Test
    public void shouldMatch4GUser() throws Exception {
        User user = createUser(Tariff._4G, Community.O2_COMMUNITY_REWRITE_URL);
        Promotion promotion = testInstance.getPromotionFromRuleForAutoOptIn(user);
        assertThat(promotion.getI(), Matchers.is(get4GPromotion().getI()));
    }

    @Test
    public void applyPotentialPromo() throws Exception {
        User user = createUser(Tariff._3G, Community.O2_COMMUNITY_REWRITE_URL);
        userRepository.save(user);

        testInstance.applyPotentialPromo(user);

        assertTrue(user.isOnFreeTrial());
        assertTrue(user.isSubscribedStatus());
        assertTrue(user.getNextSubPayment() > System.currentTimeMillis() / 1000);
        assertTrue(user.getFreeTrialExpiredMillis() > System.currentTimeMillis());
        assertNotNull(user.getLastPromo());
        assertEquals("promo8", user.getLastPromo().getCode());

        List<UserTransaction> userTransactions = userTransactionRepository.findByUser(user);
        assertEquals(1, userTransactions.size());
        assertEquals(UserTransactionType.PROMOTION_BY_PROMO_CODE, userTransactions.get(0).getTransactionType());
        assertEquals("promo8", userTransactions.get(0).getPromoCode());
        assertEquals(user.getFreeTrialStartedTimestampMillis().longValue(), userTransactions.get(0).getStartTimestamp());
        assertEquals(user.getFreeTrialExpiredMillis().longValue(), userTransactions.get(0).getEndTimestamp());
    }

    private void initSubscriptionCampaignRecord() {
        subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(MOBILE);
        subscriptionCampaignRecord.setCampaignId("O2reengagement");
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);
    }

    private User createUser(Tariff tariff, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setMobile(MOBILE);
        user.setTariff(tariff);
        user.setUserGroup(getUserGroup(communityRewriteUrl));
        user.setProvider(ProviderType.O2);
        user.setContract(PAYG);
        user.setSegment(SegmentType.CONSUMER);
        user.withOldUser(getOldUser());
        user.withAutoOptInEnabled(false);
        user.setStatus(UserStatusDao.getLimitedUserStatus());
        return user;
    }

    private UserGroup getUserGroup(String communityRewriteUrl) {
        return userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
    }

    private User getOldUser() {
        User oldUser = new User();
        oldUser.setStatus(UserStatusDao.getLimitedUserStatus());
        oldUser.setFreeTrialExpiredMillis(System.currentTimeMillis() - 1000L);
        oldUser.setCurrentPaymentDetails(null);
        return oldUser;
    }

    private Promotion get3GPromotion() {
        return promotionRepository.findOne(101);
    }

    public Promotion get4GPromotion() {
        return promotionRepository.findOne(102);
    }
}