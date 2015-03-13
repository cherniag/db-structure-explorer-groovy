package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.CommunityDto;

import java.util.List;

public interface AdminUserService {

    public List<CommunityDto> getCommunitiesbyUser(String username);
}