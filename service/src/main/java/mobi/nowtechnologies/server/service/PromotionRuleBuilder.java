package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.configuration.AbstractRuleBuilder;
import mobi.nowtechnologies.server.user.rules.Rule;


public class PromotionRuleBuilder extends AbstractRuleBuilder<User, Promotion> {

    private int priority;


    public PromotionRuleBuilder() {}

    @Override
    public Rule<User, Promotion> buildRule() {
        return new PromotionRule(getUserMatcher(), priority, getResult());
    }

    public PromotionRuleBuilder priority(int i) {
       priority = i;
       return this;
    }
}