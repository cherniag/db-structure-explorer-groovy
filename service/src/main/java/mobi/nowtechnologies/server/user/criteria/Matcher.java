package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public interface Matcher<T extends MatchingDetails> {
    boolean match(User user, T matchingDetails);
}
