package mobi.nowtechnologies.server.user.rules;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher;
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
import static mobi.nowtechnologies.server.user.criteria.OrMatcher.or;
import static mobi.nowtechnologies.server.user.rules.AutoOptInRuleService.AutoOptInTriggerType.ALL;
import static mobi.nowtechnologies.server.user.rules.RuleServiceSupport.RuleComparator;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */

public class AutoOptInRuleService {

    private static final ArrayList<String> DIRECT_PAYMENT_TYPES = Lists.newArrayList(PaymentDetails.O2_PSMS_TYPE, PaymentDetails.VF_PSMS_TYPE);
    private static final UserStatus USER_STATUS_LIMITED = new UserStatus(UserStatus.LIMITED);

    public void setRuleServiceSupport(RuleServiceSupport ruleServiceSupport) {
        this.ruleServiceSupport = ruleServiceSupport;
    }

    public enum AutoOptInTriggerType implements TriggerType {
        ALL;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoOptInRuleService.class);
    private RuleServiceSupport ruleServiceSupport;
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    public void init(){
        Map<TriggerType, SortedSet<Rule>> actionRules = new HashMap<TriggerType, SortedSet<Rule>>();
        SortedSet<Rule> rules = new TreeSet<Rule>(new RuleComparator());

        Matcher<User> fromO2Community = is(userCommunityRewriteUrl(), equalTo(Community.O2_COMMUNITY_REWRITE_URL));
        Matcher<User> hasLimitedStatus = is(userStatus(), equalTo(USER_STATUS_LIMITED));
        Matcher<User> freeTrialIsNull = is(userFreeTrialExpiredMillis(), nullValue(Long.class));
        Matcher<User> freeTrialIsEnded = is(userFreeTrialExpiredMillis(), lessThan(currentTimestamp()));
        Matcher<User> hasNoPaymentDetails = is(userCurrentPaymentDetails(), nullValue(PaymentDetails.class));
        Matcher<User> paymentDetailsAreDeactivated = is(userCurrentPaymentDetailsActivated(), equalTo(false));
        Matcher<User> hasNoPayments = is(userLastSuccessfullPaymentDetails(), nullValue(PaymentDetails.class));
        Matcher<User> isO2Provider = is(userProviderType(), equalTo(ProviderType.O2));
        Matcher<User> isConsumerSegment = is(userSegment(), equalTo(SegmentType.CONSUMER));
        Matcher<User> isInCampaignTable = new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId");

        Matcher<User> rootUserMatcher = and(
                fromO2Community,
                hasLimitedStatus,
                and(
                    not(freeTrialIsNull),
                    freeTrialIsEnded
                ),
                or(
                    hasNoPaymentDetails,
                    and(
                            paymentDetailsAreDeactivated,
                            hasNoPayments)
                    ),
                isO2Provider,
                isConsumerSegment,
                isInCampaignTable
        );
        SubscriptionCampaignUserRule subscriptionCampaignUserRule = new SubscriptionCampaignUserRule(rootUserMatcher, 10);
        rules.add(subscriptionCampaignUserRule);
        actionRules.put(ALL, rules);
        ruleServiceSupport = new RuleServiceSupport(actionRules);
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<SegmentType> userSegment() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<SegmentType>() {
            @Override
            public SegmentType getUserDetail(User user) {
                return user.getSegment();
            }
        };
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<ProviderType> userProviderType() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<ProviderType>() {
            @Override
            public ProviderType getUserDetail(User user) {
                return user.getProvider();
            }
        };
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<String> userCommunityRewriteUrl() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<String>() {
            @Override
            public String getUserDetail(User user) {
                return user.getCommunityRewriteUrl();
            }
        };
    }

    public boolean isSubjectToAutoOptIn(AutoOptInTriggerType triggerType, User user){
        LOGGER.info("Firing rules for trigger type {} and user id {}", triggerType, user.getId());
        RuleResult<Boolean> ruleResult = ruleServiceSupport.fireRules(triggerType, user);
        LOGGER.info("Rule result {}", ruleResult);
        if(ruleResult.isSuccessful()){
            return ruleResult.getResult();
        } else {
            return user.isSubjectToAutoOptIn();
        }
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<PaymentDetails> userLastSuccessfullPaymentDetails() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<PaymentDetails>() {
            @Override
            public PaymentDetails getUserDetail(User user) {
                return user.getLastSuccessfulPaymentDetails();
            }
        };
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<Boolean> userCurrentPaymentDetailsActivated() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<Boolean>() {
            @Override
            public Boolean getUserDetail(User user) {
                return user.getCurrentPaymentDetails() != null ? user.getCurrentPaymentDetails().isActivated() : null;
            }
        };
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<PaymentDetails> userCurrentPaymentDetails() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<PaymentDetails>() {
            @Override
            public PaymentDetails getUserDetail(User user) {
                return user.getCurrentPaymentDetails();
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

}
