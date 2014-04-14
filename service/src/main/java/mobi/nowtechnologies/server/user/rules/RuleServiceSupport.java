package mobi.nowtechnologies.server.user.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class RuleServiceSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceSupport.class);
    private Map<TriggerType, SortedSet<Rule>> actionRules = new HashMap<TriggerType, SortedSet<Rule>>();

    public RuleServiceSupport(Map<TriggerType, SortedSet<Rule>> actionRules) {
        this.actionRules = actionRules;
    }

    public <T, R> RuleResult<R> fireRules(TriggerType actionType, T arg){
        SortedSet<Rule> rules = actionRules.get(actionType);
        LOGGER.info("Found {} rules for trigger type {}", rules == null ? "null" : rules.size(), actionType);
        if(rules == null){
            return RuleResult.FAIL_RESULT;
        }
        for (Rule rule : rules) {
            LOGGER.info("Evaluating rule {}", rule);
            if(rule.isValid() && rule.getRootMatcher().match(arg)) {
                return rule.getResult();
            }
        }
        return RuleResult.FAIL_RESULT;
    }

    public static class RuleComparator implements Comparator<Rule> {
        @Override
        public int compare(Rule o1, Rule o2) {
            return o2.getPriority() - o1.getPriority();
        }
    }
}
