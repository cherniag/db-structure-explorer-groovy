package mobi.nowtechnologies.server.dto.asm;

import mobi.nowtechnologies.server.dto.CommunityDto;
import mobi.nowtechnologies.server.persistence.domain.Community;

import java.util.ArrayList;
import java.util.List;

// @author Titov Mykhaylo (titov) on 20.11.2014.
public class CommunityDtoAsm {

    public List<CommunityDto> toCommunityDtos(List<Community> communities) {
        List<CommunityDto> communityDtos = new ArrayList<CommunityDto>(communities.size());
        for (Community community : communities) {
            communityDtos.add(toCommunityDto(community));
        }
        return communityDtos;
    }

    public CommunityDto toCommunityDto(Community community) {
        CommunityDto communityDto = new CommunityDto();
        communityDto.setUrl(community.getRewriteUrlParameter());
        communityDto.setActive(community.isLive());
        return communityDto;
    }
}
