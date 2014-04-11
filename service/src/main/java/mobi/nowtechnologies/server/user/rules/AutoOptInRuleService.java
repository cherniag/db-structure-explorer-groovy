package mobi.nowtechnologies.server.user.rules;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.user.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static mobi.nowtechnologies.server.user.criteria.AndMatcher.and;
import static mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher.is;
import static mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher.isNull;
import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static mobi.nowtechnologies.server.user.criteria.NotMatcher.not;
import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ACC_CHECK;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */

public class AutoOptInRuleService {

    public enum AutoOptInTriggerType implements TriggerType {
        ACC_CHECK;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoOptInRuleService.class);
    private RuleServiceSupport ruleServiceSupport;
    private SubscriptionCampaignRepository subscriptionCampaignRepository;
    private PaymentPolicyRepository paymentPolicyRepository;

    public void init(){
        Map<TriggerType, SortedSet<Rule>> actionRules = new HashMap<TriggerType, SortedSet<Rule>>();
        SortedSet<Rule> rules = new TreeSet<Rule>(new RuleServiceSupport.RuleComparator());

        Matcher<User> limitedUserStatus = new ReflectionUserDetailMatcher("status", equalTo(), new UserStatus(UserStatus.LIMITED));
        Matcher<User> isInCampaignTable = new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId");
        Matcher<User> isEligibleForDirectPayment = new IsEligibleForDirectPaymentUserMatcher(paymentPolicyRepository,
                Lists.newArrayList(PaymentDetails.O2_PSMS_TYPE, PaymentDetails.VF_PSMS_TYPE));
        Matcher<User> freeTrialIsNull = isNull(userFreeTrialExpiredMillis());
        Matcher<User> freeTrialIsNotNull = not(freeTrialIsNull);
        Matcher<User> freeTrialIsEnded = is(userFreeTrialExpiredMillis(), CompareMatchStrategy.<Long>lessThan(), now());

        /*
        * users that have used their free trial but did not proceed to subscribing
        * */

        Matcher<User> rootUserMatcher = and(limitedUserStatus, freeTrialIsNotNull, freeTrialIsEnded, isInCampaignTable, isEligibleForDirectPayment);
        UserRule userRule = new UserRule(rootUserMatcher, 10);
        rules.add(userRule);
        actionRules.put(ACC_CHECK, rules);
        ruleServiceSupport = new RuleServiceSupport(actionRules);
    }

    private CallBackUserDetailsMatcher.ExpectedValueHolder<Long> now() {
        return new CallBackUserDetailsMatcher.ExpectedValueHolder<Long>() {
            @Override
            public Long getValue() {
                return System.currentTimeMillis();
            }
        };
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<Long> userFreeTrialExpiredMillis() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<Long>("freeTrialExpiredMillis") {
            @Override
            public Long getUserDetail(User user) {
                return user.getFreeTrialExpiredMillis();
            }
        };
    }

    public RuleResult<Boolean> fireRules(AutoOptInTriggerType triggerType, User user){
         LOGGER.info("Firing rules for trigger type {} and user id {}", triggerType, user.getId());
         RuleResult<Boolean> ruleResult = ruleServiceSupport.fireRules(triggerType, user);
         LOGGER.info("Rule result {}", ruleResult);
         return ruleResult;
     }

    public void setSubscriptionCampaignRepository(SubscriptionCampaignRepository subscriptionCampaignRepository) {
        this.subscriptionCampaignRepository = subscriptionCampaignRepository;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }
}
