package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.MatchingDetails;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public abstract class RulesManager <T extends RulesManager.Trigger> {

    private static final RuleComparator RULE_COMPARATOR = new RuleComparator();

    private SortedSet<Rule> rules = new TreeSet<Rule>(RULE_COMPARATOR);
    private Map<Trigger, SortedSet<Rule>> ruleMap = new HashMap<Trigger, SortedSet<Rule>>();

   public void addRule(T triggerType, Rule rule){
       Assert.notNull(rule);
       rules.add(rule);
   }

    public <R> R getResult(T trigger, User user, MatchingDetails matchingDetails){
        //TODO : implement
        throw new IllegalArgumentException();
    }

    private static class RuleComparator implements Comparator<Rule> {
        @Override
        public int compare(Rule o1, Rule o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }

    public static interface Trigger {

    }
}
