package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfig;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.BehaviorConfigRepository;
import mobi.nowtechnologies.server.persistence.repository.behavior.CommunityConfigRepository;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class BehaviorInfoService {

    @Resource
    UserReferralsSnapshotRepository userReferralsSnapshotRepository;
    @Resource
    CommunityConfigRepository communityConfigRepository;
    @Resource
    ReferralRepository referralRepository;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
    @Resource
    BehaviorConfigRepository behaviorConfigRepository;
    private Logger logger = LoggerFactory.getLogger(BehaviorInfoService.class);
    private String activationDatePropertyName;

    public boolean isFirstDeviceLoginBeforeReferralsActivation(User user) {
        String communityName = user.getCommunityRewriteUrl();
        Date featureActivation = communityResourceBundleMessageSource.readDate(communityName, activationDatePropertyName);
        return new Date(user.getFirstDeviceLoginMillis()).before(featureActivation);
    }

    @Transactional(readOnly = true)
    public UserReferralsSnapshot getUserReferralsSnapshot(User user, BehaviorConfig behaviorConfig) {
        UserReferralsSnapshot existing = userReferralsSnapshotRepository.findOne(user.getId());
        if (existing == null) {
            UserReferralsSnapshot newOne = new UserReferralsSnapshot(user.getId(), behaviorConfig.getRequiredReferrals(), behaviorConfig.getReferralsDuration());
            int referredAndConfirmedCount = getReferredAndConfirmedCount(user, behaviorConfig);
            newOne.updateMatchesData(referredAndConfirmedCount, new Date());

            existing = userReferralsSnapshotRepository.saveAndFlush(newOne);
            logger.info("New user referrals snapshot {} created for user id {}", existing, user.getId());
        }

        return existing;
    }

    @Transactional(readOnly = true)
    public int getReferredAndConfirmedCount(User user, BehaviorConfig behaviorConfig) {
        return referralRepository.countByCommunityIdUserIdAndStates(behaviorConfig.getCommunityId(), user.getId(), ReferralState.ACTIVATED);
    }

    @Transactional(readOnly = true)
    public BehaviorConfig getBehaviorConfig(boolean supportsFreemium, Community community) {
        return supportsFreemium ?
               communityConfigRepository.findByCommunity(community).getBehaviorConfig() :
               behaviorConfigRepository.findByCommunityIdAndBehaviorConfigType(community.getId(), BehaviorConfigType.DEFAULT);
    }

    public void setActivationDatePropertyName(String activationDatePropertyName) {
        this.activationDatePropertyName = activationDatePropertyName;
    }
}
