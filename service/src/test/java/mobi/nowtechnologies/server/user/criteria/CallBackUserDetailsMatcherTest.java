package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.junit.Test;

import static mobi.nowtechnologies.server.user.criteria.CallBackUserDetailsMatcher.UserDetailHolder;
import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.greaterThan;
import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.currentTimestamp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/11/2014
 */
public class CallBackUserDetailsMatcherTest {

    private CallBackUserDetailsMatcher<Long> callBackUserDetailsMatcher;

    @Test
    public void testMatch() throws Exception {
        callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Long>(freeTrialExpired(), greaterThan(currentTimestamp()));
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() + 1000L);
        assertThat(callBackUserDetailsMatcher.match(user), is(true));
    }

    @Test
    public void testNotMatch() throws Exception {
        callBackUserDetailsMatcher = new CallBackUserDetailsMatcher<Long>(freeTrialExpired(), greaterThan(currentTimestamp()));
        User user = new User();
        user.setFreeTrialExpiredMillis(System.currentTimeMillis() - 1000L);
        assertThat(callBackUserDetailsMatcher.match(user), is(false));
    }

    private UserDetailHolder<Long> freeTrialExpired(){
         return new UserDetailHolder<Long>() {
            @Override
            public Long getUserDetail(User user) {
                return user.getFreeTrialExpiredMillis();
            }
        };
    }
}
