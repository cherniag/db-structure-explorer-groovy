package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.RefundService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractPaymentSystemService implements PaymentSystemService, ApplicationEventPublisherAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentSystemService.class);
	
	protected EntityService entityService;
	
	private int retriesOnError;
	
	private long expireMillis;
	
	private ApplicationEventPublisher applicationEventPublisher;
	
	protected PaymentDetailsService paymentDetailsService;
	
	private PaymentDetailsRepository paymentDetailsRepository;
	
	protected UserService userService;

    private RefundService refundService;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) throws ServiceException {
		LOGGER.info("Starting commit process for pending payment tx:{} ...", pendingPayment.getInternalTxId());
		SubmittedPayment submittedPayment = SubmittedPayment.valueOf(pendingPayment);
		
		final User user = pendingPayment.getUser();
		final int epochSeconds = Utils.getEpochSeconds();
		
		PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
		
		final PaymentDetailsStatus status;
		final int httpStatus = response.getHttpStatus();
		if (!response.isSuccessful() && HttpServletResponse.SC_OK == httpStatus) {
			status = PaymentDetailsStatus.ERROR;
			submittedPayment.setDescriptionError(response.getDescriptionError());
			paymentDetails.setDescriptionError(response.getDescriptionError());
			paymentDetails.setErrorCode(response.getErrorCode());

			if (paymentDetails.getMadeRetries() == paymentDetails.getRetriesOnError() && epochSeconds > user.getNextSubPayment()) {
				userService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
			}
		} else if (response.isSuccessful()) {
			status = PaymentDetailsStatus.SUCCESSFUL;
			paymentDetails.setDescriptionError(null);
			paymentDetails.setErrorCode(null);
		} else {
			status = PaymentDetailsStatus.ERROR;
			final String descriptionError = "Unexpected http status code ["+httpStatus+"] so the madeRetries willn't be incremented";
			submittedPayment.setDescriptionError(descriptionError);
			paymentDetails.setDescriptionError(descriptionError);
			paymentDetails.setErrorCode(null);
			paymentDetails.decrementRetries();
		}

		if (submittedPayment.getExternalTxId() == null) {
			submittedPayment.setExternalTxId("");
		}
		
		// Store submitted payment
		submittedPayment.setStatus(status);
		paymentDetails.setLastPaymentStatus(status);
		submittedPayment = entityService.updateEntity(submittedPayment); 
		entityService.updateEntity(paymentDetails);
		LOGGER.info("Submitted payment with id {} has been created", submittedPayment.getI());
		
		// Send sync-event about committed payment
		if(submittedPayment.getStatus() == PaymentDetailsStatus.SUCCESSFUL)
			applicationEventPublisher.publishEvent(new PaymentEvent(submittedPayment));
		
		// Deleting pending payment
		entityService.removeEntity(PendingPayment.class, pendingPayment.getI());

		LOGGER.info("Commit process for payment with tx:{} has been finished with status {}.", submittedPayment.getInternalTxId(), submittedPayment.getStatus());
		
		return submittedPayment;
	}

    @Transactional(propagation=Propagation.REQUIRED)
    protected PaymentDetails commitPaymentDetails(User user, PaymentDetails newPaymentDetails){

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        user.setCurrentPaymentDetails(newPaymentDetails);
        newPaymentDetails.setOwner(user);
        newPaymentDetails.setActivated(true);
        newPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        newPaymentDetails.setRetriesOnError(getRetriesOnError());
        newPaymentDetails.setMadeRetries(0);

        return paymentDetailsRepository.save(newPaymentDetails);
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
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

    public void setRefundService(RefundService refundService) {
        this.refundService = refundService;
    }
}