package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.User;

import java.util.Map;
import java.util.SortedSet;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class AutoOptInRuleService {

    public enum AutoOptInTriggerType implements TriggerType {
        ACC_CHECK;
    }

    private final RuleServiceSupport ruleServiceSupport;

    public AutoOptInRuleService(Map<TriggerType, SortedSet<Rule>> actionRules) {
        ruleServiceSupport = new RuleServiceSupport(actionRules);
    }

     public RuleResult<Boolean> fireRules(AutoOptInTriggerType triggerType, User user){
         return ruleServiceSupport.fireRules(triggerType, user);
     }
}
