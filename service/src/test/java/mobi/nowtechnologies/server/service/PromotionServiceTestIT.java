package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Locale;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.VIDEO_AUDIO_FREE_TRIAL_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.PROMOTION_BY_PROMO_CODE_APPLIED;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.SUBSCRIPTION_CHARGE;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 *
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
        subscriptionCampaignRecord.setCampaignId("campaignId");
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

    private User getOldUser(){
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