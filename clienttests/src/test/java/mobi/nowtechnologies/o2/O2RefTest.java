package mobi.nowtechnologies.o2;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class O2RefTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(O2RefTest.class);

	@Test
	public void testClient() throws Exception {

		LOGGER.info("start");
		String o2ProxyUrl = "http://uat.mqapi.com";
		String o2ServerUrl = "https://sdpapi.ref.o2.co.uk/services/";

		O2ProviderServiceImpl o2ClientService = UtilsO2.createO2ClientService(o2ProxyUrl, o2ServerUrl);
		String phone = "07731293075";
		
		PhoneNumberValidationData phoneNumberValidationData = o2ClientService.validatePhoneNumber(phone);
		LOGGER.info("validated = " + phoneNumberValidationData.getPhoneNumber());
		
		ProviderUserDetails details = o2ClientService.getUserDetails("000000", phoneNumberValidationData.getPhoneNumber(), null);
		LOGGER.info("details oper: " + details.operator + "  tariff: " + details.contract);

		O2ServiceImpl o2Service = UtilsO2.createO2ServiceImpl(o2ServerUrl);
		
		O2SubscriberData data = o2Service.getSubscriberData(phoneNumberValidationData.getPhoneNumber());
		LOGGER.info("data "+data);
		
		LOGGER.info("completed ");
	}

}
