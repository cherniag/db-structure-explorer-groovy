package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.service.exception.ServiceNotAvailableException;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoService {

    boolean belongs(String msisdn) throws ServiceNotAvailableException;

    NZSubscriberInfo confirm(int userId, String msisdn);

}
