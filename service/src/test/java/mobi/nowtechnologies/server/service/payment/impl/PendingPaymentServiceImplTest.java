package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;

import com.google.common.collect.Lists;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.runners.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PendingPaymentServiceImplTest {

    @Mock
    UserService userServiceMock;
    @Mock
    UserRepository userRepository;
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
                PaymentDetails paymentDetails = (PaymentDetails) invocation.getArguments()[0];
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
        List<User> users = asList(generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.NONE), generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.SUCCESSFUL),
                                  generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, false), generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus.SUCCESSFUL, true));

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
        List<User> users = asList(generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.NONE), generateUserWithSagePayPaymentDetails(0, PaymentDetailsStatus.SUCCESSFUL));

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
    public void createPendingPaymentsForOneTimePaymentPolicyFirstTime() {
        List<User> users = Lists.newArrayList();
        User userWithOneTimePayment = createUserForPendingPayment(PaymentPolicyType.ONETIME, PaymentDetailsStatus.NONE);
        users.add(userWithOneTimePayment);

        when(page.getContent()).thenReturn(users);

        List<PendingPayment> pendingPayments = pendingPaymentService.createPendingPayments();

        assertFalse(pendingPayments.isEmpty());
        assertEquals(userWithOneTimePayment, pendingPayments.get(0).getUser());
        verify(userServiceMock, never()).unsubscribeUser(userWithOneTimePayment, "One time payment policy");
    }

    @Test
    public void createPendingPaymentsForOneTimePaymentPolicySecondTime() {
        List<User> users = Lists.newArrayList();
        User userWithOneTimePayment = createUserForPendingPayment(PaymentPolicyType.ONETIME, PaymentDetailsStatus.SUCCESSFUL);
        users.add(userWithOneTimePayment);

        when(page.getContent()).thenReturn(users);
        List<PendingPayment> pendingPayments = pendingPaymentService.createPendingPayments();

        assertTrue(pendingPayments.isEmpty());
        verify(userServiceMock).unsubscribeUser(userWithOneTimePayment, "One time payment policy");
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
        when(userRepository.findUsersForRetryPayment(anyInt(), any(PageRequest.class))).thenReturn(page);

        List<PendingPayment> createRetryPayments = pendingPaymentService.createRetryPayments();

        assertNotNull(createRetryPayments);
        assertEquals(maxCount, createRetryPayments.size());
    }

    private User createUserForPendingPayment(PaymentPolicyType paymentPolicyType, PaymentDetailsStatus paymentDetailsStatus) {
        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(paymentPolicy.getPaymentPolicyType()).thenReturn(paymentPolicyType);
        when(paymentPolicy.getPeriod()).thenReturn(new Period(WEEKS, 1));

        PaymentDetails paymentDetails = mock(PaymentDetails.class);
        when(paymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);
        when(paymentDetails.getLastPaymentStatus()).thenReturn(paymentDetailsStatus);

        User userWithOneTimePaymentPolicy = mock(User.class);
        when(userWithOneTimePaymentPolicy.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        return userWithOneTimePaymentPolicy;
    }


    private User generateUserWithSagePayPaymentDetails(int subBalance, PaymentDetailsStatus status) {
        String randomString = UUID.randomUUID().toString();

        User user = new User();
        user.setUserGroup(createUserGroup("c"));
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
        user.setSubBalance(subBalance);
        return user;
    }

    private UserGroup createUserGroup(String community) {
        Community c = new Community();
        c.setRewriteUrlParameter(community);

        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(c);
        return userGroup;
    }

    private User generateUserWithO2PsmsPaymentDetails(PaymentDetailsStatus status, boolean invalid) {
        String randomString = UUID.randomUUID().toString();
        User user = new User();
        user.setUserGroup(createUserGroup("c"));
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
        user.setCurrentPaymentDetails(currentPaymentDetails);
        user.setProvider(invalid ?
                         ProviderType.NON_O2 :
                         ProviderType.O2);
        user.setSegment(BUSINESS);
        return user;
    }
}