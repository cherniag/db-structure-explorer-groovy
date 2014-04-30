package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/11/2014
 */
public class CallBackUserDetailsMatcher<T> implements Matcher<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallBackUserDetailsMatcher.class);
    private UserDetailHolder<T> userDetailHolder;
    private MatchStrategy<T> matchStrategy;

    public CallBackUserDetailsMatcher(UserDetailHolder<T> userDetailHolder, MatchStrategy<T> matchStrategy) {
        this.userDetailHolder = userDetailHolder;
        this.matchStrategy = matchStrategy;
    }

    @Override
    public boolean match(User user) {
        T actual = userDetailHolder.getUserDetail(user);
        LOGGER.debug("Matching userDetail [{}] actual [{}] with strategy [{}]...", userDetailHolder, actual, matchStrategy);
        boolean result = matchStrategy.match(actual);
        LOGGER.debug("Result [{}]", result);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userDetailHolder", userDetailHolder)
                .append("matchStrategy", matchStrategy)
                .toString();
    }

    public static abstract class UserDetailHolder<T>{
        private String detailName;

        protected UserDetailHolder(String detailName) {
            this.detailName = detailName;
        }

        protected UserDetailHolder() {
            this("unknown name");
        }

        public abstract T getUserDetail(User user);

        @Override
        public String toString() {
            return detailName;
        }
    }

}
