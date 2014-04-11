package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class AutoOptInRuleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoOptInRuleService.class);

    public enum AutoOptInTriggerType implements TriggerType {
        ACC_CHECK;
    }

    private final RuleServiceSupport ruleServiceSupport;

    public AutoOptInRuleService(Map<TriggerType, SortedSet<Rule>> actionRules) {
        ruleServiceSupport = new RuleServiceSupport(actionRules);
    }

     public RuleResult<Boolean> fireRules(AutoOptInTriggerType triggerType, User user){
         LOGGER.info("Firing rules for trigger type {} and user id {}", triggerType, user.getId());
         RuleResult<Boolean> ruleResult = ruleServiceSupport.fireRules(triggerType, user);
         LOGGER.info("Rule result {}", ruleResult);
         return ruleResult;
     }
}
