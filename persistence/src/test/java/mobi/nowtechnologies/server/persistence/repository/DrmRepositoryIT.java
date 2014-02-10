package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class DrmRepositoryIT extends AbstractRepositoryIT{
	
	@Resource(name = "drmRepository")
	private DrmRepository drnRepository;
	
	@Test
	public void testCount() {
		long count = drnRepository.count();
		
		assertEquals(1, count);
	}
	
	@Test
	public void testFindOne() {
		Drm drm = drnRepository.findOne(1);
		
		assertNotNull(drm);
	}	
	
	@Test
	public void testFindByUserAndMedia() {
		Drm drm = drnRepository.findByUserAndMedia(1, 49);
		
		assertNotNull(drm);
	}	
}