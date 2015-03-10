package mobi.nowtechnologies.server.user.autooptin;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.configuration.ConfigurationAwareService;
import mobi.nowtechnologies.server.user.rules.RuleResult;
import mobi.nowtechnologies.server.user.rules.TriggerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */

public class AutoOptInRuleService extends ConfigurationAwareService<AutoOptInRuleService.AutoOptInTriggerType, Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoOptInRuleService.class);

    public boolean isSubjectToAutoOptIn(AutoOptInTriggerType triggerType, User user) {
        LOGGER.info("Firing rules for trigger type {} and user id {}", triggerType, user.getId());
        RuleResult<Boolean> ruleResult = getRuleServiceSupport().fireRules(triggerType, user);
        LOGGER.info("Rule result {}", ruleResult);
        if (ruleResult.isSuccessful()) {
            return ruleResult.getResult();
        } else {
            return user.isSubjectToAutoOptIn();
        }
    }


    public static enum AutoOptInTriggerType implements TriggerType {
        ALL, EMPTY;
    }

}
