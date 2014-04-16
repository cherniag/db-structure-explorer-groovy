package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.Matcher;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/9/2014
 */
public class SubscriptionCampaignUserRule implements Rule<User, Boolean> {
    private Matcher<User> rootMatcher;
    private boolean isValid = true;
    private int rulePriority;
    private boolean result = true;

    public SubscriptionCampaignUserRule(Matcher<User> rootMatcher, int rulePriority, boolean resultValue) {
        this.rootMatcher = rootMatcher;
        this.rulePriority = rulePriority;
        this.result = resultValue;
    }

    @Override
    public Matcher getRootMatcher() {
        return rootMatcher;
    }

    @Override
    public RuleResult<Boolean> getResult() {
        return new RuleResult<Boolean>(true, result);
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
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("rootMatcher", rootMatcher)
                .append("isValid", isValid)
                .append("rulePriority", rulePriority)
                .toString();
    }
}
