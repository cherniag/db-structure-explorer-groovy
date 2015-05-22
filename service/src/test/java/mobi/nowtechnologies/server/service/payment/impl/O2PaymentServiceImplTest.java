package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.payment.PaymentEventNotifier;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.EXTERNAL_ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.SUCCESSFUL;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;

import java.util.Locale;

import org.springframework.context.ApplicationEventPublisher;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class, Utils.class, DeviceTypeCache.class, AccountLog.class, SubmittedPayment.class, O2PSMSPaymentDetails.class})
public class O2PaymentServiceImplTest {

    private UserService userServiceMock;
    private CommunityResourceBundleMessageSource mockCommunityResourceBundleMessageSource;
    private PaymentDetailsService mockPaymentDetailsService;
    private O2ProviderService mockO2ClientService;
    private O2PaymentServiceImpl o2PaymentServiceImplSpy;
    private ApplicationEventPublisher mockApplicationEventPublisher;
    private PaymentEventNotifier paymentEventNotifier;
    @Mock
    private PaymentDetailsRepository mockPaymentDetailsRepository;
    @Mock
    private PendingPaymentRepository mockPendingPaymentRepository;
    @Mock
    private SubmittedPaymentRepository mockSubmittedPaymentRepository;

    @Before
    public void setUp() throws Exception {
        o2PaymentServiceImplSpy = spy(new O2PaymentServiceImpl());

        userServiceMock = mock(UserService.class);

        mockCommunityResourceBundleMessageSource = mock(CommunityResourceBundleMessageSource.class);
        mockPaymentDetailsService = mock(PaymentDetailsService.class);
        mockO2ClientService = mock(O2ProviderService.class);
        paymentEventNotifier = mock(PaymentEventNotifier.class);

        mockApplicationEventPublisher = mock(ApplicationEventPublisher.class);

        o2PaymentServiceImplSpy.setUserService(userServiceMock);
        o2PaymentServiceImplSpy.setApplicationEventPublisher(mockApplicationEventPublisher);
        o2PaymentServiceImplSpy.setMessageSource(mockCommunityResourceBundleMessageSource);
        o2PaymentServiceImplSpy.setO2ClientService(mockO2ClientService);
        o2PaymentServiceImplSpy.setPaymentDetailsService(mockPaymentDetailsService);
        o2PaymentServiceImplSpy.setPaymentEventNotifier(paymentEventNotifier);
        o2PaymentServiceImplSpy.setPaymentDetailsRepository(mockPaymentDetailsRepository);
        o2PaymentServiceImplSpy.setPendingPaymentRepository(mockPendingPaymentRepository);
        o2PaymentServiceImplSpy.setSubmittedPaymentRepository(mockSubmittedPaymentRepository);
    }

    @Test
    public void testStartPayment_SuccessfulO2Response_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        o2psmsPaymentDetails.withLastPaymentStatus(ERROR);
        o2psmsPaymentDetails.withMadeRetries(2);
        o2psmsPaymentDetails.setRetriesOnError(3);
        o2psmsPaymentDetails.setActivated(true);
        o2psmsPaymentDetails.setOwner(user);

        community.setRewriteUrlParameter(Community.O2_COMMUNITY_REWRITE_URL);
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setNextSubPayment(Utils.getEpochSeconds() - 50 * 60 * 60);
        user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);


        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2psmsPaymentDetails);
        pendingPayment.setPeriod(new Period().withDuration(1).withDurationUnit(WEEKS));

        final Boolean smsNotify = Boolean.TRUE;
        when(mockCommunityResourceBundleMessageSource.getMessage("o2", "sms.o2Psms.send", null, null)).thenReturn(String.valueOf(smsNotify));
        final String message = "message";
        when(mockCommunityResourceBundleMessageSource.getMessage(eq("o2"), eq("sms.o2Psms"), (Object[]) any(), (Locale) isNull())).thenReturn(message);

        final int internalTxId = Integer.MAX_VALUE;

        mockStatic(Utils.class);
        when(Utils.getBigRandomInt()).thenReturn(internalTxId);

        final String externalTxId = String.valueOf(Integer.MIN_VALUE);
        O2Response o2Response = O2Response.successfulO2Response();
        o2Response.setExternalTxId(externalTxId);

        when(mockO2ClientService.makePremiumSMSRequest(user.getId(),
                                                       String.valueOf(internalTxId),
                                                       pendingPayment.getAmount(),
                                                       o2psmsPaymentDetails.getPhoneNumber(),
                                                       message,
                                                       paymentPolicy.getContentCategory(),
                                                       paymentPolicy.getContentType(),
                                                       paymentPolicy.getContentDescription(),
                                                       paymentPolicy.getSubMerchantId(),
                                                       smsNotify.booleanValue())).thenReturn(o2Response);

        when(mockPendingPaymentRepository.save(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

            @Override
            public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
                PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];

                assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
                assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());

                return passedPendingPayment;
            }
        });

        when(mockPaymentDetailsRepository.save(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.SUCCESSFUL, o2psmsPaymentDetails.getLastPaymentStatus());
                assertTrue(o2psmsPaymentDetails.isActivated());

                return o2psmsPaymentDetails;
            }
        });

        doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());


        mock(SubmittedPayment.class);
        final SubmittedPayment submittedPayment = new SubmittedPayment();
        whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);

        when(mockSubmittedPaymentRepository.save(eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

            @Override
            public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
                SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());

                return submittedPayment;
            }
        });

        final ArgumentMatcher<PaymentEvent> matcher = new ArgumentMatcher<PaymentEvent>() {

            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;

                assertNotNull(paymentEvent);

                final AbstractPayment payment = paymentEvent.getPayment();
                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };

        doNothing().when(mockApplicationEventPublisher).publishEvent(argThat(matcher));

        o2PaymentServiceImplSpy.startPayment(pendingPayment);

        verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage("o2", "sms.o2Psms.send", null, null);
        verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage(eq("o2"), eq("sms.o2Psms"), (Object[]) any(), (Locale) isNull());
        verify(mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(),
                                                                    String.valueOf(internalTxId),
                                                                    pendingPayment.getAmount(),
                                                                    o2psmsPaymentDetails.getPhoneNumber(),
                                                                    message,
                                                                    paymentPolicy.getContentCategory(),
                                                                    paymentPolicy.getContentType(),
                                                                    paymentPolicy.getContentDescription(),
                                                                    paymentPolicy.getSubMerchantId(),
                                                                    smsNotify.booleanValue());
        verify(mockPendingPaymentRepository, times(1)).save(pendingPayment);
        verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());
        verify(mockApplicationEventPublisher, times(1)).publishEvent(argThat(matcher));

    }

    @Test
    public void testStartPayment_FailureO2ResponseAndMadeRetriesEqRetriesOnErrorAndNextSubPaymentInThePast_Success() throws Exception {
        final int epochSeconds = 55555;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        o2psmsPaymentDetails.withMadeRetries(0);
        o2psmsPaymentDetails.setRetriesOnError(3);
        o2psmsPaymentDetails.withMadeAttempts(1);
        o2psmsPaymentDetails.withLastPaymentStatus(EXTERNAL_ERROR);
        o2psmsPaymentDetails.setActivated(true);
        o2psmsPaymentDetails.setOwner(user);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setNextSubPayment(epochSeconds - 50 * 60 * 60);
        user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);


        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2psmsPaymentDetails);
        pendingPayment.setPeriod(new Period().withDuration(1).withDurationUnit(WEEKS));

        final Boolean smsNotify = Boolean.TRUE;
        when(mockCommunityResourceBundleMessageSource.getMessage("o2", "sms.o2Psms.send", null, null)).thenReturn(String.valueOf(smsNotify));
        final String message = "message";
        when(mockCommunityResourceBundleMessageSource.getMessage(eq("o2"), eq("sms.o2Psms"), (Object[]) any(), (Locale) isNull())).thenReturn(message);

        final int internalTxId = Integer.MAX_VALUE;

        mockStatic(Utils.class);
        when(Utils.getBigRandomInt()).thenReturn(internalTxId);
        when(Utils.getEpochSeconds()).thenReturn(epochSeconds);

        final String externalTxId = String.valueOf(Integer.MIN_VALUE);
        final O2Response o2Response = O2Response.failO2Response("");
        o2Response.setExternalTxId(externalTxId);

        when(mockO2ClientService.makePremiumSMSRequest(user.getId(),
                                                       String.valueOf(internalTxId),
                                                       pendingPayment.getAmount(),
                                                       o2psmsPaymentDetails.getPhoneNumber(),
                                                       message,
                                                       paymentPolicy.getContentCategory(),
                                                       paymentPolicy.getContentType(),
                                                       paymentPolicy.getContentDescription(),
                                                       paymentPolicy.getSubMerchantId(),
                                                       smsNotify.booleanValue())).thenReturn(o2Response);

        when(mockPendingPaymentRepository.save(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

            @Override
            public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
                PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];

                assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
                assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());

                return passedPendingPayment;
            }
        });

        when(mockPaymentDetailsRepository.save(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(ERROR, o2psmsPaymentDetails.getLastPaymentStatus());

                return o2psmsPaymentDetails;
            }
        });

        doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());


        mock(SubmittedPayment.class);
        final SubmittedPayment submittedPayment = new SubmittedPayment();
        whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);

        when(mockSubmittedPaymentRepository.save(eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

            @Override
            public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
                SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];

                assertEquals(ERROR, submittedPayment.getStatus());
                assertEquals(o2Response.getDescriptionError(), submittedPayment.getDescriptionError());

                return submittedPayment;
            }
        });

        final ArgumentMatcher<PaymentEvent> matcher = new ArgumentMatcher<PaymentEvent>() {

            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;

                assertNotNull(paymentEvent);

                final AbstractPayment payment = paymentEvent.getPayment();
                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };

        doNothing().when(mockApplicationEventPublisher).publishEvent(argThat(matcher));

        when(userServiceMock.unsubscribeUser(user, o2Response.getDescriptionError())).thenReturn(user);

        o2PaymentServiceImplSpy.startPayment(pendingPayment);

        verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage("o2", "sms.o2Psms.send", null, null);
        verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage(eq("o2"), eq("sms.o2Psms"), (Object[]) any(), (Locale) isNull());
        verify(mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(),
                                                                    String.valueOf(internalTxId),
                                                                    pendingPayment.getAmount(),
                                                                    o2psmsPaymentDetails.getPhoneNumber(),
                                                                    message,
                                                                    paymentPolicy.getContentCategory(),
                                                                    paymentPolicy.getContentType(),
                                                                    paymentPolicy.getContentDescription(),
                                                                    paymentPolicy.getSubMerchantId(),
                                                                    smsNotify.booleanValue());
        verify(mockPendingPaymentRepository, times(1)).save(pendingPayment);
        verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());
        verify(mockApplicationEventPublisher, times(0)).publishEvent(argThat(matcher));
        verify(userServiceMock, times(1)).unsubscribeUser(user, o2Response.getDescriptionError());

    }

    @Test
    public void testStartPayment_FailureO2ResponseAndMedeRetriesNotEqRetriesOnError_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        o2psmsPaymentDetails.withLastPaymentStatus(SUCCESSFUL);
        o2psmsPaymentDetails.withMadeRetries(0);
        o2psmsPaymentDetails.setRetriesOnError(3);
        o2psmsPaymentDetails.setActivated(true);
        o2psmsPaymentDetails.withOwner(user);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setNextSubPayment(Utils.getEpochSeconds() - 50 * 60 * 60);
        user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);


        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2psmsPaymentDetails);
        pendingPayment.setPeriod(new Period().withDuration(1).withDurationUnit(WEEKS));

        final Boolean smsNotify = Boolean.TRUE;
        when(mockCommunityResourceBundleMessageSource.getMessage("o2", "sms.o2Psms.send", null, null)).thenReturn(String.valueOf(smsNotify));
        final String message = "message";
        when(mockCommunityResourceBundleMessageSource.getMessage(eq("o2"), eq("sms.o2Psms"), (Object[]) any(), (Locale) isNull())).thenReturn(message);

        final int internalTxId = Integer.MAX_VALUE;

        mockStatic(Utils.class);
        when(Utils.getBigRandomInt()).thenReturn(internalTxId);

        final String externalTxId = String.valueOf(Integer.MIN_VALUE);
        final O2Response o2Response = O2Response.failO2Response("");
        o2Response.setExternalTxId(externalTxId);

        when(mockO2ClientService.makePremiumSMSRequest(user.getId(),
                                                       String.valueOf(internalTxId),
                                                       pendingPayment.getAmount(),
                                                       o2psmsPaymentDetails.getPhoneNumber(),
                                                       message,
                                                       paymentPolicy.getContentCategory(),
                                                       paymentPolicy.getContentType(),
                                                       paymentPolicy.getContentDescription(),
                                                       paymentPolicy.getSubMerchantId(),
                                                       smsNotify.booleanValue())).thenReturn(o2Response);

        when(mockPendingPaymentRepository.save(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

            @Override
            public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
                PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];

                assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
                assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());

                return passedPendingPayment;
            }
        });

        when(mockPaymentDetailsRepository.save(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(ERROR, o2psmsPaymentDetails.getLastPaymentStatus());
                assertTrue(o2psmsPaymentDetails.isActivated());

                return o2psmsPaymentDetails;
            }
        });

        doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());


        mock(SubmittedPayment.class);
        final SubmittedPayment submittedPayment = new SubmittedPayment();
        whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);

        when(mockSubmittedPaymentRepository.save(eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

            @Override
            public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
                SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];

                assertEquals(ERROR, submittedPayment.getStatus());
                assertEquals(o2Response.getDescriptionError(), submittedPayment.getDescriptionError());

                return submittedPayment;
            }
        });

        final ArgumentMatcher<PaymentEvent> matcher = new ArgumentMatcher<PaymentEvent>() {

            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;

                assertNotNull(paymentEvent);

                final AbstractPayment payment = paymentEvent.getPayment();
                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };

        doNothing().when(mockApplicationEventPublisher).publishEvent(argThat(matcher));

        o2PaymentServiceImplSpy.startPayment(pendingPayment);

        verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage("o2", "sms.o2Psms.send", null, null);
        verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage(eq("o2"), eq("sms.o2Psms"), (Object[]) any(), (Locale) isNull());
        verify(mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(),
                                                                    String.valueOf(internalTxId),
                                                                    pendingPayment.getAmount(),
                                                                    o2psmsPaymentDetails.getPhoneNumber(),
                                                                    message,
                                                                    paymentPolicy.getContentCategory(),
                                                                    paymentPolicy.getContentType(),
                                                                    paymentPolicy.getContentDescription(),
                                                                    paymentPolicy.getSubMerchantId(),
                                                                    smsNotify.booleanValue());
        verify(mockPendingPaymentRepository, times(1)).save(pendingPayment);
        verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());
        verify(mockApplicationEventPublisher, times(0)).publishEvent(argThat(matcher));

    }
/*
    @Test
    public void testCommitPaymentDetails_Success() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        final long currentTimeMillis = Long.MAX_VALUE;

        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);

        when(mockPaymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details")).thenReturn(user);

        final int retriesOnError = Integer.MIN_VALUE;
        when(o2PaymentServiceImplSpy.getRetriesOnError()).thenReturn(retriesOnError);

        when(mockPaymentDetailsRepository.save(any(O2PSMSPaymentDetails.class))).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                final O2PSMSPaymentDetails actualO2PSMSPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(0, actualO2PSMSPaymentDetails.getMadeRetries());
                assertEquals(null, actualO2PSMSPaymentDetails.getDescriptionError());
                assertEquals(0, actualO2PSMSPaymentDetails.getDisableTimestampMillis());
                assertEquals(PaymentDetailsStatus.NONE, actualO2PSMSPaymentDetails.getLastPaymentStatus());
                assertEquals(retriesOnError, actualO2PSMSPaymentDetails.getRetriesOnError());
                assertEquals(currentTimeMillis, actualO2PSMSPaymentDetails.getCreationTimestampMillis());
                assertEquals(true, actualO2PSMSPaymentDetails.isActivated());
                assertEquals(user, actualO2PSMSPaymentDetails.getOwner());

                return actualO2PSMSPaymentDetails;
            }
        });

        O2PSMSPaymentDetails actualO2PSMSPaymentDetails = o2PaymentServiceImplSpy.commitPaymentDetails(user, paymentPolicy);

        assertNotNull(actualO2PSMSPaymentDetails);

        assertEquals(actualO2PSMSPaymentDetails, user.getCurrentPaymentDetails());

        verify(mockPaymentDetailsService, times(1)).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(o2PaymentServiceImplSpy, times(1)).getRetriesOnError();
        verify(mockPaymentDetailsRepository, times(1)).save(any(O2PSMSPaymentDetails.class));
    }*/
}