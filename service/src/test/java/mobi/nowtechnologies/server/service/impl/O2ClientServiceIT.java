package mobi.nowtechnologies.server.service.impl;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.service.O2ClientService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class O2ClientServiceIT {

	@Resource(name = "service.O2ClientService")
	private O2ClientService o2ClientService;
	
	@Test
	public void sendFreeSms_Successful() throws Exception {
		// Preparations for test
		
			
		// Invocation of test method
		boolean result = o2ClientService.sendFreeSms("","");
		
		// Asserts
		
	}
}