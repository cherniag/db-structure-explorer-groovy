package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.Matcher;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
public class UserRule implements Rule<User, Boolean> {
    private Matcher<User> rootMatcher;
    private boolean isValid = true;
    private int rulePriority;

    public UserRule(Matcher<User> rootMatcher, int rulePriority) {
        this.rootMatcher = rootMatcher;
        this.rulePriority = rulePriority;
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
        return rulePriority;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("rootMatcher", rootMatcher)
                .append("isValid", isValid)
                .append("rulePriority", rulePriority)
                .toString();
    }
}
