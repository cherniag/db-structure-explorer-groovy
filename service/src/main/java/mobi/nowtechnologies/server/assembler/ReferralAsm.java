package mobi.nowtechnologies.server.assembler;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.dto.ReferralDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/21/2014
 */
public class ReferralAsm {

    public List<Referral> fromDtos(List<ReferralDto> dtos, User user){
        List<Referral> referrals = Lists.newArrayList();
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
