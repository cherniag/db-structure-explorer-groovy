package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.exception.NoNewContentException;
import mobi.nowtechnologies.server.shared.util.DateUtils;
import org.junit.Test;

import java.util.Date;

/**
 * Created by Oleg Artomov on 10/17/2014.
 */
public class CacheContentServiceTest {

    private CacheContentService cacheContentService = new CacheContentService();

    @Test(expected = NoNewContentException.class)
    public void testForTimeInOneSecond() {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        long currentTimeWithoutMilliseconds = DateUtils.getTimeWithoutMilliseconds(currentTime);
        long currentTimeWithMilliseconds = org.apache.commons.lang.time.DateUtils.setMilliseconds(currentDate, 100).getTime();
        cacheContentService.checkCacheContent(currentTimeWithoutMilliseconds, currentTimeWithMilliseconds);
    }

    @Test(expected = NoNewContentException.class)
    public void testForEqualsTime() {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        long currentTimeWithoutMilliseconds = DateUtils.getTimeWithoutMilliseconds(currentTime);
        long currentTimeWithMilliseconds = DateUtils.getTimeWithoutMilliseconds(currentTime);
        cacheContentService.checkCacheContent(currentTimeWithoutMilliseconds, currentTimeWithMilliseconds);
    }


    @Test
    public void testWhenClientTimeLessDateOfUpdateAndNoException() {
        Date currentDate = new Date();
        Date clientDate = org.apache.commons.lang.time.DateUtils.addDays(currentDate, -1);
        cacheContentService.checkCacheContent(clientDate.getTime(), currentDate.getTime());
    }

}
