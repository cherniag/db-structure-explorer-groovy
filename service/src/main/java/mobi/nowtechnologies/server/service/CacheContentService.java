package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.service.exception.NoNewContentException;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class CacheContentService {

    public void checkCacheContent(Long dateFromClient, Long dateOfUpdate) {
        if (dateFromClient >= dateOfUpdate) {
            throw new NoNewContentException(dateOfUpdate, dateFromClient);
        }
    }
}
