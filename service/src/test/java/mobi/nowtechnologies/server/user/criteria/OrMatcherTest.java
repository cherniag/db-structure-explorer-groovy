package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.junit.Test;

import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static mobi.nowtechnologies.server.user.criteria.OrMatcher.or;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class OrMatcherTest {

    private OrMatcher orMatcher;

    @Test
    public void testOrMatcherComplex() throws Exception {
        Matcher<User> ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher<String>("mobile", equalTo("+44123456789"));
        Matcher<User> ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher<Integer>("subBalance", equalTo(9));
        Matcher<User> ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher<String>("deviceType", equalTo("IOS"));
        orMatcher = or( or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2), ReflectionUserDetailMatcher3);
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
        orMatcher = or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
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
        orMatcher = or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
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
        orMatcher = or(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2);
        User user = new User();
        user.setMobile("+44000000000");
        user.setSubBalance(0);
        boolean match = orMatcher.match(user);
        assertThat(match, is(false));
    }
}
