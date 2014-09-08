package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CommunityDaoTest extends AbstractRepositoryIT {

	@Test
	public void testGetCOMMUNITY_MAP_NAME_AS_KEY()
		throws Exception {

		Map<String, Community> result = CommunityDao.getMapAsNames();
		assertNotNull(result);
	}

	@Test
	public void testGetCOMMUNITY_MAP_REWRITE_URL_PARAMETER_AS_KEY()
		throws Exception {

		Map<String, Community> result = CommunityDao.getMapAsUrls();
		assertNotNull(result);
	}

}