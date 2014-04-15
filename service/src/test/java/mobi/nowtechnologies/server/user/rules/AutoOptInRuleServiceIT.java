package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ALL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class AutoOptInRuleServiceIT {

    private static final String MOBILE = "+447123456789";
    @Autowired
    private AutoOptInRuleService ruleService;

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

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
    public void checkRuleIsMatched() throws Exception {
        User user = createMatchingUser();

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    @Test
    public void checkRuleIsMatchedFor3G() throws Exception {
        User user = createMatchingUser();
        user.setTariff(_3G);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    @Test
    public void checkRuleIsMatchedFor4G() throws Exception {
        User user = createMatchingUser();
        user.setTariff(_4G);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    @Test
    public void checkRuleIsMatchedForPAYM() throws Exception {
        User user = createMatchingUser();
        user.setContract(PAYM);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    @Test
    public void checkRuleIsMatchedForPAYG() throws Exception {
        User user = createMatchingUser();
        user.setContract(PAYG);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    @Test
    public void checkRuleIsMatchedForDisabledPaymentDetailsAndNoPayments() throws Exception {
        User user = createMatchingUser();
        PaymentDetails paymentDetails = new O2PSMSPaymentDetails();
        paymentDetails.setActivated(false);
        user.setCurrentPaymentDetails(paymentDetails);
        user.setLastSuccessfulPaymentDetails(null);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    @Test
    public void checkWhenUserIsNotFromO2() throws Exception {
        User user = createMatchingUser();
        user.getUserGroup().getCommunity().setRewriteUrlParameter("vf");

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
     public void checkWhenUserIsNotLimited() throws Exception {
        User user = createMatchingUser();
        user.setStatus(new UserStatus(UserStatus.SUBSCRIBED));

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUserDoesNotHaveAFreeTrial() throws Exception {
        User user = createMatchingUser();
        user.setFreeTrialExpiredMillis(null);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUsersFreeTrialIsNotExpired() throws Exception {
        User user = createMatchingUser();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() + 10000L);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUserHasActivePaymentDetails() throws Exception {
        User user = createMatchingUser();
        PaymentDetails paymentDetails = new O2PSMSPaymentDetails();
        paymentDetails.setActivated(true);
        user.setCurrentPaymentDetails(paymentDetails);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUserDeviceIsBB() throws Exception {
        User user = createMatchingUser();
        DeviceType deviceType = new DeviceType();
        deviceType.setName(DeviceType.BLACKBERRY);
        user.setDeviceType(deviceType);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUserIsNotFromO2Provider() throws Exception {
        User user = createMatchingUser();
        user.setProvider(ProviderType.NON_O2);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUserIsNotConsumer() throws Exception {
        User user = createMatchingUser();
        user.setSegment(SegmentType.BUSINESS);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWhenUserIsNotInCampaignTable() throws Exception {
        User user = createMatchingUser();
        user.setMobile("+4455555555");

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void checkWithLegacyIsSubjectToAutoOptIn() throws Exception {
        User user = createMatchingUser();
        //fail rules
        user.setStatus(new UserStatus(UserStatus.SUBSCRIBED));
        //make user.isSubjectToAutoOptIn() to return true (isAutoOptInEnabled && isNull(oldUser) && isO24GConsumer() && !isLastPromoForVideoAndAudio() )
        user.withAutoOptInEnabled(true);
        user.withOldUser(null);
        user.setSegment(SegmentType.CONSUMER);
        user.setLastPromo(null);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    private User createMatchingUser() {
        User user = new User();
        Community community = new Community();
        community.setRewriteUrlParameter(Community.O2_COMMUNITY_REWRITE_URL);
        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(community);
        DeviceType deviceType = new DeviceType();
        deviceType.setName(DeviceType.ANDROID);
        user.setDeviceType(deviceType);
        user.setMobile(MOBILE);
        user.setTariff(_4G);
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.O2);
        user.setContract(PAYG);
        user.setSegment(SegmentType.CONSUMER);
        user.setStatus(new UserStatus(UserStatus.LIMITED));
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() - 1000L);
        user.withAutoOptInEnabled(false);
        return user;
    }

    private void initSubscriptionCampaignRecord() {
        subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile(MOBILE);
        subscriptionCampaignRecord.setCampaignId("campaignId");
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);
    }
}
