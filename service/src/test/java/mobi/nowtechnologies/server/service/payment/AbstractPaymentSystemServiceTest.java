package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PendingPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.RETRY;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import java.net.HttpURLConnection;

import org.springframework.context.ApplicationEventPublisher;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {PaymentEvent.class, Utils.class, SubmittedPayment.class})
public class AbstractPaymentSystemServiceTest {

    @Mock
    private UserService mockUserService;
    @Mock
    private PaymentDetailsRepository mockPaymentDetailsRepository;
    @Mock
    SubmittedPaymentRepository mockSubmittedPaymentRepository;
    @Mock
    PendingPaymentRepository mockPendingPaymentRepository;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;
    @Mock
    private PaymentEventNotifier paymentEventNotifier;
    private AbstractPaymentSystemService mockAbstractPaymentSystemService;

    @Before
    public void setUp() throws Exception {
        mockAbstractPaymentSystemService = Mockito.mock(AbstractPaymentSystemService.class, Mockito.CALLS_REAL_METHODS);

        mockAbstractPaymentSystemService.setUserService(mockUserService);
        mockAbstractPaymentSystemService.setApplicationEventPublisher(mockApplicationEventPublisher);
        mockAbstractPaymentSystemService.setPaymentEventNotifier(paymentEventNotifier);
        mockAbstractPaymentSystemService.setPaymentDetailsRepository(mockPaymentDetailsRepository);
        mockAbstractPaymentSystemService.setSubmittedPaymentRepository(mockSubmittedPaymentRepository);
        mockAbstractPaymentSystemService.setPendingPaymentRepository(mockPendingPaymentRepository);
    }

    @Test
    public void testCommitPayment_SuccessfulResponse_Success() throws Exception {
        final int currentTimeSeconds = Integer.MIN_VALUE;

        PaymentSystemResponse response = O2Response.successfulO2Response();

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withLastPaymentStatus(NONE).withRetriesOnError(3);
        user.setCurrentPaymentDetails(paymentDetails);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);

        final SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setPaymentDetails(paymentDetails);

        PowerMockito.mockStatic(SubmittedPayment.class);
        Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

        Mockito.when(mockSubmittedPaymentRepository.save(submittedPayment)).thenReturn(submittedPayment);
        Mockito.when(mockPaymentDetailsRepository.save(paymentDetails)).thenReturn(paymentDetails);

        final ArgumentMatcher<PaymentEvent> applicationEventPublisherMatcher = new ArgumentMatcher<PaymentEvent>() {
            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;
                final AbstractPayment payment = paymentEvent.getPayment();

                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };
        Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());
        Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError())).thenReturn(user);

        SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, response);

        assertNotNull(actualSubmittedPayment);
        assertEquals(submittedPayment, actualSubmittedPayment);
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, actualSubmittedPayment.getStatus());

        assertEquals(PaymentDetailsStatus.SUCCESSFUL, paymentDetails.getLastPaymentStatus());
        assertEquals(1, paymentDetails.getMadeRetries());
        assertEquals(0, paymentDetails.getMadeAttempts());

        Mockito.verify(mockSubmittedPaymentRepository, times(1)).save(submittedPayment);
        Mockito.verify(mockPaymentDetailsRepository, times(1)).save(paymentDetails);
        Mockito.verify(mockApplicationEventPublisher, times(1)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());

        Mockito.verify(mockUserService, times(0)).unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
    }

    @Test
    public void testCommitPayment_FailResponseAndMadeRetriesNotEqRetriesOnError_Success() throws Exception {
        final int currentTimeSeconds = Integer.MIN_VALUE;

        PaymentSystemResponse response = O2Response.failO2Response("");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withI(Long.MAX_VALUE).withLastPaymentStatus(ERROR).withPaymentPolicy(new PaymentPolicy());
        paymentDetails.setRetriesOnError(3);
        paymentDetails.withMadeRetries(1);
        paymentDetails.withOwner(user);
        user.setCurrentPaymentDetails(paymentDetails);
        user.setLastSuccessfulPaymentDetails(paymentDetails);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setType(RETRY);
        pendingPayment.setPaymentDetails(paymentDetails);

        PowerMockito.mockStatic(Utils.class);
        Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);

        final SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setPaymentDetails(paymentDetails);
        submittedPayment.setType(RETRY);

        PowerMockito.mockStatic(SubmittedPayment.class);
        Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

        Mockito.when(mockSubmittedPaymentRepository.save(submittedPayment)).thenReturn(submittedPayment);
        Mockito.when(mockPaymentDetailsRepository.save(paymentDetails)).thenReturn(paymentDetails);

        final ArgumentMatcher<PaymentEvent> applicationEventPublisherMatcher = new ArgumentMatcher<PaymentEvent>() {
            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;
                final AbstractPayment payment = paymentEvent.getPayment();

                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };
        Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());
        Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError())).thenReturn(user);

        SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, response);

        assertNotNull(actualSubmittedPayment);
        assertEquals(submittedPayment, actualSubmittedPayment);
        assertEquals(ERROR, actualSubmittedPayment.getStatus());

        assertEquals(ERROR, paymentDetails.getLastPaymentStatus());
        assertEquals(2, paymentDetails.getMadeRetries());
        assertEquals(0, paymentDetails.getMadeAttempts());

        Mockito.verify(mockSubmittedPaymentRepository, times(1)).save(submittedPayment);
        Mockito.verify(mockPaymentDetailsRepository, times(1)).save(paymentDetails);
        Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());

        Mockito.verify(mockUserService, times(0)).unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
    }

    @Test
    public void testCommitPayment_FailResponseAndMadeRetriesEqRetriesOnError_Success() throws Exception {
        final int currentTimeSeconds = Integer.MAX_VALUE;

        PaymentSystemResponse response = O2Response.failO2Response("");

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withLastPaymentStatus(ERROR).withPaymentPolicy(new PaymentPolicy());
        paymentDetails.setRetriesOnError(3);
        paymentDetails.withMadeRetries(0);
        paymentDetails.withMadeAttempts(1);

        user.setCurrentPaymentDetails(paymentDetails);
        user.setNextSubPayment(Integer.MIN_VALUE);

        paymentDetails.withOwner(user);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setType(RETRY);

        PowerMockito.mockStatic(Utils.class);
        Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);

        final SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setPaymentDetails(paymentDetails);
        submittedPayment.setType(RETRY);

        PowerMockito.mockStatic(SubmittedPayment.class);
        Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

        Mockito.when(mockSubmittedPaymentRepository.save(submittedPayment)).thenReturn(submittedPayment);
        Mockito.when(mockPaymentDetailsRepository.save(paymentDetails)).thenReturn(paymentDetails);

        final ArgumentMatcher<PaymentEvent> applicationEventPublisherMatcher = new ArgumentMatcher<PaymentEvent>() {
            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;
                final AbstractPayment payment = paymentEvent.getPayment();

                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };
        Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());
        Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError())).thenReturn(user);

        SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, response);

        assertNotNull(actualSubmittedPayment);
        assertEquals(submittedPayment, actualSubmittedPayment);
        assertEquals(ERROR, actualSubmittedPayment.getStatus());
        assertEquals(null, actualSubmittedPayment.getExternalTxId());

        assertEquals(ERROR, paymentDetails.getLastPaymentStatus());
        assertEquals(1, paymentDetails.getMadeRetries());
        assertEquals(1, paymentDetails.getMadeAttempts());

        Mockito.verify(mockSubmittedPaymentRepository, times(1)).save(submittedPayment);
        Mockito.verify(mockPaymentDetailsRepository, times(1)).save(paymentDetails);
        Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());

        Mockito.verify(mockUserService, times(1)).unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
    }

    @Test
    public void testCommitPayment_FailResponseAndNot200HttpStatusCode_Success() throws Exception {
        final int currentTimeSeconds = Integer.MAX_VALUE;

        PaymentSystemResponse mockPaymentSystemResponse = Mockito.mock(PaymentSystemResponse.class);
        Mockito.when(mockPaymentSystemResponse.isSuccessful()).thenReturn(false);
        Mockito.when(mockPaymentSystemResponse.getHttpStatus()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy());
        paymentDetails.setRetriesOnError(3);
        paymentDetails.withMadeRetries(2);
        paymentDetails.withOwner(user);
        user.setCurrentPaymentDetails(paymentDetails);
        user.setNextSubPayment(Integer.MIN_VALUE);

        PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);

        PowerMockito.mockStatic(Utils.class);
        Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);

        final SubmittedPayment submittedPayment = new SubmittedPayment();

        PowerMockito.mockStatic(SubmittedPayment.class);
        Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

        Mockito.when(mockSubmittedPaymentRepository.save(submittedPayment)).thenReturn(submittedPayment);
        Mockito.when(mockPaymentDetailsRepository.save(paymentDetails)).thenReturn(paymentDetails);

        final ArgumentMatcher<PaymentEvent> applicationEventPublisherMatcher = new ArgumentMatcher<PaymentEvent>() {
            @Override
            public boolean matches(Object argument) {
                PaymentEvent paymentEvent = (PaymentEvent) argument;
                final AbstractPayment payment = paymentEvent.getPayment();

                assertNotNull(payment);
                assertEquals(submittedPayment, payment);

                return true;
            }
        };
        Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.doNothing().when(mockPendingPaymentRepository).delete(pendingPayment.getI());
        Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), mockPaymentSystemResponse.getDescriptionError())).thenReturn(user);

        SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, mockPaymentSystemResponse);

        final String descriptionError = "Unexpected http status code [" + HttpURLConnection.HTTP_BAD_REQUEST + "] so the madeRetries won't be incremented";

        assertNotNull(actualSubmittedPayment);
        assertEquals(submittedPayment, actualSubmittedPayment);
        assertEquals(ERROR, actualSubmittedPayment.getStatus());
        assertEquals(null, actualSubmittedPayment.getExternalTxId());
        assertEquals(descriptionError, actualSubmittedPayment.getDescriptionError());

        assertEquals(ERROR, paymentDetails.getLastPaymentStatus());
        assertEquals(2, paymentDetails.getMadeRetries());
        assertEquals(0, paymentDetails.getMadeAttempts());
        assertEquals(descriptionError, paymentDetails.getDescriptionError());

        Mockito.verify(mockSubmittedPaymentRepository, times(1)).save(submittedPayment);
        Mockito.verify(mockPaymentDetailsRepository, times(1)).save(paymentDetails);
        Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
        Mockito.verify(mockPendingPaymentRepository, times(1)).delete(pendingPayment.getI());

        Mockito.verify(mockUserService, times(0)).unsubscribeUser(paymentDetails.getOwner(), mockPaymentSystemResponse.getDescriptionError());
    }

}