package mobi.nowtechnologies.server.service.configuration;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.user.criteria.*;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import mobi.nowtechnologies.server.user.rules.TriggerType;
import org.junit.Test;

import java.util.Map;

import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.greaterThan;
import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.currentTimestamp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static mobi.nowtechnologies.server.service.configuration.Configuration.*;

public class ConfigurationUtilsTest {


    @Test
    public void testMatch() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher<String>("mobile", nullValue(String.class));
        Matcher<User> userNotMatcher = not(reflectionUserDetailMatcher);
        User user = new User();
        user.setMobile(null);
        assertThat(reflectionUserDetailMatcher.match(user), is(true));
        assertThat(userNotMatcher.match(user), is(false));

    }

    @Test
    public void testOrMatcherComplex() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User> ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher<String>("deviceType", equalTo("IOS"));
        Matcher<User> orMatcher = or( or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2), ReflectionUserDetailMatcher3);
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(100);
        boolean match = orMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testOrMatcherWithOneMatching() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(123));
        Matcher<User> orMatcher = or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(0);
        boolean match = orMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testOrMatcherWithBothMatching() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User> orMatcher = or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        boolean match = orMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testOrMatcherWithNoneMatching() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User> orMatcher = or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
        User user = new User();
        user.setMobile("+44000000000");
        user.setSubBalance(0);
        boolean match = orMatcher.match(user);
        assertThat(match, is(false));
    }

    @Test
    public void testMatchExistingPrimitiveField() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("subBalance", equalTo(9));
        User user = new User();
        user.setSubBalance(9);
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchExistingEnumField() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("tariff", equalTo(Tariff._4G));
        User user = new User();
        user.setTariff(Tariff._4G);
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchExistingStringField() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", equalTo("+447123456789"));
        User user = new User();
        user.setMobile("+447123456789");
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMismatchExistingField() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", equalTo("0000"));
        User user = new User();
        user.setMobile("+447123456789");
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(false));
    }

    @Test(expected = MatchException.class)
    public void testMatchNonExistingField() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("Not exist", equalTo("Value"));
        User user = new User();
        reflectionUserDetailMatcher.match(user);
    }

    @Test
    public void testMatchNullsMacth() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", nullValue(String.class));
        User user = new User();
        user.setMobile(null);
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchNullsNotMatch() throws Exception {
        Matcher<User> reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", nullValue(String.class));
        User user = new User();
        user.setMobile("+447788992556");
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(false));
    }

    @Test
    public void testAndMatchWith2MatchingArgs() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User> andMatcher = and(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        boolean match = andMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testAndMatchWith3MatchingArgs() throws Exception {
        Matcher<User>  ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User>  ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User>  ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher<String>("device", equalTo("IOS"));
        Matcher<User>  andMatcher = and(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2, ReflectionUserDetailMatcher3);
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        user.setDevice("IOS");
        boolean match = andMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchAnd() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User> ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher<String>("device", equalTo("IOS"));
        Matcher<User> andMatcher = and( and(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2), ReflectionUserDetailMatcher3);
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        user.setDevice("IOS");
        boolean match = andMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testSeveralMatcherTypes() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo( "+44123456789"));
        Matcher<User> callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Tariff>(new CallBackUserDetailsMatcher.UserDetailHolder<Tariff>() {
            @Override
            public Tariff getUserDetail(User user) {
                return user.getTariff();
            }
        }, equalTo(Tariff._4G));
        Matcher<User>  andMatcher = and(ReflectionUserDetailMatcher1, callBackUserDetailsMatcher);
        User user = new User();
        user.setMobile("+44123456789");
        user.setTariff(Tariff._4G);
        assertThat(andMatcher.match(user), is(true));
    }


    @Test
    public void testUserDetailsMatch() throws Exception {
        Matcher<User> callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Long>(freeTrialExpired(), greaterThan(currentTimestamp()));
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() + 1000L);
        assertThat(callBackUserDetailsMatcher.match(user), is(true));
    }

    @Test
    public void testUserDetailsNotMatch() throws Exception {
        Matcher<User> callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Long>(freeTrialExpired(), greaterThan(currentTimestamp()));
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() - 1000L);
        assertThat(callBackUserDetailsMatcher.match(user), is(false));
    }

    @Test
    public void testUserDetailsMatchNull() throws Exception {
        Matcher<User> callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Long>(freeTrialExpired(), nullValue(Long.class));
        User user = new User();
        user.setFreeTrialExpiredMillis(null);
        assertThat(callBackUserDetailsMatcher.match(user), is(true));
    }

    @Test
    public void testUserDetailsNotMatchNull() throws Exception {
        Matcher<User> callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Long>(freeTrialExpired(), nullValue(Long.class));
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());
        assertThat(callBackUserDetailsMatcher.match(user), is(false));
    }

    private CallBackUserDetailsMatcher.UserDetailHolder<Long> freeTrialExpired(){
        return new CallBackUserDetailsMatcher.UserDetailHolder<Long>() {
            @Override
            public Long getUserDetail(User user) {
                return user.getFreeTrialExpiredMillis();
            }
        };
    }

}
