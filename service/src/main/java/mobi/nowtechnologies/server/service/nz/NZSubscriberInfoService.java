package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoService {
    boolean belongs(String msisdn) throws SubscriberServiceException.ServiceNotAvailable, SubscriberServiceException.MSISDNNotFound;
}
