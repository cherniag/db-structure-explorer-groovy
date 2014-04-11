package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.junit.Test;

import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
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
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        OrMatcher matcher = new OrMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2));
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher("deviceType", equalTo(), "IOS");
        orMatcher = new OrMatcher(Lists.<Matcher>newArrayList(matcher, ReflectionUserDetailMatcher3));
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(100);
        boolean match = orMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testOrMatcherWithOneMatching() throws Exception {
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 123);
        orMatcher = new OrMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2));
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(0);
        boolean match = orMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testOrMatcherWithBothMatching() throws Exception {
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        orMatcher = new OrMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2));
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        boolean match = orMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testOrMatcherWithNoneMatching() throws Exception {
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        orMatcher = new OrMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2));
        User user = new User();
        user.setMobile("+44000000000");
        user.setSubBalance(0);
        boolean match = orMatcher.match(user);
        assertThat(match, is(false));
    }
}
