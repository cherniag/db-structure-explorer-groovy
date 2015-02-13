package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoService {

    boolean belongs(String msisdn);

    NZSubscriberInfo confirm(int userId, String msisdn);

}
