package mobi.nowtechnologies.server.transport.referrals;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Gennadii Cherniaiev Date: 11/21/2014
 */
public class ReferralAsm {

    public List<Referral> fromDtos(List<ReferralDto> dtos, User user) {
        List<Referral> referrals = new ArrayList<Referral>();
        for (ReferralDto incomingDto : dtos) {
            referrals.add(fromDto(incomingDto, user));
        }
        return referrals;
    }

    private Referral fromDto(ReferralDto dto, User user) {
        Referral referral = new Referral();
        referral.setProviderType(dto.getSource());
        referral.setContact(dto.getId());
        referral.setUserId(user.getId());
        referral.setCommunityId(user.getCommunityId());
        return referral;
    }

}
