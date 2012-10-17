package mobi.nowtechnologies.server.service.payment;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractPaymentSystemService implements PaymentSystemService, ApplicationEventPublisherAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentSystemService.class);
	
	protected EntityService entityService;
	
	private int retriesOnError;
	
	private long expireMillis;
	
	private ApplicationEventPublisher applicationEventPublisher;
	
	protected PaymentDetailsService paymentDetailsService;
	
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) throws ServiceException {
		LOGGER.info("Starting commit process for pending payment tx:{} ...", pendingPayment.getInternalTxId());
		SubmittedPayment payment = SubmittedPayment.valueOf(pendingPayment);		
		
		PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
		
		PaymentDetailsStatus status = PaymentDetailsStatus.SUCCESSFUL;
		if (!response.isSuccessful() && HttpServletResponse.SC_OK==response.getHttpStatus()) {
			status = PaymentDetailsStatus.ERROR;
			payment.setDescriptionError(response.getDescriptionError());
			
			if (paymentDetails.getMadeRetries() == paymentDetails.getRetriesOnError()) {
				paymentDetails.setActivated(false);
			}
		}
		
		// Store submitted payment
		payment.setStatus(status);
		paymentDetails.setLastPaymentStatus(status);
		payment = entityService.updateEntity(payment); 
		entityService.updateEntity(paymentDetails);
		LOGGER.info("Submitted payment with id {} has been created", payment.getI());
		
		// Send sync-event about commited payment
		if(payment.getStatus() == PaymentDetailsStatus.SUCCESSFUL)
			applicationEventPublisher.publishEvent(new PaymentEvent(payment));
		
		// Deleting pending payment
		entityService.removeEntity(PendingPayment.class, pendingPayment.getI());

		LOGGER.info("Commit process for payment with tx:{} has been finished with status {}.", payment.getInternalTxId(), payment.getStatus());
		
		return payment;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public int getRetriesOnError() {
		return retriesOnError;
	}

	public void setRetriesOnError(int retriesOnError) {
		this.retriesOnError = retriesOnError;
	}
	
	public long getExpireMillis() {
		return expireMillis;
	}

	public void setExpireMillis(long expireMillis) {
		this.expireMillis = expireMillis;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	public void setPaymentDetailsRepository(PaymentDetailsRepository paymentDetailsRepository) {
		this.paymentDetailsRepository = paymentDetailsRepository;
	}

	/**
	 * @deprecated Use {@link #paymentDetailsService}
	 */
	@Deprecated
	public PaymentDetailsRepository getPaymentDetailsRepository() {
		return paymentDetailsRepository;
	}
	
	public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
		this.paymentDetailsService = paymentDetailsService;
	}
}