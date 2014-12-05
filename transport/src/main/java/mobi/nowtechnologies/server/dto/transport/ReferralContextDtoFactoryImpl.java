package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ReferralService;

import javax.annotation.Resource;

/**
 * Created by zam on 12/2/2014.
 */
public class ReferralContextDtoFactoryImpl implements ReferralContextDtoFactory {

    private ReferralService referralService;

    @Resource
    public void setReferralService(ReferralService referralService) {
        this.referralService = referralService;
    }

    @Override
    public ReferralContextDto getReferralContextDto(User user) {
        ReferralContextDto dto = new ReferralContextDto();
        dto.setRequired(referralService.getRequiredReferralsCount(user.getCommunityRewriteUrl()));
        dto.setActivated(referralService.getActivatedReferralsCount(user));

        return dto;
    }
}
