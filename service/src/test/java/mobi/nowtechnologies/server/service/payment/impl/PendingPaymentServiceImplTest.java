package mobi.nowtechnologies.server.service.payment.impl;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.common.ListDataResult;
import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
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

import static mobi.nowtechnologies.server.shared.enums.PeriodUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PendingPaymentServiceImplTest {

	private PendingPaymentService service;
	private PaymentDao paymentDao;
	private UserService userService;
	@Mock
	private PaymentPolicyService mockPaymentPolicyService;
    private int maxCount = 35;

    @Before
	public void startup() {
		Map<String, PaymentSystemService> paymentSystems = new HashMap<String, PaymentSystemService>();
		paymentSystems.put("sagePayCreditCard", new SagePayPaymentServiceImpl());
		paymentSystems.put("o2Psms", new O2PaymentServiceImpl());

		PendingPaymentServiceImpl serviceImpl = spy(new PendingPaymentServiceImpl());
		serviceImpl.setPaymentSystems(paymentSystems);
        serviceImpl.setMaxCount(maxCount);
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
				Period period = paymentPolicy.getPeriod();
				paymentPolicyDto.setPeriod(period.getDuration());
				paymentPolicyDto.setPeriodUnit(period.getPeriodUnit());
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

        ListDataResult<User> dataResult = new ListDataResult<User>(users);

		Mockito.when(userService.getUsersForPendingPayment(maxCount)).thenReturn(dataResult);

		List<PendingPayment> createPendingPayments = service.createPendingPayments();

		Assert.assertNotNull(createPendingPayments);
		Assert.assertEquals(dataResult.getData().size(), dataResult.getTotal());
		Assert.assertEquals(dataResult.getData().size() - 1, createPendingPayments.size());

		verify(userService, times(1)).unsubscribeUser(eq(users.get(7).getId()), any(UnsubscribeDto.class));
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

        ListDataResult<User> dataResult = new ListDataResult<User>(users);

		Mockito.when(userService.getUsersForPendingPayment(maxCount)).thenReturn(dataResult);

		List<PendingPayment> createPendingPayments = service.createPendingPayments();

		Assert.assertNotNull(createPendingPayments);
		Assert.assertEquals(dataResult.getTotal(), createPendingPayments.size());
		for (User user : users) {
			Assert.assertEquals(PaymentDetailsStatus.AWAITING, user.getCurrentPaymentDetails().getLastPaymentStatus());
		}
	}

    @Test
    public void createPendingPaymentsForUserCountGreaterThanAllowed() {
        List<User> users = Lists.newArrayList();
        int maxCount = 5;
        for (int i = 0; i < 10; i++) {
            users.add(generateUserWithSagePayPaymentDetails((byte) 0, PaymentDetailsStatus.NONE));
        }
        Mockito.when(userService.getUsersForPendingPayment(maxCount)).thenReturn(new ListDataResult<User>(users.subList(0, maxCount)));
        ((PendingPaymentServiceImpl)service).setMaxCount(maxCount);

        List<PendingPayment> createPendingPayments = service.createPendingPayments();

        Assert.assertNotNull(createPendingPayments);
        Assert.assertEquals(maxCount, createPendingPayments.size());
    }

    @Test
    public void createRetryPaymentsForUserCountGreaterThanAllowed() {
        int maxCount = 5;
        List<User> users = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            users.add(generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, true));
        }
        Mockito.when(userService.getUsersForRetryPayment(maxCount)).thenReturn(new ListDataResult<User>(users.subList(0, maxCount)));
        ((PendingPaymentServiceImpl)service).setMaxCount(maxCount);

        List<PendingPayment> createRetryPayments = service.createRetryPayments();

        Assert.assertNotNull(createRetryPayments);
        Assert.assertEquals(maxCount, createRetryPayments.size());
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
		paymentPolicy.setPeriod(new Period().withDuration(10).withPeriodUnit(WEEKS));
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
		paymentPolicy.setProvider(O2);
		paymentPolicy.setSegment(BUSINESS);
		paymentPolicy.setPeriod(new Period().withDuration(10).withPeriodUnit(WEEKS));
		currentPaymentDetails.setPaymentPolicy(paymentPolicy);
		currentPaymentDetails.setLastPaymentStatus(status);
		user.addPaymentDetails(currentPaymentDetails);
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.setProvider(invalid ? ProviderType.NON_O2 : ProviderType.O2);
		user.setSegment(BUSINESS);
		return user;
	}
}