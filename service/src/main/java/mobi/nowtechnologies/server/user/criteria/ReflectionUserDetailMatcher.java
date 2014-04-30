package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class ReflectionUserDetailMatcher<T> implements Matcher<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallBackUserDetailsMatcher.class);

    private String fieldName;
    private MatchStrategy<T> matchStrategy;

    public ReflectionUserDetailMatcher(String fieldName, MatchStrategy<T> matchStrategy) {
        this.fieldName = fieldName;
        this.matchStrategy = matchStrategy;
    }

    @Override
    public boolean match(User user) {
        try {
            Object actualValue = ReflectionHelper.getFieldValue(user, fieldName);
            LOGGER.debug("Matching field [{}] value [{}] with strategy [{}]...", fieldName, actualValue, matchStrategy);
            boolean result = matchStrategy.match((T) actualValue);
            LOGGER.debug("Result [{}]", result);
            return result;
        } catch (Exception e){
            throw new MatchException(e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fieldName", fieldName)
                .append("matchStrategy", matchStrategy)
                .toString();
    }
}
