package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.user.criteria.Matcher;

/**
 * Author: Gennadii Cherniaiev Date: 4/8/2014
 */
public interface Rule<T, R> {

    Matcher<T> getRootMatcher();

    RuleResult<R> getResult();

    int getPriority();

    boolean isValid();
}
