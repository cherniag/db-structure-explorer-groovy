package mobi.nowtechnologies.server.service.configuration;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.user.criteria.*;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import mobi.nowtechnologies.server.user.rules.TriggerType;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.valueOf;

public abstract class Configuration <TType extends TriggerType, V, BuilderType extends RuleBuilder<?, V> > implements InitializingBean {

    private Map<TType, List<BuilderType>> registeredBuilderMap = new HashMap<TType, List<BuilderType>>();

    final public RuleServiceSupport<TType> get() {
        Map<TType, SortedSet<Rule>> actionRules = new HashMap<TType, SortedSet<Rule>>();
        for (Map.Entry<TType, List<BuilderType>> ruleBuilderListPerTrigger : registeredBuilderMap.entrySet()) {
            if (actionRules.get(ruleBuilderListPerTrigger.getKey()) == null){
                SortedSet<Rule> ruleSet = new TreeSet<Rule>(new RuleServiceSupport.RuleComparator());
                actionRules.put(ruleBuilderListPerTrigger.getKey(), ruleSet);
            }
            SortedSet<Rule> ruleSet = actionRules.get(ruleBuilderListPerTrigger.getKey());
            for (RuleBuilder<?, V> builder : ruleBuilderListPerTrigger.getValue()) {
                ruleSet.add(builder.buildRule());
            }
        }
        return createRuleServiceSupport(actionRules);
    }

    protected abstract RuleServiceSupport<TType> createRuleServiceSupport(Map<TType, SortedSet<Rule>> actionRules);

    @Override
    final public void afterPropertiesSet() throws Exception {
        configure();
    }

    final public BuilderType rule(TType trigger){
        BuilderType builder = createBuilder(trigger);
        if (registeredBuilderMap.get(trigger) == null){
            List<BuilderType> builderList = new ArrayList<BuilderType>(3);
            registeredBuilderMap.put(trigger, builderList);
        }
        registeredBuilderMap.get(trigger).add(builder);
        return builder;
    };

    abstract protected BuilderType createBuilder(TType trigger);
    abstract protected void configure();

    public static <T> ExactMatchStrategy<T> equalTo(ExpectedValueHolder<T> second){
        return new ExactMatchStrategy<T>(second);
    }

    public static  <T> ExactMatchStrategy<T> equalTo(T second){
        return new ExactMatchStrategy<T>(valueOf(second));
    }

    public static  <T> ExactMatchStrategy<T> nullValue(Class<T> clazz){
        return new ExactMatchStrategy<T>(valueOf((T)null));
    }

    public static  <T> CallBackUserDetailsMatcher<T> is(CallBackUserDetailsMatcher.UserDetailHolder<T> userDetailHolder, MatchStrategy<T> matchStrategy){
        return new CallBackUserDetailsMatcher<T>(userDetailHolder, matchStrategy);
    }

    public static  <T> AndMatcher<T> and(Matcher<T>... matchers){
        return new AndMatcher<T>(matchers);
    }

    public static  <T> OrMatcher<T> or(Matcher<T> ... matchers){
        return new OrMatcher<T>(matchers);
    }

    public  static <T> NotMatcher<T> not(Matcher<T> matcher){
        return new NotMatcher<T>(matcher);
    }

    public static OldUserMatcher withOldUser(Matcher<User> matcher){
        return new OldUserMatcher(matcher);
    }
    //user details

    public static  CallBackUserDetailsMatcher.UserDetailHolder<String> userDeviceTypeName() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<String>("user.deviceType") {
            @Override
            public String getUserDetail(User user) {
                return user.getDeviceType() != null ? user.getDeviceType().getName() : null;
            }
        };
    }

    public static  CallBackUserDetailsMatcher.UserDetailHolder<SegmentType> userSegment() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<SegmentType>("user.segment") {
            @Override
            public SegmentType getUserDetail(User user) {
                return user.getSegment();
            }
        };
    }

    public static   CallBackUserDetailsMatcher.UserDetailHolder<ProviderType> userProviderType() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<ProviderType>("user.providerType") {
            @Override
            public ProviderType getUserDetail(User user) {
                return user.getProvider();
            }
        };
    }

    public static  CallBackUserDetailsMatcher.UserDetailHolder<String> userCommunityRewriteUrl() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<String>("user.communityRewriteUrl") {
            @Override
            public String getUserDetail(User user) {
                return user.getCommunityRewriteUrl();
            }
        };
    }

    public  static CallBackUserDetailsMatcher.UserDetailHolder<Boolean> userCurrentPaymentDetailsActivated() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<Boolean>("user.paymentDetailsActivated") {
            @Override
            public Boolean getUserDetail(User user) {
                return user.getCurrentPaymentDetails() != null ? user.getCurrentPaymentDetails().isActivated() : null;
            }
        };
    }

    public static  CallBackUserDetailsMatcher.UserDetailHolder<PaymentDetails> userCurrentPaymentDetails() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<PaymentDetails>("user.currentPaymentDetails") {
            @Override
            public PaymentDetails getUserDetail(User user) {
                return user.getCurrentPaymentDetails();
            }
        };
    }

    public static  CallBackUserDetailsMatcher.UserDetailHolder<Long> userFreeTrialExpiredMillis() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<Long>("user.freeTrialExpiredMillis") {
            @Override
            public Long getUserDetail(User user) {
                return user.getFreeTrialExpiredMillis();
            }
        };
    }

    public static  CallBackUserDetailsMatcher.UserDetailHolder<UserStatus> userStatus() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<UserStatus>("user.status") {
            @Override
            public UserStatus getUserDetail(User user) {
                return user.getStatus();
            }
        };
    }

    public static  CallBackUserDetailsMatcher.UserDetailHolder<User> oldUser() {
        return new CallBackUserDetailsMatcher.UserDetailHolder<User>("oldUser") {
            @Override
            public User getUserDetail(User user) {
                return user.getOldUser();
            }
        };
    }
}
