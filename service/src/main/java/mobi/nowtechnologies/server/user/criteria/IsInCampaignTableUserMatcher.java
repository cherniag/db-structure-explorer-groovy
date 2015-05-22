package mobi.nowtechnologies.server.user.criteria;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev Date: 4/8/2014
 */
public class IsInCampaignTableUserMatcher implements Matcher<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsInCampaignTableUserMatcher.class);

    private SubscriptionCampaignRepository subscriptionCampaignRepository;
    private String campaignId;

    public IsInCampaignTableUserMatcher(SubscriptionCampaignRepository subscriptionCampaignRepository, String campaignId) {
        this.subscriptionCampaignRepository = subscriptionCampaignRepository;
        this.campaignId = campaignId;
    }

    @Override
    public boolean match(User user) {
        LOGGER.debug("Matching user with mobile [{}] and campaignId [{}]...", user.getMobile(), campaignId);
        if (user.getMobile() == null) {
            return false;
        }
        long countForMobile = subscriptionCampaignRepository.countForMobile(user.getMobile(), campaignId);
        LOGGER.debug("Result [{}] records", countForMobile);
        return countForMobile > 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("campaignId", campaignId).toString();
    }
}
