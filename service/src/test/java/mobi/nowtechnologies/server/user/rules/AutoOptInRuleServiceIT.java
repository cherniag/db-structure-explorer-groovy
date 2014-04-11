package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.shared.enums.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ACC_CHECK;
import static org.hamcrest.CoreMatchers.notNullValue;
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

    @Before
    public void setUp() throws Exception {
        subscriptionCampaignRepository.deleteAll();
    }

    @Test
    public void testContext() throws Exception {
        SubscriptionCampaignRecord subscriptionCampaignRecord = new SubscriptionCampaignRecord();
        subscriptionCampaignRecord.setMobile("+447123456789");
        subscriptionCampaignRecord.setCampaignId("campaignId");
        subscriptionCampaignRepository.save(subscriptionCampaignRecord);

        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setTariff(Tariff._4G);
        paymentPolicy.setMediaType(MediaType.AUDIO);
        Community o2 = communityRepository.findByRewriteUrlParameter("o2");
        paymentPolicy.setCommunity(o2);
        paymentPolicy.setProvider(ProviderType.O2);
        paymentPolicy.setSegment(SegmentType.BUSINESS);
        paymentPolicy.setPaymentType(PaymentDetails.O2_PSMS_TYPE);
        paymentPolicy.setContract(Contract.PAYG);
        paymentPolicy.setSubcost(BigDecimal.ONE);
        paymentPolicy.withOnline(true);

        paymentPolicyRepository.save(paymentPolicy);

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

        RuleResult<Boolean> ruleResult = ruleService.fireRules(ACC_CHECK, user);
        assertThat(ruleResult, notNullValue());
        assertThat(ruleResult.isSuccessful(), is(true));
        assertThat(ruleResult.getResult(), notNullValue());
        assertThat(ruleResult.getResult(), is(true));

    }
}
