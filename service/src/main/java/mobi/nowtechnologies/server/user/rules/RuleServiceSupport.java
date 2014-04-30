package mobi.nowtechnologies.server.user.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import static mobi.nowtechnologies.server.user.rules.RuleResult.FAIL_RESULT;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class RuleServiceSupport <TT extends TriggerType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceSupport.class);
    private Map<TT, SortedSet<Rule>> actionRules = new HashMap<TT, SortedSet<Rule>>();

    public RuleServiceSupport(Map<TT, SortedSet<Rule>> actionRules) {
        this.actionRules = actionRules;
    }

    public <T, R> RuleResult<R> fireRules(TT actionType, T arg){
        SortedSet<Rule> rules = actionRules.get(actionType);
        LOGGER.info("Found {} rules for trigger type {}", rules == null ? "null" : rules.size(), actionType);
        if(rules == null){
            return FAIL_RESULT;
        }
        for (Rule rule : rules) {
            LOGGER.debug("Evaluating rule {}", rule);
            if(rule.isValid() && rule.getRootMatcher().match(arg)) {
                return rule.getResult();
            }
        }
        return FAIL_RESULT;
    }

    public static class RuleComparator implements Comparator<Rule> {
        @Override
        public int compare(Rule o1, Rule o2) {
            return o2.getPriority() - o1.getPriority();
        }
    }
}
