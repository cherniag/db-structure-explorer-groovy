package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;

import static mobi.nowtechnologies.server.user.criteria.AndMatcher.and;
import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class AndMatcherTest {

    private AndMatcher<User> andMatcher;

    @Test
    public void testAndMatchWith2MatchingArgs() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        andMatcher = and(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
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
        andMatcher = and(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2, ReflectionUserDetailMatcher3);
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
        andMatcher = and( and(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2), ReflectionUserDetailMatcher3);
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
        andMatcher = and(ReflectionUserDetailMatcher1, callBackUserDetailsMatcher);
        User user = new User();
        user.setMobile("+44123456789");
        user.setTariff(Tariff._4G);
        assertThat(andMatcher.match(user), is(true));
    }
}
