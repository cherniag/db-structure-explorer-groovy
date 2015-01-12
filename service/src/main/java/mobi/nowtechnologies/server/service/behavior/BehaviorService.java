package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;
import java.util.Date;

public class BehaviorService {
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
    @Resource
    BehaviorInfoService behaviorInfoService;

    private String activationDatePropertyName;

    public boolean isFreemiumActivated(User user, Date now) {
        return getFeatureActivationDate(user).before(now);
    }

    public UserReferralsSnapshot getSnapshot(User user) {
        return behaviorInfoService.getUserReferralsSnapshot(user);
    }

    public void setActivationDatePropertyName(String activationDatePropertyName) {
        this.activationDatePropertyName = activationDatePropertyName;
    }

    private Date getFeatureActivationDate(User user) {
        String communityName = user.getCommunityRewriteUrl();

        return communityResourceBundleMessageSource.readDate(communityName, activationDatePropertyName);
    }
}
