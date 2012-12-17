package mobi.nowtechnologies.server.service.aop;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import antlr.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@PrepareForTest(Utils.class)
@SuppressWarnings("deprecation")
public class SMSNotificationIT {
	
	@Autowired
	private SMSNotification fixture;

	
	@Test
	public void testSendLimitedStatusSMS_Success()
		throws Exception {
	}
	
		
	@Test
	public void testSendUnsubscribePotentialSMS_Success()
		throws Exception {
	}

	@Before
	public void setUp()
		throws Exception {
	}
}