package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;

import java.util.List;

public interface CommunityService {

    Community getCommunityByUrl(String communityUrl);

    Community getCommunityByName(String communityName);

    List<Community> list();

    List<Community> getLiveCommunities();
}