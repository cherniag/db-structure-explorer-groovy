package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;

import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.lessThan;
import static mobi.nowtechnologies.server.user.criteria.ExactMatchStrategy.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class ReflectionUserDetailMatcherTest {
    private ReflectionUserDetailMatcher reflectionUserDetailMatcher;

    @Test
    public void testMatchExistingPrimitiveField() throws Exception {
        reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("subBalance", equalTo(), 9);
        new ReflectionUserDetailMatcher("nextSubPayment", lessThan(), 1555555L);
        User user = new User();
        user.setSubBalance(9);
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchExistingEnumField() throws Exception {
        reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("tariff", equalTo(), Tariff._4G);
        User user = new User();
        user.setTariff(Tariff._4G);
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMatchExistingStringField() throws Exception {
        reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", equalTo(), "+447123456789");
        User user = new User();
        user.setMobile("+447123456789");
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }

    @Test
    public void testMismatchExistingField() throws Exception {
        reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", equalTo(), "0000");
        User user = new User();
        user.setMobile("+447123456789");
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(false));
    }

    @Test(expected = MatchException.class)
    public void testMatchNonExistingField() throws Exception {
        reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("Not exist", equalTo(), "Value");
        User user = new User();
        reflectionUserDetailMatcher.match(user);
    }

    @Test
    public void testMatchNulls() throws Exception {
        reflectionUserDetailMatcher = new ReflectionUserDetailMatcher("mobile", equalTo(), null);
        User user = new User();
        user.setMobile(null);
        boolean match = reflectionUserDetailMatcher.match(user);
        assertThat(match, is(true));
    }
}
