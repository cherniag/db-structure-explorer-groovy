package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.ListDataResult;
import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.impl.PendingPaymentServiceImpl;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Ignore
public class PendingPaymentServiceTest {

	private PendingPaymentService service;
	private PaymentDao paymentDao;
	private UserService userService;
    private int maxCount = 35;

    @Before
	public void startup() {
		PendingPaymentServiceImpl serviceImpl = new PendingPaymentServiceImpl();
        serviceImpl.setMaxCount(35);
		paymentDao = Mockito.mock(PaymentDao.class);
		Mockito.when(paymentDao.savePendingPayment(Mockito.any(PendingPayment.class))).thenAnswer(new Answer<PendingPayment>() {
			@Override public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
				return (PendingPayment) invocation.getArguments()[0];
			}
		});
		userService = Mockito.mock(UserService.class);
			serviceImpl.setPaymentDao(paymentDao);
			serviceImpl.setUserService(userService);
		service = serviceImpl;
	}
	
	@Test
	public void createPendingPaymentsForRetries_Successful() {
		List<User> users = Arrays.asList(createUser(), createUser(), createUser());
        ListDataResult<User> dataResult = new ListDataResult<User>(users);
		Mockito.when(userService.getUsersForRetryPayment(maxCount)).thenReturn(dataResult);
		List<PendingPayment> paymentsForRetries = service.createRetryPayments();
		
		Assert.assertNotNull(paymentsForRetries);
		Assert.assertEquals(3, paymentsForRetries.size());
		for(PendingPayment payment : paymentsForRetries) {
			Assert.assertEquals(PaymentDetailsStatus.AWAITING, payment.getUser().getCurrentPaymentDetails().getLastPaymentStatus());
			Assert.assertEquals(1, payment.getUser().getCurrentPaymentDetails().getMadeRetries());
			Assert.assertTrue(payment.getInternalTxId().startsWith(PaymentDetailsType.RETRY.toString()));
		}
	}
	
	private User createUser() {
		User user = new User();
			user.setUserName(UUID.randomUUID().toString());
			PaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
				currentPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
				currentPaymentDetails.setRetriesOnError(2);
				PaymentPolicy paymentPolicy = new PaymentPolicy();
					paymentPolicy.setCurrencyISO("GBP");
					paymentPolicy.setSubcost(BigDecimal.TEN);
					paymentPolicy.setSubweeks((byte)10);
				currentPaymentDetails.setPaymentPolicy(paymentPolicy);
			user.addPaymentDetails(currentPaymentDetails);
		return user;
	}
}