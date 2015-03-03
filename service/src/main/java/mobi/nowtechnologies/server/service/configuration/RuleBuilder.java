package mobi.nowtechnologies.server.service.configuration;

import mobi.nowtechnologies.server.user.rules.Rule;

public interface RuleBuilder<T, V> {

    Rule<T, V> buildRule();
}
