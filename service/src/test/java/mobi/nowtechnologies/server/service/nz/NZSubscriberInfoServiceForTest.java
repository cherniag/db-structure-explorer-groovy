package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;

/**
 * @author Anton Zemliankin
 */
class NZSubscriberInfoServiceForTest extends NZSubscriberInfoService {

    @Override
    NZSubscriberInfo getSubscriberInfoByMsisdn(String msisdn) {
        return null;
    }

}
