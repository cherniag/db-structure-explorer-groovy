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
	public void testMakePremiumSMSRequest_Failure() throws Exception {
		// Preparations for test	
		String subMerchantId = "O2 Tracks";
		String contentDescription = "Description of content";
		String contentType = "mqbed_tracks_3107054";
		String contentCategory = "other";
		String message = "";
		String o2PhoneNumber = "447511182660";
		BigDecimal subCost = new BigDecimal(1);
		String internalTxId = "";
		int userId = 1;
		boolean smsNotify = false;
		
		// Invocation of test method
		O2Response result = o2ClientService.makePremiumSMSRequest(userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription , subMerchantId, smsNotify  );
		
		// Asserts
		assertNotNull(result);
		assertEquals(false, result.isSuccessful());
	}
	
	@Test
	public void testMakePremiumSMSRequest_Successful() throws Exception {
		// Preparations for test	
		String subMerchantId = "O2 Tracks";
		String contentDescription = "Description of content";
		String contentType = "mqbed_tracks_3107054";
		String contentCategory = "other";
		String message = "";
		String o2PhoneNumber = "447511182663";
		BigDecimal subCost = new BigDecimal(1);
		String internalTxId = "";
		int userId = 1;
		boolean smsNotify = false;
		
		// Invocation of test method
		O2Response result = o2ClientService.makePremiumSMSRequest(userId, internalTxId, subCost, o2PhoneNumber, message, contentCategory, contentType, contentDescription , subMerchantId, smsNotify  );
		
		// Asserts
		assertNotNull(result);
		assertEquals(true, result.isSuccessful());
	}
}