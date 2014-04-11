package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class ReflectionUserDetailMatcher<T> implements Matcher<User> {

    private String fieldName;
    private T expectedValue;
    private MatchStrategy<T> matchStrategy;

    public ReflectionUserDetailMatcher(String fieldName, MatchStrategy<T> matchStrategy, T expectedValue) {
        this.fieldName = fieldName;
        this.expectedValue = expectedValue;
        this.matchStrategy = matchStrategy;
    }

    @Override
    public boolean match(User user) {
        try {
            Object actualValue = ReflectionHelper.getFieldValue(user, fieldName);
            return matchStrategy.match((T) actualValue, expectedValue);
        } catch (Exception e){
            throw new MatchException(e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fieldName", fieldName)
                .append("matchStrategy", matchStrategy)
                .append("expectedValue", expectedValue)
                .toString();
    }
}
