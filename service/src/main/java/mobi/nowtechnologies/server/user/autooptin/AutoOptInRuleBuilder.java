package mobi.nowtechnologies.server.user.autooptin;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PromotionProvider;
import mobi.nowtechnologies.server.service.configuration.AbstractRuleBuilder;
import mobi.nowtechnologies.server.user.rules.PromotionBasedValidationDelegate;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.SubscriptionCampaignUserRule;
import mobi.nowtechnologies.server.user.rules.ValidationDelegate;

import org.springframework.util.Assert;


public class AutoOptInRuleBuilder extends AbstractRuleBuilder<User, Boolean> {

    private int priority = -1;
    private ValidationDelegate validationDelegate;


    public AutoOptInRuleBuilder() {}

    @Override
    public Rule<User, Boolean> buildRule() {
        return new SubscriptionCampaignUserRule(getUserMatcher(), validationDelegate, priority, getResult());
    }

    public AutoOptInRuleBuilder priority(int i) {
        priority = i;
        return this;
    }

    public AutoOptInRuleBuilder validAsPer(PromotionProvider.PromotionProxy promotion) {
        Assert.notNull(promotion);
        validationDelegate = new PromotionBasedValidationDelegate(promotion);
        return this;
    }

}
