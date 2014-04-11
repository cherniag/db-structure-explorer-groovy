package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/11/2014
 */
public class CallBackUserDetailsMatcher<T> implements Matcher<User> {
    private UserDetailHolder<T> userDetailHolder;
    private MatchStrategy<T> matchStrategy;

    public CallBackUserDetailsMatcher(UserDetailHolder<T> userDetailHolder, MatchStrategy<T> matchStrategy) {
        this.userDetailHolder = userDetailHolder;
        this.matchStrategy = matchStrategy;
    }

    @Override
    public boolean match(User user) {
        T actual = userDetailHolder.getUserDetail(user);
        return matchStrategy.match(actual);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userDetailHolder", userDetailHolder)
                .append("matchStrategy", matchStrategy)
                .toString();
    }

    public static <T> CallBackUserDetailsMatcher<T> is(UserDetailHolder<T> userDetailHolder, MatchStrategy<T> matchStrategy){
        return new CallBackUserDetailsMatcher<T>(userDetailHolder, matchStrategy);
    }

    public static <T> CallBackUserDetailsMatcher<T> isNull(UserDetailHolder<T> userDetailHolder){
        return new CallBackUserDetailsMatcher<T>(userDetailHolder, ExactMatchStrategy.equalTo(ExpectedValueHolder.<T>nullValue()));
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
