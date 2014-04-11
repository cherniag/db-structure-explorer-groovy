package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.junit.Test;

import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class NotMatcherTest {

    private NotMatcher<User> userNotMatcher;
    @Test
    public void testMatch() throws Exception {
        ReflectionUserDetailMatcher reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", equalTo(), null);
        userNotMatcher = new NotMatcher<User>(reflectionUserDetailMatcher);
        User user = new User();
        user.setMobile(null);
        assertThat(reflectionUserDetailMatcher.match(user), is(true));
        assertThat(userNotMatcher.match(user), is(false));

    }
}
