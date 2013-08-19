package mobi.nowtechnologies.o2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mobi.nowtechnologies.server.dto.O2UserDetails;
import mobi.nowtechnologies.server.service.o2.impl.O2ClientServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class O2StubTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(O2StubTest.class);
	private static final String SERVER_URL = "http://localhost:8998";
	private static final String O2_PROXY_URL = SERVER_URL;
	private static final String O2_SERVER_FULL_URL = SERVER_URL + "/services/";

	private static O2ClientServiceImpl o2ClientService;
	private static O2ServiceImpl o2Service;

	@BeforeClass
	public static void beforeClass() throws Exception {
		o2ClientService = UtilsO2.createO2ClientService(O2_PROXY_URL, O2_SERVER_FULL_URL);

		o2Service = UtilsO2.createO2ServiceImpl(O2_SERVER_FULL_URL);
	}

	@Test
	public void testClient() throws Exception {


		checkPhone("07731293079", false, false, false, false);
		checkPhone("07731293070", true, true, false, false);
		checkPhone("07731293071", true, false, false, false);
		checkPhone("07731293072", true, true, true, false);
		checkPhone("07731293073", true, true, false, true);
		
		LOGGER.info("completed ");

		
	}

	private void checkPhone(String phone, boolean o2, boolean monthlyContract, boolean segmentBusiness, boolean tariff4G) {
		LOGGER.info("start " + phone);

		String validatedPhone = o2ClientService.validatePhoneNumber(phone);
		LOGGER.info("validated = " + validatedPhone);
		assertTrue(validatedPhone.startsWith("+44"));

		O2UserDetails details = o2ClientService.getUserDetails(validatedPhone, validatedPhone);
		LOGGER.info("details oper: " + details.getOperator() + "  tariff: " + details.getTariff());
		assertEquals(details.getOperator(), o2 ? "o2" : "non-o2");
		assertEquals(details.getTariff(), (monthlyContract) ? "PAYM" : "PAYG");

		O2SubscriberData data = o2Service.getSubscriberData(validatedPhone);
		LOGGER.info("data " + data);
		assertEquals(data.isBusinessOrConsumerSegment(), segmentBusiness);
		assertEquals(data.isProviderO2(), o2);
		assertEquals(data.isTariff4G(), tariff4G);
		assertEquals(data.isContractPostPay(), monthlyContract);
	}

}
