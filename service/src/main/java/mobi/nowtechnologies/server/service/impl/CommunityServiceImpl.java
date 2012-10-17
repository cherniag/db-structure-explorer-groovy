package mobi.nowtechnologies.server.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.CommunityService;

public class CommunityServiceImpl implements CommunityService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommunityServiceImpl.class);
	
	private CommunityRepository communityRepository;
	
	@Override
	public Community getCommunityByUrl(String communityUrl) {
		return CommunityDao.getMapAsUrls().get(communityUrl.toUpperCase());
	}

	@Override
	public Community getCommunityByName(String communityName) {
		LOGGER.debug("input parameters communityName: [{}]", communityName);
		Community community = CommunityDao.getCommunity(communityName);
		LOGGER.debug("Output parameter community=[{}]", community);
		return community;
	}

	@Override
	public List<Community> list() {
		return communityRepository.findAll();
	}

	public void setCommunityRepository(CommunityRepository communityRepository) {
		this.communityRepository = communityRepository;
	}
}