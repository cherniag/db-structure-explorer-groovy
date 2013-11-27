package mobi.nowtechnologies.server.service.impl.o2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/service-test-ws.xml" })
@Ignore
public class O2ServiceIT {

	protected final Logger LOGGER = LoggerFactory.getLogger(O2ServiceIT.class);

	@Autowired
	private O2Service o2service;

	@Test
	public void testService() throws Exception {
		LOGGER.info("start");

		KeystoreUtils.initKeystore();

		O2SubscriberData res = o2service
				.getSubscriberData(PhoneNumbers.O2_4G_CONTRACT);
		assertTrue(res.isProviderO2());
		assertFalse(res.isBusinessOrConsumerSegment());
		assertTrue(res.isContractPostPayOrPrePay());
		assertTrue(res.isDirectOrIndirect4GChannel());
		assertTrue(res.isTariff4G());
		
		res=o2service.getSubscriberData(PhoneNumbers.O2_4G_BOLTON);
		assertTrue(res.isProviderO2());
		assertFalse(res.isBusinessOrConsumerSegment());
		assertTrue(res.isContractPostPayOrPrePay());
		assertTrue(res.isDirectOrIndirect4GChannel());
		assertTrue(res.isTariff4G());
		
		res = o2service.getSubscriberData(PhoneNumbers.O2_3G_CONTRACT);
		assertTrue(res.isProviderO2());
		assertFalse(res.isBusinessOrConsumerSegment());
		assertTrue(res.isContractPostPayOrPrePay());
		assertTrue(res.isDirectOrIndirect4GChannel());
		assertFalse(res.isTariff4G());

		res = o2service.getSubscriberData(PhoneNumbers.NON_O2);
		assertFalse(res.isProviderO2());
		assertFalse(res.isBusinessOrConsumerSegment());
		assertFalse(res.isContractPostPayOrPrePay());
		assertTrue(res.isDirectOrIndirect4GChannel());
		assertFalse(res.isTariff4G());


		res = o2service.getSubscriberData(PhoneNumbers.NON_O2_ANOTHER);
		assertFalse(res.isProviderO2());
		assertFalse(res.isBusinessOrConsumerSegment());
		assertFalse(res.isContractPostPayOrPrePay());
		assertTrue(res.isDirectOrIndirect4GChannel());
		assertFalse(res.isTariff4G());
		
		LOGGER.info("competed");
	}

}
