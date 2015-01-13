package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.CommunityConfig;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

public class BehaviorInfoService {

    private Logger logger = LoggerFactory.getLogger(BehaviorInfoService.class);

    @Resource
    UserReferralsSnapshotRepository userReferralsSnapshotRepository;
    @Resource
    CommunityConfigRepository communityConfigRepository;
    @Resource
    ReferralRepository referralRepository;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    private String activationDatePropertyName;

    public boolean isFreemiumActivated(User user, Date now) {
        String communityName = user.getCommunityRewriteUrl();
        return communityResourceBundleMessageSource.readDate(communityName, activationDatePropertyName).before(now);
    }

    @Transactional(readOnly = true)
    public UserReferralsSnapshot getUserReferralsSnapshot(User user, BehaviorConfig behaviorConfig) {
        UserReferralsSnapshot existing = userReferralsSnapshotRepository.findOne(user.getId());
        if (existing == null) {
            UserReferralsSnapshot newOne = new UserReferralsSnapshot(
                    user.getId(), behaviorConfig.getRequiredReferrals(), behaviorConfig.getReferralsDuration());
            int referredAndConfirmedCount = getReferredAndConfirmedCount(user, behaviorConfig);
            newOne.updateMatchesData(referredAndConfirmedCount);

            existing = userReferralsSnapshotRepository.saveAndFlush(newOne);
            logger.info("New user referrals snapshot {} created for user id {}", existing, user.getId());
        }

        return existing;
    }

    @Transactional(readOnly = true)
    public int getReferredAndConfirmedCount(User user, BehaviorConfig behaviorConfig) {
        return referralRepository.getCountByCommunityIdUserIdAndStates(behaviorConfig.getCommunityId(), user.getId(), ReferralState.ACTIVATED);
    }

    @Transactional(readOnly = true)
    public BehaviorConfig getBehaviorConfig(Community community) {
        CommunityConfig communityConfig = communityConfigRepository.findByCommunity(community);
        return communityConfig.getBehaviorConfig();
    }

    public void setActivationDatePropertyName(String activationDatePropertyName) {
        this.activationDatePropertyName = activationDatePropertyName;
    }
}
