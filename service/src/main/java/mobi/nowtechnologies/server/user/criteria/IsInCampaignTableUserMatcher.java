package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class IsInCampaignTableUserMatcher implements Matcher<User> {

    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    public IsInCampaignTableUserMatcher(SubscriptionCampaignRepository subscriptionCampaignRepository) {
        this.subscriptionCampaignRepository = subscriptionCampaignRepository;
    }

    @Override
    public boolean match(User user){
        if (user.getMobile() == null){
            return false;
        }
        return subscriptionCampaignRepository.getCountForMobile(user.getMobile()) > 0;
    }

}
