package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PendingPaymentServiceImplTest {

	private PendingPaymentService service;
	private PaymentDao paymentDao;
	private UserService userService;
	@Mock
	private PaymentPolicyService mockPaymentPolicyService;

	@Before
	public void startup() {
		Map<String, PaymentSystemService> paymentSystems = new HashMap<String, PaymentSystemService>();
		paymentSystems.put("sagePayCreditCard", new SagePayPaymentServiceImpl());
		paymentSystems.put("o2Psms", new O2PaymentServiceImpl());

		PendingPaymentServiceImpl serviceImpl = spy(new PendingPaymentServiceImpl());
		serviceImpl.setPaymentSystems(paymentSystems);
		serviceImpl.setPaymentPolicyService(mockPaymentPolicyService);

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
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				return (User) invocation.getArguments()[0];
			}
		});
		serviceImpl.setUserService(userService);
		
		Mockito.when(mockPaymentPolicyService.getPaymentPolicy(any(PaymentDetails.class))).thenAnswer(new Answer<PaymentPolicyDto>(){
			@Override
			public PaymentPolicyDto answer(InvocationOnMock invocation) throws Throwable {
				PaymentDetails paymentDetails = (PaymentDetails)invocation.getArguments()[0];
				PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
				PaymentPolicyDto paymentPolicyDto = new PaymentPolicyDto();
				paymentPolicyDto.setSubcost(paymentPolicy.getSubcost());
				paymentPolicyDto.setSubweeks((int)paymentPolicy.getSubweeks());
				paymentPolicyDto.setCurrencyISO(paymentPolicy.getCurrencyISO());
				
				return paymentPolicyDto;
			}
		});
		
		service = serviceImpl;
	}

	/**
	 * Test whether method creates the same size of the pending payments as users were found and also sets for each user a lastPaymentSatatus
	 */
	@Test
	public void createPendingPayments_WithInvalidPaymentPolicy_Successful() {
		List<User> users = Arrays.asList(
				generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.SUCCESSFUL)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.SUCCESSFUL)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.SUCCESSFUL)
				, generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, false)
				, generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, true)
				);

		Mockito.when(userService.getUsersForPendingPayment()).thenReturn(users);

		List<PendingPayment> createPendingPayments = service.createPendingPayments();

		Assert.assertNotNull(createPendingPayments);
		Assert.assertEquals(users.size() - 1, createPendingPayments.size());
		
		verify(userService, times(1)).unsubscribeUser(any(User.class), anyString());
	}

	@Test
	public void testCreatePendingPayments_Successful() {
		List<User> users = Arrays.asList(
				generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.SUCCESSFUL)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.SUCCESSFUL)
				, generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.SUCCESSFUL)
				);

		Mockito.when(userService.getUsersForPendingPayment()).thenReturn(users);

		List<PendingPayment> createPendingPayments = service.createPendingPayments();

		Assert.assertNotNull(createPendingPayments);
		Assert.assertEquals(users.size(), createPendingPayments.size());
		for (User user : users) {
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
		paymentPolicy.setSubweeks((byte) 10);
		currentPaymentDetails.setPaymentPolicy(paymentPolicy);
		currentPaymentDetails.setLastPaymentStatus(status);
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.addPaymentDetails(currentPaymentDetails);
		user.setSubBalance(subBalance);
		return user;
	}

	private User generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus status, boolean invalid) {
		User user = new User();
		String randomString = UUID.randomUUID().toString();
		user.setUserName(randomString);
		PaymentDetails currentPaymentDetails = new O2PSMSPaymentDetails();
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setCurrencyISO("GBP");
		paymentPolicy.setPaymentType(PaymentDetails.O2_PSMS_TYPE);
		paymentPolicy.setSubcost(BigDecimal.TEN);
		paymentPolicy.setProvider("o2");
		paymentPolicy.setSegment(SegmentType.BUSINESS);
		paymentPolicy.setSubweeks((byte) 10);
		currentPaymentDetails.setPaymentPolicy(paymentPolicy);
		currentPaymentDetails.setLastPaymentStatus(status);
		user.addPaymentDetails(currentPaymentDetails);
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.setProvider(invalid ? "non-o2" : "o2");
		user.setSegment(SegmentType.BUSINESS);
		return user;
	}
}