package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
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

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.*;

public abstract class AbstractPaymentSystemService implements PaymentSystemService, ApplicationEventPublisherAware {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentSystemService.class);
	
	protected EntityService entityService;
	
	private int retriesOnError;
	
	private long expireMillis;
	
	private ApplicationEventPublisher applicationEventPublisher;
	
	protected PaymentDetailsService paymentDetailsService;
	
	private PaymentDetailsRepository paymentDetailsRepository;
	
	protected UserService userService;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) throws ServiceException {
		LOGGER.info("Starting commit process for pending payment tx:{} ...", pendingPayment.getInternalTxId());
		SubmittedPayment submittedPayment = SubmittedPayment.valueOf(pendingPayment);

		PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();
		
		final PaymentDetailsStatus status;
		final int httpStatus = response.getHttpStatus();
		if (!response.isSuccessful() && SC_OK == httpStatus) {
			status = ERROR;
			submittedPayment.setDescriptionError(response.getDescriptionError());
			paymentDetails.setDescriptionError(response.getDescriptionError());
			paymentDetails.setErrorCode(response.getErrorCode());
            paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();
		} else if (response.isSuccessful()) {
			status = SUCCESSFUL;
			paymentDetails.setDescriptionError(null);
			paymentDetails.setErrorCode(null);
            paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();
		} else {
			status = ERROR;
			final String descriptionError = "Unexpected http status code ["+httpStatus+"] so the madeRetries won't be incremented";
			submittedPayment.setDescriptionError(descriptionError);
			paymentDetails.setDescriptionError(descriptionError);
			paymentDetails.setErrorCode(null);
		}

		if (isNull(submittedPayment.getExternalTxId())) {
			submittedPayment.setExternalTxId("");
		}
		
		// Store submitted payment
		submittedPayment.setStatus(status);
        submittedPayment = entityService.updateEntity(submittedPayment);
        LOGGER.info("Submitted payment with id {} has been created", submittedPayment.getI());

        paymentDetails.setLastPaymentStatus(status);
        entityService.updateEntity(paymentDetails);

		// Send sync-event about committed payment
		if(submittedPayment.getStatus().equals(SUCCESSFUL)){
			applicationEventPublisher.publishEvent(new PaymentEvent(submittedPayment));
        }else {
            checkPaymentDetailsAndUnSubscribe(response, pendingPayment);
        }
		
		// Deleting pending payment
		entityService.removeEntity(PendingPayment.class, pendingPayment.getI());

		LOGGER.info("Commit process for payment with tx:{} has been finished with status {}.", submittedPayment.getInternalTxId(), submittedPayment.getStatus());
		
		return submittedPayment;
	}

    private void checkPaymentDetailsAndUnSubscribe(PaymentSystemResponse response, PendingPayment pendingPayment) {
        PaymentDetails paymentDetails = pendingPayment.getPaymentDetails();

        if (paymentDetails.shouldBeUnSubscribed()) {
            userService.unsubscribeUser(paymentDetails.getOwner(), response.getDescriptionError());
        }
    }

    @Transactional(propagation=Propagation.REQUIRED)
    protected PaymentDetails commitPaymentDetails(User user, PaymentDetails newPaymentDetails){

        paymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");

        user.setCurrentPaymentDetails(newPaymentDetails);
        newPaymentDetails.setOwner(user);
        newPaymentDetails.setActivated(true);
        newPaymentDetails.setLastPaymentStatus(NONE);
        newPaymentDetails.setRetriesOnError(getRetriesOnError());
        newPaymentDetails.resetMadeAttempts();

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
}