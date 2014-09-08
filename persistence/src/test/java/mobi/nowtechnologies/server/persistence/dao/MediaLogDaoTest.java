package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaLog;
import mobi.nowtechnologies.server.persistence.repository.AbstractRepositoryIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @version $Revision: 1.0 $
 */
public class MediaLogDaoTest extends AbstractRepositoryIT {
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