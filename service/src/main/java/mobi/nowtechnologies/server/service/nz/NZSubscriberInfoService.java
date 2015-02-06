package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoService {

    NZSubscriberInfo getSubscriberInfo(int userId, String msisdn);

}
