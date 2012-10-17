package mobi.nowtechnologies.server.service.payment;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.domain.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.impl.PayPalPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.shared.service.PostService.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dao-test.xml" })
@TransactionConfiguration(defaultRollback = true)
public class PayPalPaymentServiceIT {
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;
	
	private PayPalPaymentService paypalPaymentService;
	
	PayPalHttpService httpService = Mockito.mock(PayPalHttpService.class);
	
	Response successfulResponse = new Response() {
		@Override
		public int getStatusCode() {
			return HttpServletResponse.SC_OK;
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
		paypalPaymentService = service;
	}
	
	@Test
	public void commitPayment_Successful() {
		when(httpService.makeBillingAgreementRequest(anyString())).thenReturn(new PayPalResponse(successfulResponse));
		User user = new User();
			user.setUserName("good@user.com");
			entityDao.saveEntity(user);
		PaymentPolicy paymentPolicy = new PaymentPolicy();
			entityDao.saveEntity(paymentPolicy);
			
		PayPalPaymentDetails paymentDetails = paypalPaymentService.commitPaymentDetails("token", user, paymentPolicy, true);
		
		assertEquals(user.getId(), paymentDetails.getOwner().getId());
		assertEquals(1, user.getPaymentDetailsList().size());
	}
}