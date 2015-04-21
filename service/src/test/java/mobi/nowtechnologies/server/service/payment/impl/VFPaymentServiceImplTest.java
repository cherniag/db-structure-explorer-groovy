package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.PendingPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PaymentEventNotifier;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.VFResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.SMSCDeliveryReceipt;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * User: Alexsandr_Kolpakov Date: 10/22/13 Time: 1:27 PM
 */
@RunWith(PowerMockRunner.class)
public class VFPaymentServiceImplTest {

    private VFPaymentServiceImpl fixture;
    
    @Mock
    private VFNZSMSGatewayServiceImpl gatewayServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PendingPaymentService pendingPaymentServiceMock;
    
    @Mock
    private PaymentEventNotifier paymentEventNotifier;
    
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private SubmittedPaymentRepository submittedPaymentRepository;

    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @Mock
    private PendingPaymentRepository pendingPaymentRepository;

    @Before
    public void setUp() throws Exception {
        fixture = spy(new VFPaymentServiceImpl());
        fixture.setUserRepository(userRepository);
        fixture.setGatewayService(gatewayServiceMock);
        fixture.setUserService(userServiceMock);
        fixture.setPendingPaymentService(pendingPaymentServiceMock);
        fixture.setPaymentEventNotifier(paymentEventNotifier);
        fixture.setApplicationEventPublisher(applicationEventPublisher);
        fixture.setSubmittedPaymentRepository(submittedPaymentRepository);
        fixture.setPaymentDetailsRepository(paymentDetailsRepository);
        fixture.setPendingPaymentRepository(pendingPaymentRepository);
    }

    @Test
    public void testMakePayment_Success() throws Exception {
        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        VFPSMSPaymentDetails vfpsmsPaymentDetails = new VFPSMSPaymentDetails();
        vfpsmsPaymentDetails.setPhoneNumber("+642111111111");
        vfpsmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        pendingPayment.setPaymentDetails(vfpsmsPaymentDetails);
        String msg = "msg";

        doReturn(null).when(gatewayServiceMock).send(anyString(), anyString(), anyString(), any(SMSCDeliveryReceipt.class), anyLong());

        PaymentSystemResponse result = fixture.makePayment(pendingPayment, msg);

        Assert.assertEquals(VFResponse.class, result.getClass());
        Assert.assertEquals(true, result.isFuture());

        verify(gatewayServiceMock, times(1)).send(vfpsmsPaymentDetails.getPhoneNumber(), msg, paymentPolicy.getShortCode(), SMSCDeliveryReceipt.SUCCESS_FAILURE, fixture.getExpireMillis());
    }

    @Test
    public void testSupports_Supported_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setDestAddress("5003");
        fixture.setPaymentCodes(new HashSet<>(Arrays.asList(deliverSm.getDestAddress())));

        boolean result = fixture.supports(deliverSm);

        assertEquals(true, result);
    }

    @Test
    public void testSupports_NotSupported_InvalidDestAddress_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setDestAddress("5000");
        fixture.setPaymentCodes(new HashSet<>(Arrays.asList("5003")));

        boolean result = fixture.supports(deliverSm);

        assertEquals(false, result);
    }

    @Test
    public void testSupports_NotSupported_NotDeliveryReceipt_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setDestAddress("5003");
        fixture.setPaymentCodes(new HashSet<>(Arrays.asList("5003")));

        boolean result = fixture.supports(deliverSm);

        assertEquals(false, result);
    }

    @Test
    public void testProcess_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("+642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "DELIVRD", "000", "It is test").getBytes());

        VFResponse vfResponse = VFResponse.futureResponse().parse(deliverSm);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        VFPSMSPaymentDetails vfpsmsPaymentDetails = new VFPSMSPaymentDetails();
        vfpsmsPaymentDetails.setPhoneNumber("+642111111111");
        vfpsmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        pendingPayment.setPaymentDetails(vfpsmsPaymentDetails);

        PowerMockito.doReturn(Collections.singletonList(user)).when(userRepository).findByMobile("+" + deliverSm.getSourceAddr());
        PowerMockito.doReturn(Collections.singletonList(pendingPayment)).when(pendingPaymentServiceMock).getPendingPayments(user.getId());
        PowerMockito.doReturn(null).when(fixture).commitPayment(pendingPayment, vfResponse);
        Answer<?> answer  = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        };

        when(submittedPaymentRepository.save(any(SubmittedPayment.class))).thenAnswer(answer);

        fixture.process(vfResponse);

        verify(userRepository, times(1)).findByMobile("+" + deliverSm.getSourceAddr());
        verify(pendingPaymentServiceMock, times(1)).getPendingPayments(user.getId());
        verify(applicationEventPublisher).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void testProcess_NotCorrespondingPendingPayments_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "DELIVRD", "000", "It is test").getBytes());

        VFResponse vfResponse = VFResponse.futureResponse().parse(deliverSm);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        O2PSMSPaymentDetails vfpsmsPaymentDetails = new O2PSMSPaymentDetails();
        vfpsmsPaymentDetails.setPhoneNumber("+642111111111");
        vfpsmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        pendingPayment.setPaymentDetails(vfpsmsPaymentDetails);

        PowerMockito.doReturn(Collections.singletonList(user)).when(userRepository).findByMobile("+" + deliverSm.getSourceAddr());
        PowerMockito.doReturn(Collections.singletonList(pendingPayment)).when(pendingPaymentServiceMock).getPendingPayments(user.getId());
        PowerMockito.doReturn(null).when(fixture).commitPayment(pendingPayment, vfResponse);

        fixture.process(vfResponse);

        verify(userRepository, times(1)).findByMobile("+" + deliverSm.getSourceAddr());
        verify(pendingPaymentServiceMock, times(1)).getPendingPayments(user.getId());
        verify(fixture, times(0)).commitPayment(pendingPayment, vfResponse);
    }

    @Test
    public void testProcess_NotCorrespondingUsers_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setSmscDeliveryReceipt();
        deliverSm.setSourceAddr("642111111111");
        deliverSm.setShortMessage(buildMessage("108768587", "000", "000", "1310020119", "1310020119", "DELIVRD", "000", "It is test").getBytes());

        VFResponse vfResponse = VFResponse.futureResponse().parse(deliverSm);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        O2PSMSPaymentDetails vfpsmsPaymentDetails = new O2PSMSPaymentDetails();
        vfpsmsPaymentDetails.setPhoneNumber("+642111111111");
        vfpsmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        pendingPayment.setPaymentDetails(vfpsmsPaymentDetails);

        PowerMockito.doReturn(Collections.emptyList()).when(userRepository).findByMobile("+" + deliverSm.getSourceAddr());
        PowerMockito.doReturn(Collections.singletonList(pendingPayment)).when(pendingPaymentServiceMock).getPendingPayments(user.getId());
        PowerMockito.doReturn(null).when(fixture).commitPayment(pendingPayment, vfResponse);

        fixture.process(vfResponse);

        verify(userRepository, times(1)).findByMobile("+" + deliverSm.getSourceAddr());
        verify(pendingPaymentServiceMock, times(0)).getPendingPayments(user.getId());
        verify(fixture, times(0)).commitPayment(pendingPayment, vfResponse);
    }

    protected String buildMessage(String msgId, String sub, String dlvrd, String submitDate, String doneDate, String stat, String err, String text) {
        return "id:" + msgId + " sub:" + sub + " dlvrd:" + dlvrd + " submit date:" + submitDate + " done date:" + doneDate + " stat:" + stat + " err:" + err + " Text:" + text;
    }
}
