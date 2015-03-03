package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.hamcrest.Matchers;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class PromotionServiceTestIT {

    private static final String MOBILE = "+447123456789";

    @Autowired
    private PromotionService testInstance;

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Autowired
    private PromotionRepository promotionRepository;

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
        User user = createUser(Tariff._3G);
        Promotion promotion = testInstance.getPromotionFromRuleForAutoOptIn(user);
        assertThat(promotion.getI(), Matchers.is(get3GPromotion().getI()));
    }


    @Test
    public void shouldMatch4GUser() throws Exception {
        User user = createUser(Tariff._4G);
        Promotion promotion = testInstance.getPromotionFromRuleForAutoOptIn(user);
        assertThat(promotion.getI(), Matchers.is(get4GPromotion().getI()));
    }

    private void initSubscriptionCampaignRecord() {
        subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(MOBILE);
        subscriptionCampaignRecord.setCampaignId("O2reengagement");
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);
    }

    private User createUser(Tariff tariff) {
        User user = new User();
        user.setDeviceType(getDeviceType(DeviceType.ANDROID));
        user.setMobile(MOBILE);
        user.setTariff(tariff);
        user.setUserGroup(getUserGroup());
        user.setProvider(ProviderType.O2);
        user.setContract(PAYG);
        user.setSegment(SegmentType.CONSUMER);
        user.withOldUser(getOldUser());
        user.withAutoOptInEnabled(false);
        return user;
    }

    private DeviceType getDeviceType(String name) {
        DeviceType deviceType = new DeviceType();
        deviceType.setName(name);
        return deviceType;
    }

    private UserGroup getUserGroup() {
        Community community = new Community();
        community.setRewriteUrlParameter(Community.O2_COMMUNITY_REWRITE_URL);
        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(community);
        return userGroup;
    }

    private User getOldUser() {
        User oldUser = new User();
        oldUser.setStatus(new UserStatus(UserStatus.LIMITED));
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