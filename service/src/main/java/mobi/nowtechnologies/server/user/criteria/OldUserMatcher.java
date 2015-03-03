package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Author: Gennadii Cherniaiev Date: 4/15/2014
 */
public class OldUserMatcher implements Matcher<User> {

    private Matcher<User> matcher;

    public OldUserMatcher(Matcher<User> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(User value) {
        return matcher.match(value.getOldUser());
    }
}
