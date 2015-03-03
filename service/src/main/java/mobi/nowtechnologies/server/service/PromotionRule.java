package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.Matcher;
import mobi.nowtechnologies.server.user.rules.PromotionBasedValidationDelegate;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.ValidationDelegate;

import org.apache.commons.lang.builder.ToStringBuilder;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * @author Titov Mykhaylo (titov) on 14.04.2014.
 */
public class PromotionRule implements Rule<User, PromotionProvider.PromotionProxy> {

    private Matcher<User> rootMatcher;
    private int rulePriority;
    private PromotionProvider.PromotionProxy promo;
    private ValidationDelegate validationDelegate;

    public PromotionRule(Matcher<User> rootMatcher, int rulePriority, PromotionProvider.PromotionProxy promoProxy) {
        this.rootMatcher = rootMatcher;
        this.rulePriority = rulePriority;
        this.promo = promoProxy;
        this.validationDelegate = new PromotionBasedValidationDelegate(promo);
    }

    @Override
    public Matcher getRootMatcher() {
        return rootMatcher;
    }

    @Override
    public RuleResult<PromotionProvider.PromotionProxy> getResult() {
        return new RuleResult<PromotionProvider.PromotionProxy>(true, promo);
    }

    @Override
    public int getPriority() {
        return rulePriority;
    }

    @Override
    public boolean isValid() {
        return validationDelegate.isValid();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append("rootMatcher", rootMatcher).append("validation", validationDelegate).append("isValid", isValid())
                                                            .append("rulePriority", rulePriority).append("promo", promo).toString();
    }
}
