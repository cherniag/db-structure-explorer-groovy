package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.CommunityService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityServiceImpl implements CommunityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityServiceImpl.class);

    private CommunityRepository communityRepository;

    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public Community getCommunityByUrl(String communityUrl) {
        return communityRepository.findByRewriteUrlParameter(communityUrl);
    }

    @Override
    public Community getCommunityByName(String communityName) {
        LOGGER.debug("input parameters communityName: [{}]", communityName);
        Community community = communityRepository.findByRewriteUrlParameter(communityName);
        LOGGER.debug("Output parameter community=[{}]", community);
        return community;
    }

    @Override
    public List<Community> getLiveCommunities() {
        return communityRepository.findByLive(true);
    }

}