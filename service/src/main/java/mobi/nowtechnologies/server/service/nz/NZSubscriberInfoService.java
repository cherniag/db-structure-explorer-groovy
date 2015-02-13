package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;

/**
 * @author Anton Zemliankin
 */
public interface NZSubscriberInfoService {

    boolean checkVodafone(int userId, String msisdn);

    NZSubscriberInfo confirmSubscriber(int userId, String msisdn);

}
