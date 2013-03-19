package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.payment.response.O2Response;

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
	
	@Test
	public void testMakePremiumSMSRequest_Successful() throws Exception {
		// Preparations for test	
		String subMerchantId = "O2 Tracks";
		String contentDescription = "O2 tracks subscription";
		String contentType = "other";
		String contentCategory = "";
		String message = "hello, you made subscription";
		String o2PhoneNumber = "447702059016";
		BigDecimal subCost = new BigDecimal(100);
		String internalTxId = "internalTxId";
		int userId = 1;
		boolean smsNotify = true;
		
		// Invocation of test method
		O2Response result = o2ClientService.makePremiumSMSRequest(userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription , subMerchantId, smsNotify  );
		
		// Asserts
		assertNotNull(result);
		assertEquals(true, result.isSuccessful());
	}
}