package mobi.nowtechnologies.server.persistence.repository;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.Drm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class DrmRepositoryIT {
	
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