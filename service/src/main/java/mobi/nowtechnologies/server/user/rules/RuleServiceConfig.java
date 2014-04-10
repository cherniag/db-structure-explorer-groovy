package mobi.nowtechnologies.server.user.rules;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.user.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ACC_CHECK;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
@Configuration
public class RuleServiceConfig {

    @Autowired
    private PaymentPolicyRepository paymentPolicyRepository;

    @Autowired
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Bean(name = "service.AutoOptInRuleService")
    public AutoOptInRuleService autoOptInRuleService(){
        return new AutoOptInRuleService(actionRules());
    }

    private Map<TriggerType, SortedSet<Rule>> actionRules() {
        Map<TriggerType, SortedSet<Rule>> actionRules = new HashMap<TriggerType, SortedSet<Rule>>();

        actionRules.put(ACC_CHECK, accountCheckRules());

        return actionRules;
    }

    private SortedSet<Rule> accountCheckRules(){
        SortedSet<Rule> accountCheckRules = new TreeSet<Rule>(new RuleComparator());

        AndMatcher<User> rootMatcher = andMatcher(
                userStatusLimitedMatcher(),
                isInCampaignTableUserMatcher(),
                userIsEligibleForDirectBillingMatcher()
        );

        accountCheckRules.add(new UserRule(rootMatcher));
        return  accountCheckRules;
    }

    private <T> AndMatcher<T> andMatcher(Matcher... matchers) {
        return new AndMatcher<T>(Lists.newArrayList(matchers));
    }

    private ReflectionUserDetailMatcher userStatusLimitedMatcher() {
        return new ReflectionUserDetailMatcher("status", equalTo(), new UserStatus(UserStatus.LIMITED));
    }

    private IsEligibleForDirectPaymentUserMatcher userIsEligibleForDirectBillingMatcher() {
        return new IsEligibleForDirectPaymentUserMatcher(paymentPolicyRepository, Sets.newHashSet(PaymentDetails.O2_PSMS_TYPE, PaymentDetails.VF_PSMS_TYPE));
    }

    private IsInCampaignTableUserMatcher isInCampaignTableUserMatcher(){
        return new IsInCampaignTableUserMatcher(subscriptionCampaignRepository);
    }

    private static class RuleComparator implements Comparator<Rule> {
        @Override
        public int compare(Rule o1, Rule o2) {
            return o2.getPriority() - o1.getPriority();
        }
    }
}
