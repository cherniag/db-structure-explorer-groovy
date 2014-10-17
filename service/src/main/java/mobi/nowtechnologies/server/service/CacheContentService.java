package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.exception.NoNewContentException;

import static mobi.nowtechnologies.server.shared.util.DateUtils.getTimeWithoutMilliseconds;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class CacheContentService {

    public void checkCacheContent(Long dateFromClient, Long dateOfUpdate) {
        Long timeFromClientWithoutMilliseconds = getTimeWithoutMilliseconds(dateFromClient);
        Long timeOfUpdateWithoutMilliseconds = getTimeWithoutMilliseconds(dateOfUpdate);
        if (timeFromClientWithoutMilliseconds >= timeOfUpdateWithoutMilliseconds) {
            throw new NoNewContentException(dateOfUpdate, dateFromClient);
        }
    }
}
