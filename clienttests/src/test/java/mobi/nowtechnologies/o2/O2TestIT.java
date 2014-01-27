package mobi.nowtechnologies.o2;

import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to test responses sent by O2 - we can test QA and prod environments - useful to see the responses returned by O2
 * 
 * TAKE CARE WHEN TESTING ON PROD BECAUSE SMSs CAN BE SENT
 *
 */
public class O2TestIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(O2TestIT.class);

	@SuppressWarnings("unused")
	@Test
	public void testClient() throws Exception {

		LOGGER.info("start");
		
		O2Config config = O2Config.QA;

		O2ProviderServiceImpl o2ClientService = UtilsO2.createO2ClientService(config);
		String phone = "+447731293075";
		
		
		// DO NOT CALL THIS ON PROD FOR A REALNUMBER - THE USER WILL RECEIVE AN SMS
//		PhoneNumberValidationData phoneNumberValidationData = o2ClientService.validatePhoneNumber(phone);
//		LOGGER.info("validated = " + phoneNumberValidationData.getPhoneNumber());
		
		// USE THIS ONLY ON QA/UAT
//		ProviderUserDetails details = o2ClientService.getUserDetails("000000, 00000000-fake-musi-cqub-edauthcodezz", phoneNumberValidationData.getPhoneNumber(), null);
//		LOGGER.info("details oper: " + details.operator + "  tariff: " + details.contract);

		O2ServiceImpl o2Service = UtilsO2.createO2ServiceImpl(config);
		
		O2SubscriberData data = o2Service.getSubscriberData(phone/*phoneNumberValidationData.getPhoneNumber()*/);
		LOGGER.info("data "+data);
		
		LOGGER.info("completed ");
	}

}