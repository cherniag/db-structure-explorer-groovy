package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.configuration.AbstractRuleBuilder;
import mobi.nowtechnologies.server.user.rules.Rule;


public class PromotionRuleBuilder extends AbstractRuleBuilder<User, PromotionProvider.PromotionProxy> {

    private int priority;


    public PromotionRuleBuilder() {}

    @Override
    public Rule<User, PromotionProvider.PromotionProxy> buildRule() {
        return new PromotionRule(getUserMatcher(), priority, getResult());
    }

    public PromotionRuleBuilder priority(int i) {
        priority = i;
        return this;
    }
}
