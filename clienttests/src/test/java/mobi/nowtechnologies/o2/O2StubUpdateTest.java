package mobi.nowtechnologies.o2;

import static org.junit.Assert.assertEquals;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class O2StubUpdateTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(O2StubUpdateTest.class);

	private static final String SERVER_URL = "http://localhost:8998";
	private static final String O2_SERVER_FULL_URL = SERVER_URL + "/services/";

	private static O2ServiceImpl o2Service;

	@BeforeClass
	public static void beforeClass() throws Exception {

		o2Service = UtilsO2.createO2ServiceImpl(O2_SERVER_FULL_URL);
	}

	@Test
	public void testClient() throws Exception {

		String phone = "09999293073";

		O2SubscriberData data = new O2SubscriberData();

		new UpdatePhoneClient().updatePhone(phone, data);
		checkPhone(phone, data);

		//another check
		data.setProviderO2(true);
		data.setContractPostPayOrPrePay(true);
		data.setBusinessOrConsumerSegment(true);
		data.setTariff4G(false);

		new UpdatePhoneClient().updatePhone(phone, data);
		checkPhone(phone, data);

		LOGGER.info("completed ");
	}

	private void checkPhone(String phone, O2SubscriberData data) {
		LOGGER.info("start " + phone);
		O2SubscriberData serverData = o2Service.getSubscriberData(phone);
		LOGGER.info("data  " + data);
		LOGGER.info("sdata " + serverData);
		assertEquals("" + data, "" + serverData);
	}

}
