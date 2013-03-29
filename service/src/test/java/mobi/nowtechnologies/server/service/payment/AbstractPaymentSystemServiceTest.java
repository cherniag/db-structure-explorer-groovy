package mobi.nowtechnologies.server.service.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import mobi.nowtechnologies.server.persistence.domain.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.PendingPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {PaymentEvent.class, Utils.class, SubmittedPayment.class })
public class AbstractPaymentSystemServiceTest {

	private EntityService mockEntityService;
	private PaymentDetailsService mockPaymentDetailsService;
	private UserService mockUserService;
	private AbstractPaymentSystemService mockAbstractPaymentSystemService;
	private PaymentDetailsRepository mockPaymentDetailsRepository;
	private PaymentDetailsService mockPaymentDetailsService2;
	private ApplicationEventPublisher mockApplicationEventPublisher;

	@Before
	public void setUp()
			throws Exception {
		mockEntityService = Mockito.mock(EntityService.class);
		mockPaymentDetailsService = Mockito.mock(PaymentDetailsService.class);
		mockUserService = Mockito.mock(UserService.class);
		mockPaymentDetailsRepository = Mockito.mock(PaymentDetailsRepository.class);
		mockApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

		mockAbstractPaymentSystemService = Mockito.mock(AbstractPaymentSystemService.class, Mockito.CALLS_REAL_METHODS);

		mockAbstractPaymentSystemService.setEntityService(mockEntityService);
		mockAbstractPaymentSystemService.setPaymentDetailsService(mockPaymentDetailsService2);
		mockAbstractPaymentSystemService.setUserService(mockUserService);
		mockAbstractPaymentSystemService.setPaymentDetailsRepository(mockPaymentDetailsRepository);
		mockAbstractPaymentSystemService.setApplicationEventPublisher(mockApplicationEventPublisher);
	}

	@Test
	public void testCommitPayment_SuccesfulResponse_Success()
			throws Exception {
		final int curremtTimeSeconds = Integer.MIN_VALUE;

		PaymentSystemResponse response = O2Response.successfulO2Response();

		User user = UserFactory.createUser();

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		user.setCurrentPaymentDetails(paymentDetails);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.getEpochSeconds()).thenReturn(curremtTimeSeconds);

		final SubmittedPayment submittedPayment = new SubmittedPayment();

		PowerMockito.mockStatic(SubmittedPayment.class);
		Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

		Mockito.when(mockEntityService.updateEntity(submittedPayment)).thenReturn(submittedPayment);
		Mockito.when(mockEntityService.updateEntity(paymentDetails)).thenReturn(paymentDetails);

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
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError())).thenReturn(user);

		SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, response);

		assertNotNull(actualSubmittedPayment);
		assertEquals(submittedPayment, actualSubmittedPayment);
		assertEquals(PaymentDetailsStatus.SUCCESSFUL, actualSubmittedPayment.getStatus());
		
		assertEquals(curremtTimeSeconds, user.getLastPaymentTryInCycleSeconds());
		assertEquals(PaymentDetailsStatus.SUCCESSFUL, paymentDetails.getLastPaymentStatus());

		Mockito.verify(mockEntityService, times(1)).updateEntity(submittedPayment);
		Mockito.verify(mockEntityService, times(1)).updateEntity(paymentDetails);
		Mockito.verify(mockApplicationEventPublisher, times(1)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		Mockito.verify(mockUserService, times(0)).unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
	}
	
	@Test
	public void testCommitPayment_FailResponseAndMadeRetriesNotEqRetriesOnError_Success()
			throws Exception {
		final int curremtTimeSeconds = Integer.MIN_VALUE;

		PaymentSystemResponse response = O2Response.failO2Response("");

		User user = UserFactory.createUser();

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setMadeRetries(1);
		user.setCurrentPaymentDetails(paymentDetails);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.getEpochSeconds()).thenReturn(curremtTimeSeconds);

		final SubmittedPayment submittedPayment = new SubmittedPayment();

		PowerMockito.mockStatic(SubmittedPayment.class);
		Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

		Mockito.when(mockEntityService.updateEntity(submittedPayment)).thenReturn(submittedPayment);
		Mockito.when(mockEntityService.updateEntity(paymentDetails)).thenReturn(paymentDetails);

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
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError())).thenReturn(user);

		SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, response);

		assertNotNull(actualSubmittedPayment);
		assertEquals(submittedPayment, actualSubmittedPayment);
		assertEquals(PaymentDetailsStatus.ERROR, actualSubmittedPayment.getStatus());
		
		assertEquals(curremtTimeSeconds, user.getLastPaymentTryInCycleSeconds());
		assertEquals(PaymentDetailsStatus.ERROR, paymentDetails.getLastPaymentStatus());

		Mockito.verify(mockEntityService, times(1)).updateEntity(submittedPayment);
		Mockito.verify(mockEntityService, times(1)).updateEntity(paymentDetails);
		Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		Mockito.verify(mockUserService, times(0)).unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
	}

	@Test
	public void testCommitPayment_FailResponseAndMadeRetriesEqRetriesOnError_Success()
			throws Exception {
		final int curremtTimeSeconds = Integer.MAX_VALUE;

		PaymentSystemResponse response = O2Response.failO2Response("");

		User user = UserFactory.createUser();

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setMadeRetries(3);
		user.setCurrentPaymentDetails(paymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.getEpochSeconds()).thenReturn(curremtTimeSeconds);

		final SubmittedPayment submittedPayment = new SubmittedPayment();

		PowerMockito.mockStatic(SubmittedPayment.class);
		Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

		Mockito.when(mockEntityService.updateEntity(submittedPayment)).thenReturn(submittedPayment);
		Mockito.when(mockEntityService.updateEntity(paymentDetails)).thenReturn(paymentDetails);

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
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError())).thenReturn(user);

		SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, response);

		assertNotNull(actualSubmittedPayment);
		assertEquals(submittedPayment, actualSubmittedPayment);
		assertEquals(PaymentDetailsStatus.ERROR, actualSubmittedPayment.getStatus());
		assertEquals("", actualSubmittedPayment.getExternalTxId());
		
		assertEquals(curremtTimeSeconds, user.getLastPaymentTryInCycleSeconds());
		assertEquals(PaymentDetailsStatus.ERROR, paymentDetails.getLastPaymentStatus());

		Mockito.verify(mockEntityService, times(1)).updateEntity(submittedPayment);
		Mockito.verify(mockEntityService, times(1)).updateEntity(paymentDetails);
		Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		Mockito.verify(mockUserService, times(1)).unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
	}
	
	@Test
	public void testCommitPayment_FailResponseAndNot200HttpStatusCode_Success()
			throws Exception {
		final int curremtTimeSeconds = Integer.MAX_VALUE;

		PaymentSystemResponse mockPaymentSystemResponse = Mockito.mock(PaymentSystemResponse.class);
		Mockito.when(mockPaymentSystemResponse.isSuccessful()).thenReturn(false);
		Mockito.when(mockPaymentSystemResponse.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST.value());

		User user = UserFactory.createUser();

		PaymentDetails paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setMadeRetries(3);
		user.setCurrentPaymentDetails(paymentDetails);
		user.setNextSubPayment(Integer.MIN_VALUE);

		PendingPayment pendingPayment = PendingPaymentFactory.createPendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.getEpochSeconds()).thenReturn(curremtTimeSeconds);

		final SubmittedPayment submittedPayment = new SubmittedPayment();

		PowerMockito.mockStatic(SubmittedPayment.class);
		Mockito.when(SubmittedPayment.valueOf(pendingPayment)).thenReturn(submittedPayment);

		Mockito.when(mockEntityService.updateEntity(submittedPayment)).thenReturn(submittedPayment);
		Mockito.when(mockEntityService.updateEntity(paymentDetails)).thenReturn(paymentDetails);

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
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.when(mockUserService.unsubscribeUser(paymentDetails.getOwner(), mockPaymentSystemResponse.getDescriptionError())).thenReturn(user);

		SubmittedPayment actualSubmittedPayment = mockAbstractPaymentSystemService.commitPayment(pendingPayment, mockPaymentSystemResponse);

		assertNotNull(actualSubmittedPayment);
		assertEquals(submittedPayment, actualSubmittedPayment);
		assertEquals(PaymentDetailsStatus.ERROR, actualSubmittedPayment.getStatus());
		assertEquals("", actualSubmittedPayment.getExternalTxId());
		
		assertEquals(curremtTimeSeconds, user.getLastPaymentTryInCycleSeconds());
		assertEquals(PaymentDetailsStatus.ERROR, paymentDetails.getLastPaymentStatus());

		Mockito.verify(mockEntityService, times(1)).updateEntity(submittedPayment);
		Mockito.verify(mockEntityService, times(1)).updateEntity(paymentDetails);
		Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(applicationEventPublisherMatcher));
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		Mockito.verify(mockUserService, times(0)).unsubscribeUser(paymentDetails.getOwner(), mockPaymentSystemResponse.getDescriptionError());
	}

}