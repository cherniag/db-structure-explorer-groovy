package mobi.nowtechnologies.server.service.payment.impl;

import java.math.BigDecimal;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.payment.SagePayPaymentService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.service.PostService.Response;

import org.junit.Assert;
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
public class PaymentSystemServiceIT {
	
	@Resource(name = "service.sagePayPaymentService")
	private SagePayPaymentService paymentService;
	
	@Resource(name = "service.EntityService")
	private EntityService entityService;
	
	@Test
	public void commitSagePayPayment_Successful() throws Exception {
		// Preparations for test
		PaymentSystemResponse response = new SagePayResponse(new Response(){
			@Override public String getMessage() { return "StatusDetail=0000 : The Authorisation was Successful.\nTxAuthNo=12123123\nAVSCV2=SECURITY CODE MATCH ONLY\n3DSecureStatus=NOTCHECKED\nVPSTxId=123123123\nStatus=OK\nAddressResult=NOTMATCHED\nPostCodeResult=MATCHED\nCV2Result=MATCHED\nSecurityKey=123234234\nVPSProtocol=2.23"; };
			@Override public int getStatusCode() { return HttpServletResponse.SC_OK; }
		});
		
		User user = new User();
		user.setUserName(UUID.randomUUID().toString());
		SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
			currentPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
			currentPaymentDetails.setReleased(true);
			entityService.saveEntity(currentPaymentDetails);
		user.addPaymentDetails(currentPaymentDetails);
		entityService.saveEntity(user);
		
		PendingPayment pendingPayment = new PendingPayment();
			pendingPayment.setAmount(new BigDecimal(10));
			pendingPayment.setCurrencyISO("GBP");
			pendingPayment.setInternalTxId(UUID.randomUUID().toString());
			pendingPayment.setPaymentSystem(PaymentDetails.SAGEPAY_CREDITCARD_TYPE);
			pendingPayment.setSubweeks(2);
			pendingPayment.setTimestamp(System.currentTimeMillis());
			pendingPayment.setUser(user);
			entityService.saveEntity(pendingPayment);
			
		// Invocation of test method
		SubmittedPayment submittedPayment = paymentService.commitPayment(pendingPayment, response);
		
		// Asserts
		Assert.assertNotNull(submittedPayment);
		Assert.assertEquals(pendingPayment.getAmount(), submittedPayment.getAmount());
		Assert.assertEquals(null, submittedPayment.getDescriptionError());
		Assert.assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
	}
}