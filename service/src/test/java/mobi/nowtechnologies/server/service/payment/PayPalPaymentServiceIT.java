package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.impl.PayPalPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Ignore
public class PayPalPaymentServiceIT {
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;
	
	private PayPalPaymentService paypalPaymentService;
	
	@Resource(name = "service.PaymentDetailsService")
	private PaymentDetailsService paymentDetailsService;
	
	PayPalHttpService httpService = Mockito.mock(PayPalHttpService.class);

    BasicResponse successfulResponse = new BasicResponse() {
		@Override
		public int getStatusCode() {
			return HttpServletResponse.SC_OK;
		}
		@Override public String getMessage() {
			return "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD=2271164";
		}
	};
	
	@Before
	public void before() {
		
		PayPalPaymentServiceImpl service = new PayPalPaymentServiceImpl();
			service.setExpireMillis(3L);
			service.setRedirectURL("redirectUrl");
			service.setRetriesOnError(3);
			service.setHttpService(httpService);
			EntityService entityService = new EntityService();
				entityService.setEntityDao(entityDao);
			service.setEntityService(entityService);
			service.setPaymentDetailsService(paymentDetailsService);
		paypalPaymentService = service;
	}
	
	@Test
	public void commitPayment_Successful() {
		when(httpService.makeBillingAgreementRequest(anyString())).thenReturn(new PayPalResponse(successfulResponse));
		User user = new User();
			user.setUserName("good@user.com");
			entityDao.saveEntity(user);
			
		Community community = CommunityDao.getCommunity("CN Commercial Beta");
			PaymentPolicy paymentPolicy = new PaymentPolicy();
				paymentPolicy.setAvailableInStore(true);
				paymentPolicy.setCommunity(community);
				paymentPolicy.setCurrencyISO("GBP");
				paymentPolicy.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
				paymentPolicy.setSubcost(BigDecimal.TEN);
				paymentPolicy.setSubweeks((byte)0);
			entityDao.saveEntity(paymentPolicy);
			
		PayPalPaymentDetails paymentDetails = paypalPaymentService.commitPaymentDetails("token", user, paymentPolicy, true);
		
		assertEquals(user.getId(), paymentDetails.getOwner().getId());
		assertEquals(1, user.getPaymentDetailsList().size());
	}
}