package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * The class <code>MediaLogDaoTest</code> contains tests for the class <code>{@link MediaLogDao}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class MediaLogDaoTest {
    @Autowired
	private MediaLogDao mediaLogDao;

    @Autowired
    private EntityDao entityDao;

    /**
	 * Run the List<MediaLogShallow> findPurchasedTracksByUserId(int) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testFindPurchasedTracksByUserId()
		throws Exception {
		int userId = 91;

		List<MediaLog> result = mediaLogDao.findPurchasedTracksByUserId(userId);

		assertNotNull(result);
	}

	/**
	 * Run the void logMediaEvent(int,int,byte) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testLogMediaEvent()
		throws Exception {
		int userId = 1;
		Media media = new Media();
		byte mediaLogType = (byte) 1;

        entityDao.saveEntity(media);
        mediaLogDao.logMediaEvent(userId, media, mediaLogType);
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testLogMediaEventWrongMediaId()
		throws Exception {
		
		int userId = 1;
		Media media = null;
		byte mediaLogType = (byte) 1;

		mediaLogDao.logMediaEvent(userId, media, mediaLogType);

	}

	/**
	 * Run the void logMediaEvent(int,int,byte) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testLogMediaEventWrongMediaLogType()
		throws Exception {
		int userId = 1;
		Media media = new Media();
		media.setI(1);
		byte mediaLogType = (byte) -1;

		mediaLogDao.logMediaEvent(userId, media, mediaLogType);

	}
}