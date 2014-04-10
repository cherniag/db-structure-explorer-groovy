package mobi.nowtechnologies.server.user.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class RuleServiceSupport {
    private Map<TriggerType, SortedSet<Rule>> actionRules = new HashMap<TriggerType, SortedSet<Rule>>();

    public RuleServiceSupport(Map<TriggerType, SortedSet<Rule>> actionRules) {
        this.actionRules = actionRules;
    }

    public <T, R> RuleResult<R> fireRules(TriggerType actionType, T arg){
        SortedSet<Rule> rules = actionRules.get(actionType);
        if(rules == null){
            return RuleResult.FAIL_RESULT;
        }
        for (Rule rule : rules) {
            if(rule.isValid() && rule.getRootMatcher().match(arg)) {
                return rule.getResult();
            }
        }
        return RuleResult.FAIL_RESULT;
    }
}
