package mobi.nowtechnologies.server.service.configuration;

import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.TriggerType;

public interface RuleBuilder <T, V> {
    Rule<T,V> buildRule();
}
