package mobi.nowtechnologies.server.user.autooptin;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.configuration.AbstractRuleBuilder;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.SubscriptionCampaignUserRule;


public class AutoOptInRuleBuilder extends AbstractRuleBuilder<User, Boolean> {

    private int priority;


    public AutoOptInRuleBuilder() {}

    @Override
    public Rule<User, Boolean> buildRule() {
        return new SubscriptionCampaignUserRule(getUserMatcher(), priority, getResult());
    }

    public AutoOptInRuleBuilder priority(int i) {
       priority = i;
       return this;
    }
}
