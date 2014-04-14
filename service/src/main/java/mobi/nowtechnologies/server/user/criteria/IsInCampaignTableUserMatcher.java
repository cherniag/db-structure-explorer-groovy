package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class IsInCampaignTableUserMatcher implements Matcher<User> {

    private SubscriptionCampaignRepository subscriptionCampaignRepository;
    private String campaignId;

    public IsInCampaignTableUserMatcher(SubscriptionCampaignRepository subscriptionCampaignRepository, String campaignId) {
        this.subscriptionCampaignRepository = subscriptionCampaignRepository;
        this.campaignId = campaignId;
    }

    @Override
    public boolean match(User user){
        if (user.getMobile() == null){
            return false;
        }
        return subscriptionCampaignRepository.getCountForMobile(user.getMobile(), campaignId) > 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("campaignId", campaignId)
                .toString();
    }
}
