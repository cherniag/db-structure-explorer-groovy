package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.Matcher;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
public class UserRule implements Rule<User, Boolean> {
    private Matcher<User> rootMatcher;

    public UserRule(Matcher<User> rootMatcher) {
        this.rootMatcher = rootMatcher;
    }

    @Override
    public Matcher getRootMatcher() {
        return rootMatcher;
    }

    @Override
    public RuleResult<Boolean> getResult() {
        return new RuleResult<Boolean>(true, true);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
