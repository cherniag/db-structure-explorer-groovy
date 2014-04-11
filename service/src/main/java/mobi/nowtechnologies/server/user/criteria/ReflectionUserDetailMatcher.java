package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class ReflectionUserDetailMatcher implements Matcher<User> {

    private String fieldName;
    private Object expectedValue;
    private MatchStrategy matchStrategy;

    public ReflectionUserDetailMatcher(String fieldName, MatchStrategy matchStrategy, Object expectedValue) {
        this.fieldName = fieldName;
        this.expectedValue = expectedValue;
        this.matchStrategy = matchStrategy;
    }

    @Override
    public boolean match(User user) {
        try {
            Object actualValue = ReflectionHelper.getFieldValue(user, fieldName);
            return matchStrategy.match(actualValue, expectedValue);
        } catch (Exception e){
            throw new MatchException(e);
        }
    }
}