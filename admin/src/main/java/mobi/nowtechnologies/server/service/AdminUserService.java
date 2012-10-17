package mobi.nowtechnologies.server.service;

import java.util.List;

import mobi.nowtechnologies.server.dto.CommunityDto;

public interface AdminUserService {
	
	public List<CommunityDto> getCommunitiesbyUser(String username);
}