package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PendingPaymentServiceImplTest {
	@Mock
	UserService userServiceMock;
	@Mock
	PaymentPolicyService paymentPolicyServiceMock;
	@Mock
	PendingPaymentRepository pendingPaymentRepositoryMock;
	@Mock
	Map<String, PaymentSystemService> paymentSystems;
	@Mock
	PaymentSystemService paymentSystemService;
	@InjectMocks
	PendingPaymentServiceImpl pendingPaymentService;

	@Mock
	Page<User> page;
	@Captor
	ArgumentCaptor<PendingPayment> pendingPaymentCaptor;

	int maxCount = 35;

    @Before
	public void setUp() {
		pendingPaymentService.setMaxCount(maxCount);

		when(paymentSystems.get(anyString())).thenReturn(paymentSystemService);
		when(page.hasNextPage()).thenReturn(false);
		when(userServiceMock.getUsersForPendingPayment(maxCount)).thenReturn(page);

		when(pendingPaymentRepositoryMock.save(pendingPaymentCaptor.capture())).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return pendingPaymentCaptor.getValue();
			}
		});


		when(userServiceMock.updateUser(Mockito.any(User.class))).thenAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				return (User) invocation.getArguments()[0];
			}
		});

		when(paymentPolicyServiceMock.getPaymentPolicy(any(PaymentDetails.class))).thenAnswer(new Answer<PaymentPolicyDto>() {
			@Override
			public PaymentPolicyDto answer(InvocationOnMock invocation) throws Throwable {
				PaymentDetails paymentDetails = (PaymentDetails)invocation.getArguments()[0];
				PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
				PaymentPolicyDto paymentPolicyDto = new PaymentPolicyDto();
				paymentPolicyDto.setSubcost(paymentPolicy.getSubcost());
				Period period = paymentPolicy.getPeriod();
				paymentPolicyDto.setDuration(period.getDuration());
				paymentPolicyDto.setDurationUnit(period.getDurationUnit());
				paymentPolicyDto.setCurrencyISO(paymentPolicy.getCurrencyISO());
				
				return paymentPolicyDto;
			}
		});
	}

	@Test
	public void createPendingPaymentsForValidAndInvalidUsers() {
		//given
		List<User> users = asList(
				generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.NONE),
				generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.SUCCESSFUL),
				generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, false),
				generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, true)
				);

		when(page.getContent()).thenReturn(users);

		//when
		List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();

		//then
		assertNotNull(createPendingPayments);
		assertEquals(users.size() - 1, createPendingPayments.size());
		verify(userServiceMock, times(1)).unsubscribeUser(eq(users.get(3).getId()), any(UnsubscribeDto.class));
	}

	@Test
	public void createPendingPaymentsSuccessful() {
		//given
		List<User> users = asList(
				generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.NONE),
				generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.SUCCESSFUL)
				);

		when(page.getContent()).thenReturn(users);

		//when
		List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();

		//then
		assertNotNull(createPendingPayments);
		assertEquals(users.size(), createPendingPayments.size());
		for (User user : users) {
			assertEquals(PaymentDetailsStatus.AWAITING, user.getCurrentPaymentDetails().getLastPaymentStatus());
		}
	}

    @Test
    public void createPendingPaymentsForUserCountGreaterThanAllowed() {
		//given
		int maxCount = 5;
		List<User> users = nCopies(5, generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.NONE));

		when(page.getContent()).thenReturn(users);
		when(page.hasNextPage()).thenReturn(true);

		pendingPaymentService.setMaxCount(maxCount);
		when(userServiceMock.getUsersForPendingPayment(maxCount)).thenReturn(page);

		//when
		List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();

		//then
		assertNotNull(createPendingPayments);
		assertEquals(maxCount, createPendingPayments.size());
	}

    @Test
    public void createRetryPaymentsForUserCountGreaterThanAllowed() {
		//given
        int maxCount = 5;
		List<User> users = nCopies(5, generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, true));

		when(page.getContent()).thenReturn(users);
		when(page.hasNextPage()).thenReturn(true);

		pendingPaymentService.setMaxCount(maxCount);
		when(userServiceMock.getUsersForRetryPayment(maxCount)).thenReturn(page);

		List<PendingPayment> createRetryPayments = pendingPaymentService.createRetryPayments();

		assertNotNull(createRetryPayments);
		assertEquals(maxCount, createRetryPayments.size());
    }

    private User generateUserWithSagePayPaymentDetails(int subBalance, PaymentDetailsStatus status) {
		User user = new User();
		String randomString = UUID.randomUUID().toString();
		user.setUserName(randomString);
		PaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setCurrencyISO("GBP");
		paymentPolicy.setPaymentType(PaymentDetails.SAGEPAY_CREDITCARD_TYPE);
		paymentPolicy.setSubcost(BigDecimal.TEN);
		paymentPolicy.setPeriod(new Period().withDuration(10).withDurationUnit(WEEKS));
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
		paymentPolicy.setPeriod(new Period().withDuration(10).withDurationUnit(WEEKS));
		currentPaymentDetails.setPaymentPolicy(paymentPolicy);
		currentPaymentDetails.setLastPaymentStatus(status);
		user.addPaymentDetails(currentPaymentDetails);
		user.setCurrentPaymentDetails(currentPaymentDetails);
		user.setProvider(invalid ? ProviderType.NON_O2 : ProviderType.O2);
		user.setSegment(BUSINESS);
		return user;
	}
}