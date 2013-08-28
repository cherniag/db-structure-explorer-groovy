package mobi.nowtechnologies.o2;

import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.service.o2.impl.O2ClientServiceImpl;
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

		O2ClientServiceImpl o2ClientService = UtilsO2.createO2ClientService(o2ProxyUrl, o2ServerUrl);
		String phone = "07731293075";
		
		String validatedPhone = o2ClientService.validatePhoneNumber(phone);
		LOGGER.info("validated = " + validatedPhone);
		
		O2UserDetails details = o2ClientService.getUserDetails("000000", validatedPhone);
		LOGGER.info("details oper: " + details.getOperator() + "  tariff: " + details.getTariff());

		O2ServiceImpl o2Service = UtilsO2.createO2ServiceImpl(o2ServerUrl);
		
		
		O2SubscriberData data = o2Service.getSubscriberData(validatedPhone);
		LOGGER.info("data "+data);
		
		LOGGER.info("completed ");
	}

}
