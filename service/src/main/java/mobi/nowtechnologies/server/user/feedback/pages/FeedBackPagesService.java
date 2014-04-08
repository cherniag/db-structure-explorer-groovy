package mobi.nowtechnologies.server.user.feedback.pages;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.MatchingDetails;
import mobi.nowtechnologies.server.user.rules.RulesManager;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class FeedBackPagesService {

    private RulesManager <FeedBackUserPagesTrigger> rulesManager;

    public boolean subjectToOptIn(User user){
        return rulesManager.<Boolean>getResult(FeedBackUserPagesTrigger.ACC_CHECK, user, MatchingDetails.NONE);
    }


    public static enum FeedBackUserPagesTrigger implements RulesManager.Trigger{
        ACC_CHECK
    }

}
