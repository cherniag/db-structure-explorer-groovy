package mobi.nowtechnologies.server.user.rules;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher;
import mobi.nowtechnologies.server.user.criteria.IsEligibleForDirectPaymentUserMatcher;
import mobi.nowtechnologies.server.user.criteria.IsInCampaignTableUserMatcher;
import mobi.nowtechnologies.server.user.criteria.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static mobi.nowtechnologies.server.user.criteria.AndMatcher.and;
import static mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher.is;
import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.lessThan;
import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.nullValue;
import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.currentTimestamp;
import static mobi.nowtechnologies.server.user.criteria.NotMatcher.not;
import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ACC_CHECK;
import static mobi.nowtechnologies.server.user.rules.RuleServiceSupport.RuleComparator;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */

public class AutoOptInRuleService {

    private static final ArrayList<String> DIRECT_PAYMENT_TYPES = Lists.newArrayList(PaymentDetails.O2_PSMS_TYPE, PaymentDetails.VF_PSMS_TYPE);
    private static final UserStatus USER_STATUS_LIMITED = new UserStatus(UserStatus.LIMITED);

    public enum AutoOptInTriggerType implements TriggerType {
        ACC_CHECK;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoOptInRuleService.class);
    private RuleServiceSupport ruleServiceSupport;
    private SubscriptionCampaignRepository subscriptionCampaignRepository;
    private PaymentPolicyRepository paymentPolicyRepository;

    public void init(){
        Map<TriggerType, SortedSet<Rule>> actionRules = new HashMap<TriggerType, SortedSet<Rule>>();
        SortedSet<Rule> rules = new TreeSet<Rule>(new RuleComparator());

        Matcher<User> userStatusIsLimited = is(userStatus(), equalTo(USER_STATUS_LIMITED));
        Matcher<User> freeTrialIsNull = is(userFreeTrialExpiredMillis(), nullValue(Long.class));
        Matcher<User> freeTrialIsEnded = is(userFreeTrialExpiredMillis(), lessThan(currentTimestamp()));
        Matcher<User> isInCampaignTable = new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId");
        Matcher<User> isEligibleForDirectPayment = new IsEligibleForDirectPaymentUserMatcher(paymentPolicyRepository, DIRECT_PAYMENT_TYPES);

        /*
        * users that have used their free trial but did not proceed to subscribing
        * */

        Matcher<User> rootUserMatcher = and(userStatusIsLimited, not(freeTrialIsNull), freeTrialIsEnded, isInCampaignTable, isEligibleForDirectPayment);
        SubscriptionCampaignUserRule subscriptionCampaignUserRule = new SubscriptionCampaignUserRule(rootUserMatcher, 10);
        rules.add(subscriptionCampaignUserRule);
        actionRules.put(ACC_CHECK, rules);
        ruleServiceSupport = new RuleServiceSupport(actionRules);
    }

    public RuleResult<Boolean> fireRules(AutoOptInTriggerType triggerType, User user){
         LOGGER.info("Firing rules for trigger type {} and user id {}", triggerType, user.getId());
         RuleResult<Boolean> ruleResult = ruleServiceSupport.fireRules(triggerType, user);
         LOGGER.info("Rule result {}", ruleResult);
         return ruleResult;
    }


    private CallBackUserDetailsMatcher.UserDetailHolder<Long> userFreeTrialExpiredMillis() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<Long>("freeTrialExpiredMillis") {
            @Override
            public Long getUserDetail(User user) {
                return user.getFreeTrialExpiredMillis();
            }
        };
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<UserStatus> userStatus() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<UserStatus>("status") {
            @Override
            public UserStatus getUserDetail(User user) {
                return user.getStatus();
            }
        };
    }

    public void setSubscriptionCampaignRepository(SubscriptionCampaignRepository subscriptionCampaignRepository) {
        this.subscriptionCampaignRepository = subscriptionCampaignRepository;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }
}
