package mobi.nowtechnologies.server.service;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;

public interface CommunityService {
	Community getCommunityByUrl(String communityUrl);

	Community getCommunityByName(String communityName);
	
	List<Community> list();
}