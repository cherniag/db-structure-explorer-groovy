package mobi.nowtechnologies.server.service.payment.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@Ignore
public class PendingPaymentServiceImplTest {
	
	private PendingPaymentService service;
	private PaymentDao paymentDao;
	private UserService userService;
	
	@Before
	public void startup() {
		PendingPaymentServiceImpl serviceImpl = new PendingPaymentServiceImpl();
			paymentDao = Mockito.mock(PaymentDao.class);
			Mockito.when(paymentDao.savePendingPayment(Mockito.any(PendingPayment.class))).thenAnswer(new Answer<PendingPayment>() {
				@Override
				public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
					return (PendingPayment) invocation.getArguments()[0];
				}
			});
			serviceImpl.setPaymentDao(paymentDao);
			
			userService = Mockito.mock(UserService.class);
				Mockito.when(userService.updateUser(Mockito.any(User.class))).thenAnswer(new Answer<User>() {
					@Override public User answer(InvocationOnMock invocation) throws Throwable {
						return (User) invocation.getArguments()[0];
					}
				});
			serviceImpl.setUserService(userService);
		service = serviceImpl;
	}
	
	/**
	 * Test whether method creates the same size of the pending payments as users were found
	 * and also sets for each user a lastPaymentSatatus
	 */
	@Test
	public void createSagePayPendingPayments_Successful() {
		List<User> users = Arrays.asList(
					generateUserWithSagePayPaymentDetails((byte)0, PaymentDetailsStatus.NONE)
					,generateUserWithSagePayPaymentDetails((byte)0, PaymentDetailsStatus.NONE)
					,generateUserWithSagePayPaymentDetails((byte)0, PaymentDetailsStatus.NONE)
					,generateUserWithSagePayPaymentDetails((byte)0, PaymentDetailsStatus.SUCCESSFUL)
					,generateUserWithSagePayPaymentDetails((byte)0, PaymentDetailsStatus.SUCCESSFUL)
					,generateUserWithSagePayPaymentDetails((byte)0, PaymentDetailsStatus.SUCCESSFUL)
		);
		
		Mockito.when(paymentDao.getUsersForPendingPayment()).thenReturn(users);
		
		List<PendingPayment> createPendingPayments = service.createPendingPayments();
		
		Assert.assertNotNull(createPendingPayments);
		Assert.assertEquals(users.size(), createPendingPayments.size());
		for(User user : users) {
			Assert.assertEquals(PaymentDetailsStatus.AWAITING, user.getCurrentPaymentDetails().getLastPaymentStatus());
		}
	}
	
	private User generateUserWithSagePayPaymentDetails(byte subBalance, PaymentDetailsStatus status) {
		User user = new User();
			String randomString = UUID.randomUUID().toString();
			user.setUserName(randomString);
			PaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
				PaymentPolicy paymentPolicy = new PaymentPolicy();
					paymentPolicy.setCurrencyISO("GBP");
					paymentPolicy.setPaymentType(PaymentDetails.SAGEPAY_CREDITCARD_TYPE);
					paymentPolicy.setSubcost(BigDecimal.TEN);
					paymentPolicy.setSubweeks((byte)10);
				currentPaymentDetails.setPaymentPolicy(paymentPolicy);
				currentPaymentDetails.setLastPaymentStatus(status);
			user.addPaymentDetails(currentPaymentDetails);
			user.setSubBalance(subBalance);
		return user;
	}
}