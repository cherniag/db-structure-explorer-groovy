package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ReferralService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;

import java.util.Date;

import static mobi.nowtechnologies.server.shared.util.DateUtils.newDate;

// Created by zam on 12/2/2014.
public class ReferralContextDtoFactoryImpl implements ReferralContextDtoFactory {

    private static final Date DEFAULT_REFERRAL_LOGIC_ACTIVATION_DATE = newDate(28, 1, 2015);
    private ReferralService referralService;

    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    @Resource
    public void setReferralService(ReferralService referralService) {
        this.referralService = referralService;
    }

    @Resource
    public void setCommunityResourceBundleMessageSource(CommunityResourceBundleMessageSource communityResourceBundleMessageSource) {
        this.communityResourceBundleMessageSource = communityResourceBundleMessageSource;
    }

    @Override
    public ReferralContextDto getReferralContextDto(User user) {
        ReferralContextDto dto = new ReferralContextDto();

        Date date = communityResourceBundleMessageSource.readDate(user.getCommunityRewriteUrl(), "referral.logic.activation.date", DEFAULT_REFERRAL_LOGIC_ACTIVATION_DATE);

        if (new Date(user.getFirstDeviceLoginMillis()).after(date)) {
        dto.setRequired(referralService.getRequiredReferralsCount(user.getCommunityRewriteUrl()));
        dto.setActivated(referralService.getActivatedReferralsCount(user));
        } else {
            dto.setRequired(-1);
            dto.setActivated(-1);
        }

        return dto;
    }
}
