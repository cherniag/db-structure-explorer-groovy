package mobi.nowtechnologies.o2.stub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mobi.nowtechnologies.o2.O2Config;
import mobi.nowtechnologies.o2.UtilsO2;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class O2StubTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(O2StubTest.class);

	private static O2ProviderServiceImpl o2ClientService;
	private static O2ServiceImpl o2Service;

	@BeforeClass
	public static void beforeClass() throws Exception {
		o2ClientService = UtilsO2.createO2ClientService(O2Config.LOCAL_STUB);

		o2Service = UtilsO2.createO2ServiceImpl(O2Config.LOCAL_STUB);
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

		PhoneNumberValidationData validationData = o2ClientService.validatePhoneNumber(phone);
		LOGGER.info("validated = " + validationData.getPhoneNumber());
		assertTrue(validationData.getPhoneNumber().startsWith("+44"));

		ProviderUserDetails details = o2ClientService.getUserDetails(validationData.getPhoneNumber(), validationData.getPhoneNumber(), null);
		LOGGER.info("details oper: " + details.operator + "  tariff: " + details.contract);
		assertEquals(details.operator, o2 ? "o2" : "non-o2");
		assertEquals(details.contract, (monthlyContract) ? "PAYM" : "PAYG");

		O2SubscriberData data = o2Service.getSubscriberData(validationData.getPhoneNumber());
		LOGGER.info("data " + data);
		assertEquals(data.isBusinessOrConsumerSegment(), segmentBusiness);
		assertEquals(data.isProviderO2(), o2);
		assertEquals(data.isTariff4G(), tariff4G);
		assertEquals(data.isContractPostPay(), monthlyContract);
	}

}