package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.dao.*;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Locale;

import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class, OperatorDao.class, AccountLog.class, SubmittedPayment.class, O2PSMSPaymentDetails.class})
public class O2PaymentServiceImplTest {

    private UserService userServiceMock;
    private UserRepository mockUserRepository;
    private UserDao mockUserDao;
    private EntityService mockEntityService;
    private AccountLogService mockAccountLogService;
    private CommunityResourceBundleMessageSource mockCommunityResourceBundleMessageSource;
    private MigHttpService mockMigHttpService;
    private PaymentDetailsService mockPaymentDetailsService;
    private CommunityService mockCommunityService;
    private CountryService mockCountryService;
    private O2ProviderService mockO2ClientService;
    private DeviceService mockDeviceService;
    private O2PaymentServiceImpl o2PaymentServiceImplSpy;
    private ApplicationEventPublisher mockApplicationEventPublisher;
    private PaymentDetailsRepository mockPaymentDetailsRepository;
    private RefundService refundServiceMock;

    @Before
    public void setUp() throws Exception {
        o2PaymentServiceImplSpy = spy(new O2PaymentServiceImpl());

        userServiceMock = mock(UserService.class);

        mockCountryService = mock(CountryService.class);
        mockCommunityResourceBundleMessageSource = mock(CommunityResourceBundleMessageSource.class);
        mockUserRepository = mock(UserRepository.class);
        mockPaymentDetailsService = mock(PaymentDetailsService.class);
        mockUserDao = mock(UserDao.class);
        mockEntityService = mock(EntityService.class);
        mockCommunityService = mock(CommunityService.class);
        mockDeviceService = mock(DeviceService.class);
        mockMigHttpService = mock(MigHttpService.class);
        mockAccountLogService = mock(AccountLogService.class);
        mockO2ClientService = mock(O2ProviderService.class);
        mockUserRepository = mock(UserRepository.class);
        mockPaymentDetailsRepository = mock(PaymentDetailsRepository.class);
        refundServiceMock = PowerMockito.mock(RefundService.class);

        mockStatic(UserStatusDao.class);

        mockApplicationEventPublisher = mock(ApplicationEventPublisher.class);

        o2PaymentServiceImplSpy.setUserService(userServiceMock);
        o2PaymentServiceImplSpy.setApplicationEventPublisher(mockApplicationEventPublisher);
        o2PaymentServiceImplSpy.setMessageSource(mockCommunityResourceBundleMessageSource);
        o2PaymentServiceImplSpy.setO2ClientService(mockO2ClientService);
        o2PaymentServiceImplSpy.setEntityService(mockEntityService);
        o2PaymentServiceImplSpy.setPaymentDetailsRepository(mockPaymentDetailsRepository);
        o2PaymentServiceImplSpy.setPaymentDetailsService(mockPaymentDetailsService);
        o2PaymentServiceImplSpy.setRefundService(refundServiceMock);
    }

    @Test
    public void testStartPayment_SuccessfulO2Response_Success() throws Exception {
        final User user = UserFactory.createUser();
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        o2psmsPaymentDetails.setActivated(true);

        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider("o2");
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setNextSubPayment(Utils.getEpochSeconds() - 50 * 60 * 60);
        user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);


        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2psmsPaymentDetails);

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

        when(
                mockO2ClientService.makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
                        paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue()))
                .thenReturn(o2Response);

        when(mockEntityService.updateEntity(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

            @Override
            public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
                PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];

                assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
                assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());

                return passedPendingPayment;
            }
        });

        when(mockEntityService.updateEntity(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.SUCCESSFUL, o2psmsPaymentDetails.getLastPaymentStatus());
                assertTrue(o2psmsPaymentDetails.isActivated());

                return o2psmsPaymentDetails;
            }
        });

        doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());


        mock(SubmittedPayment.class);
        final SubmittedPayment submittedPayment = new SubmittedPayment();
        whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);

        when(mockEntityService.updateEntity(eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

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
        verify(
                mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
                paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue());
        verify(mockEntityService, times(1)).updateEntity(pendingPayment);
        verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
        verify(mockApplicationEventPublisher, times(1)).publishEvent(argThat(matcher));

    }

    @Test
    public void testStartPayment_FailureO2ResponseAndMadeRetriesEqRetriesOnErrorAndNextSubPaymentInThePast_Success() throws Exception {
        final int epochSeconds = 55555;

        final User user = UserFactory.createUser();
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        o2psmsPaymentDetails.setMadeRetries(Integer.MAX_VALUE);
        o2psmsPaymentDetails.setRetriesOnError(Integer.MAX_VALUE);
        o2psmsPaymentDetails.setActivated(true);
        o2psmsPaymentDetails.setOwner(user);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider("o2");
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setNextSubPayment(epochSeconds - 50 * 60 * 60);
        user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);


        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2psmsPaymentDetails);

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

        when(
                mockO2ClientService.makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
                        paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue()))
                .thenReturn(o2Response);

        when(mockEntityService.updateEntity(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

            @Override
            public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
                PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];

                assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
                assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());

                return passedPendingPayment;
            }
        });

        when(mockEntityService.updateEntity(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.ERROR, o2psmsPaymentDetails.getLastPaymentStatus());

                return o2psmsPaymentDetails;
            }
        });

        doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());


        mock(SubmittedPayment.class);
        final SubmittedPayment submittedPayment = new SubmittedPayment();
        whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);

        when(mockEntityService.updateEntity(eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

            @Override
            public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
                SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
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
        verify(
                mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
                paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue());
        verify(mockEntityService, times(1)).updateEntity(pendingPayment);
        verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
        verify(mockApplicationEventPublisher, times(0)).publishEvent(argThat(matcher));
        verify(userServiceMock, times(1)).unsubscribeUser(user, o2Response.getDescriptionError());

    }

    @Test
    public void testStartPayment_FailureO2ResponseAndMedeRetriesNotEqRetriesOnError_Success() throws Exception {
        final User user = UserFactory.createUser();
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
        o2psmsPaymentDetails.setMadeRetries(Integer.MIN_VALUE);
        o2psmsPaymentDetails.setRetriesOnError(Integer.MAX_VALUE);
        o2psmsPaymentDetails.setActivated(true);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider("o2");
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setNextSubPayment(Utils.getEpochSeconds() - 50 * 60 * 60);
        user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
        user.setCurrentPaymentDetails(o2psmsPaymentDetails);


        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(o2psmsPaymentDetails);

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

        when(
                mockO2ClientService.makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
                        paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue()))
                .thenReturn(o2Response);

        when(mockEntityService.updateEntity(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

            @Override
            public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
                PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];

                assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
                assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());

                return passedPendingPayment;
            }
        });

        when(mockEntityService.updateEntity(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

            @Override
            public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.ERROR, o2psmsPaymentDetails.getLastPaymentStatus());
                assertTrue(o2psmsPaymentDetails.isActivated());

                return o2psmsPaymentDetails;
            }
        });

        doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());


        mock(SubmittedPayment.class);
        final SubmittedPayment submittedPayment = new SubmittedPayment();
        whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);

        when(mockEntityService.updateEntity(eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

            @Override
            public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
                SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];

                assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
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
        verify(
                mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
                paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue());
        verify(mockEntityService, times(1)).updateEntity(pendingPayment);
        verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
        verify(mockApplicationEventPublisher, times(0)).publishEvent(argThat(matcher));

    }

    @Test
    public void testCommitPaymentDetails_Success() throws Exception {
        final User user = UserFactory.createUser();

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
    }
}