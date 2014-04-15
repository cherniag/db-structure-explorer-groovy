package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.user.criteria.Matcher;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * @author Titov Mykhaylo (titov) on 14.04.2014.
 */
public class PromotionRule implements Rule<User, Promotion> {

    private Matcher<User> rootMatcher;
    private int rulePriority;
    private Promotion promo;

    public PromotionRule(Matcher<User> rootMatcher, int rulePriority, Promotion promo) {
        this.rootMatcher = rootMatcher;
        this.rulePriority = rulePriority;
        this.promo = promo;
    }

    @Override
    public Matcher getRootMatcher() {
        return rootMatcher;
    }

    @Override
    public RuleResult<Promotion> getResult() {
        return new RuleResult<Promotion>(true, promo);
    }

    @Override
    public int getPriority() {
        return rulePriority;
    }

    @Override
    public boolean isValid() {
        return promo.getIsActive() && promo.getEndDate()> Utils.getEpochSeconds();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("rootMatcher", rootMatcher)
                .append("isValid", isValid())
                .append("rulePriority", rulePriority)
                .append("promo", promo)
                .toString();
    }
}
