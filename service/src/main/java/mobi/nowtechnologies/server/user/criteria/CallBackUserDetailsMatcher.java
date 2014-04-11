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
    private ExpectedValueHolder<T> expectedValueHolder;
    private MatchStrategy<T> matchStrategy;

    public CallBackUserDetailsMatcher(UserDetailHolder<T> userDetailHolder, MatchStrategy<T> matchStrategy, ExpectedValueHolder<T> expectedValueHolder) {
        this.userDetailHolder = userDetailHolder;
        this.matchStrategy = matchStrategy;
        this.expectedValueHolder = expectedValueHolder;
    }

    @Override
    public boolean match(User user) {
        T actual = userDetailHolder.getUserDetail(user);
        T expected = expectedValueHolder.getValue();
        return matchStrategy.match(actual, expected);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userDetailHolder", userDetailHolder)
                .append("matchStrategy", matchStrategy)
                .append("expectedValue", expectedValueHolder)
                .toString();
    }

    public static <T> CallBackUserDetailsMatcher<T> is(UserDetailHolder<T> userDetailHolder, MatchStrategy<T> matchStrategy, ExpectedValueHolder<T> expectedValueHolder){
        return new CallBackUserDetailsMatcher<T>(userDetailHolder, matchStrategy, expectedValueHolder);
    }

    public static <T> CallBackUserDetailsMatcher<T> isNull(UserDetailHolder<T> userDetailHolder){
        return new CallBackUserDetailsMatcher<T>(userDetailHolder, ExactMatchStrategy.<T>equalTo(), new ExpectedValueHolder<T>() {
            @Override
            public T getValue() {
                return null;
            }
        });
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

    public static abstract class ExpectedValueHolder<T>{
        public abstract T getValue();

        @Override
        public String toString() {
            return String.valueOf(getValue());
        }

        public static <T> ExpectedValueHolder<T> valueOf(final T value){
            return new ExpectedValueHolder<T>(){

                @Override
                public T getValue() {
                    return value;
                }
            };
        }

        public static ExpectedValueHolder<Long> now() {
            return new ExpectedValueHolder<Long>() {
                @Override
                public Long getValue() {
                    return System.currentTimeMillis();
                }
            };
        }
    }
}
