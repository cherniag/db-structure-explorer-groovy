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
public class AndMatcherTest {

    private AndMatcher andMatcher;

    @Test
    public void testAndMatchWith2MatchingArgs() throws Exception {
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        andMatcher = new AndMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2));
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        boolean match = andMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testAndMatchWith3MatchingArgs() throws Exception {
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher("device", equalTo(), "IOS");
        andMatcher = new AndMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2, ReflectionUserDetailMatcher3));
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        user.setDevice("IOS");
        boolean match = andMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchAnd() throws Exception {
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher1 = new ReflectionUserDetailMatcher("mobile", equalTo(), "+44123456789");
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher2 = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        AndMatcher matcher = new AndMatcher(Lists.<Matcher>newArrayList(ReflectionUserDetailMatcher1, ReflectionUserDetailMatcher2));
        ReflectionUserDetailMatcher ReflectionUserDetailMatcher3 = new ReflectionUserDetailMatcher("device", equalTo(), "IOS");
        andMatcher = new AndMatcher(Lists.<Matcher>newArrayList(matcher, ReflectionUserDetailMatcher3));
        User user = new User();
        user.setMobile("+44123456789");
        user.setSubBalance(9);
        user.setDevice("IOS");
        boolean match = andMatcher.match(user);
        assertThat(match, is(true));
    }
}
