package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.Drm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * The class <code>MessageRepositoryTest</code> contains tests for the class <code>{@link DrmRepository}</code>.
 *
 * @generatedBy CodePro at 16.05.12 11:10
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public class DrmRepositoryIT {
	
	@Resource(name = "drmRepository")
	private DrmRepository drnRepository;
	
	@Test
	public void testCount() {
		long count = drnRepository.count();
		
		assertEquals(count, 5);
	}
	
	@Test
	public void testFindOne() {
		Drm drm = drnRepository.findOne(1);
		
		Assert.assertNotNull(drm);
	}	
	
	@Test
	public void testFindByUserAndMedia() {
		Drm drm = drnRepository.findByUserAndMedia(1, 49);
		
		Assert.assertNotNull(drm);
	}	
}