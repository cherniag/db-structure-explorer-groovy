package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 *
 */
@Deprecated
public class CommunityDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommunityDao.class);
	private static Map<String,Community> COMMUNITY_MAP_NAME_AS_KEY; 
	private static Map<String,Community> COMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY;
	
	private static void setEntityDao(EntityDao entityDao) {
		List<Community> communityList = entityDao.findAll(Community.class);
		Map<String,Community> communityMapName = new HashMap<String, Community>();
		Map<String,Community> communityMapUrl = new HashMap<String, Community>();
		for (Community community : communityList) {
			communityMapName.put(community.getName(), community);
			communityMapUrl.put(
					community.getRewriteUrlParameter().toUpperCase(), community);
		}
		COMMUNITY_MAP_NAME_AS_KEY = Collections.unmodifiableMap(communityMapName);
		COMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY = 
			Collections.unmodifiableMap(communityMapUrl);
	}
	
	public static Map<String,Community> getMapAsNames() {
		return COMMUNITY_MAP_NAME_AS_KEY;
	}
	
	public static Map<String, Community> getMapAsUrls() {
		return COMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY;
	}

	public static Community getCommunity(String communityName) {
		if (communityName == null)
			throw new PersistenceException("The parameter communityName is null");
		LOGGER.debug("input parameters communityName: [{}]", communityName);
		Community community = COMMUNITY_MAP_NAME_AS_KEY.get(communityName);
		LOGGER.debug("Output parameter community=[{}]", community);
		return community;
	}
	
}
