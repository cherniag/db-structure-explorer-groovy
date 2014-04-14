package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.shared.enums.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

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

    @Autowired
    private AutoOptInRuleService ruleService;

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Autowired
    private PaymentPolicyRepository paymentPolicyRepository;

    @Autowired
    private CommunityRepository communityRepository;
    private Community o2;
    private PaymentPolicy paymentPolicy;
    private SubscriptionCampaignRecord subscriptionCampaignRecord;

    @Before
    public void setUp() throws Exception {
        initCommunity();
        disableAutoOptIn();
        initPaymentPolicy();
        initSubscriptionCampaignRecord();
    }

    private void disableAutoOptIn() {
        System.setProperty("auto.opt.in.enabled", "false");
    }

    @After
    public void tearDown() throws Exception {
        paymentPolicyRepository.delete(paymentPolicy);
        subscriptionCampaignRepository.deleteAll();
    }

    @Test
    public void testAllMatchersAreMatched() throws Exception {
        User user = createMatchingUser();

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));

    }

    @Test
     public void testFireWhenUserIsNotLimited() throws Exception {
        User user = createMatchingUser();
        user.setStatus(new UserStatus(UserStatus.SUBSCRIBED));

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));

    }

    @Test
    public void testFireWhenUserDoesNotHaveAFreeTrial() throws Exception {
        User user = createMatchingUser();
        user.setFreeTrialExpiredMillis(null);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void testFireWhenUsersFreeTrialIsNotExpired() throws Exception {
        User user = createMatchingUser();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() + 10000L);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void testFireWhenUserIsNotInCampaignTable() throws Exception {
        User user = createMatchingUser();
        user.setMobile("+4455555555");

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void testFireWhenUserIsNotDirectCharged() throws Exception {
        User user = createMatchingUser();
        user.setProvider(ProviderType.NON_O2);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(false));
    }

    @Test
    public void testFireWhenUserRulesAreFalse() throws Exception {
        User user = createMatchingUser();
        //fail rules
        user.setStatus(new UserStatus(UserStatus.SUBSCRIBED));
        //make user.isSubjectToAutoOptIn() to return true (isAutoOptInEnabled && isNull(oldUser) && isO24GConsumer() && !isLastPromoForVideoAndAudio() )
        System.setProperty("auto.opt.in.enabled", "true");
        user.withOldUser(null);
        user.setSegment(SegmentType.CONSUMER);
        user.setLastPromo(null);

        boolean ruleResult = ruleService.isSubjectToAutoOptIn(ALL, user);
        assertThat(ruleResult, is(true));
    }

    private User createMatchingUser() {
        User user = new User();
        user.setMobile("+447123456789");
        user.setTariff(Tariff._4G);
        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(o2);
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.O2);
        user.setContract(Contract.PAYG);
        user.setSegment(SegmentType.BUSINESS);
        user.setStatus(new UserStatus(UserStatus.LIMITED));
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() - 1000L);
        return user;
    }

    private void initCommunity() {
        o2 = communityRepository.findByRewriteUrlParameter("o2");
    }

    private void initPaymentPolicy() {
        paymentPolicy = new PaymentPolicy();
        paymentPolicy.setTariff(Tariff._4G);
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setCommunity(o2);
        paymentPolicy.setProvider(ProviderType.O2);
        paymentPolicy.setSegment(SegmentType.BUSINESS);
        paymentPolicy.setPaymentType(PaymentDetails.O2_PSMS_TYPE);
        paymentPolicy.setContract(Contract.PAYG);
        paymentPolicy.setSubcost(BigDecimal.ONE);
        paymentPolicy.withOnline(true);

        paymentPolicyRepository.save(paymentPolicy);
    }

    private void initSubscriptionCampaignRecord() {
        subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile("+447123456789");
        subscriptionCampaignRecord.setCampaignId("campaignId");
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);
    }
}
