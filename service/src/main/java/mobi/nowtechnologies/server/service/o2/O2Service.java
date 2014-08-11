package mobi.nowtechnologies.server.service.o2;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

public interface O2Service {
	O2SubscriberData getSubscriberData(String phoneNumber);

    String validatePhoneNumber(String url, String phoneNumber);

    ProviderUserDetails getProviderUserDetails(String serverO2Url, String token);
}
