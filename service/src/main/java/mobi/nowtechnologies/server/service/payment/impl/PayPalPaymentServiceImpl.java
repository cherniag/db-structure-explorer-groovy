package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PayPalPaymentService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.PeriodUnit;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

import static mobi.nowtechnologies.server.shared.enums.PeriodUnit.DAYS;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public class PayPalPaymentServiceImpl extends AbstractPaymentSystemService implements PayPalPaymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PayPalPaymentServiceImpl.class);

	private PayPalHttpService httpService;

	private String redirectURL;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PayPalPaymentDetails makePaymentWithPaymentDetails(PaymentDetailsDto paymentDto, User user, PaymentPolicy paymentPolicy) throws ServiceException {
		PayPalPaymentDetails newPaymentDetails = commitPaymentDetails(paymentDto.getToken(), user, paymentPolicy, false);

		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setOfferId(paymentDto.getOfferId());
		pendingPayment.setAmount(new BigDecimal(paymentDto.getAmount()));
		pendingPayment.setCurrencyISO(paymentDto.getCurrency());
		pendingPayment.setPaymentSystem(PaymentDetails.PAYPAL_TYPE);
		pendingPayment.setUser(user);
		pendingPayment.setExternalTxId("NONE");
		pendingPayment.setTimestamp(Utils.getEpochMillis());
		pendingPayment.setExpireTimeMillis(getExpireMillis());
		pendingPayment.setType(PaymentDetailsType.PAYMENT);
		pendingPayment.setPaymentDetails(newPaymentDetails);
		pendingPayment.setPeriod(new Period().withDuration(0).withPeriodUnit(DAYS));
		entityService.saveEntity(pendingPayment);

		startPayment(pendingPayment);

		return newPaymentDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void startPayment(PendingPayment pendingPayment) throws ServiceException {
		PayPalPaymentDetails currentPaymentDetails = (PayPalPaymentDetails) pendingPayment.getUser().getCurrentPaymentDetails();
        PaymentPolicy currentPaymentPolicy = currentPaymentDetails.getPaymentPolicy();
        String communityRewriteUrlParameter = currentPaymentPolicy.getCommunity() != null ? currentPaymentPolicy.getCommunity().getRewriteUrlParameter() : null;
		PayPalResponse response = httpService.makeReferenceTransactionRequest(currentPaymentDetails.getBillingAgreementTxId(), pendingPayment.getCurrencyISO(), pendingPayment.getAmount(), communityRewriteUrlParameter);
		pendingPayment.setExternalTxId(response.getTransactionId());
		entityService.updateEntity(pendingPayment);
		LOGGER.info("PayPal responsed {} for pending payment id: {}", response, pendingPayment.getI());
		commitPayment(pendingPayment, response);
	}

	@Override
	public PayPalPaymentDetails createPaymentDetails(String billingDescription, String successUrl, String failUrl, User user, PaymentPolicy paymentPolicy) throws ServiceException {
		LOGGER.info("Starting creation PayPal payment details");
        String communityRewriteUrlParameter = paymentPolicy.getCommunity() != null ? paymentPolicy.getCommunity().getRewriteUrlParameter() : null;
		PayPalResponse response = httpService.makeTokenRequest(billingDescription, successUrl, failUrl, paymentPolicy.getCurrencyISO(), communityRewriteUrlParameter);
		if (!response.isSuccessful())
			throw new ServiceException("Can't connect to PayPal. Please try again later.");

		PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
		paymentDetails.setBillingAgreementTxId(redirectURL.concat("?cmd=_express-checkout&token=").concat(response.getToken())); // Temporary setting token to billingAgreement
		paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
		paymentDetails.setPaymentPolicy(paymentPolicy);
		paymentDetails.resetMadeAttempts();
		paymentDetails.setRetriesOnError(getRetriesOnError());
		paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());
		paymentDetails.setActivated(false);
		paymentDetails.setOwner(user);

		LOGGER.info("Creation PayPal payment details - REDIRECT to PayPal page");
		return paymentDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PayPalPaymentDetails commitPaymentDetails(String token, User user, PaymentPolicy paymentPolicy, boolean activated) throws ServiceException {
		LOGGER.info("Committing PayPal payment details for user {} ...", user.getUserName());
        String communityRewriteUrlParameter = paymentPolicy.getCommunity() != null ? paymentPolicy.getCommunity().getRewriteUrlParameter() : null;
		PayPalResponse response = httpService.makeBillingAgreementRequest(token, communityRewriteUrlParameter);
		if (response.isSuccessful()) {
			LOGGER.debug("Got billing agreement {} from PayPal for user {}", response.getBillingAgreement(), user.getUserName());

			PayPalPaymentDetails newPaymentDetails = new PayPalPaymentDetails();
			newPaymentDetails.setBillingAgreementTxId(response.getBillingAgreement());
			newPaymentDetails.setPaymentPolicy(paymentPolicy);
            newPaymentDetails.setCreationTimestampMillis(Utils.getEpochMillis());

            newPaymentDetails = (PayPalPaymentDetails) super.commitPaymentDetails(user, newPaymentDetails);
            newPaymentDetails.setActivated(activated);

			LOGGER.info("Done creation of PayPal payment details for user {}", user.getUserName());
			return newPaymentDetails;
		}

		throw new ServiceException("pay.paypal.error.external", response.getDescriptionError());
	}

	@Override
	public PaymentSystemResponse getExpiredResponse() {
		PayPalResponse response = new PayPalResponse(new BasicResponse() {
			@Override
			public int getStatusCode() {
				return HttpServletResponse.SC_OK;
			}

			@Override
			public String getMessage() {
				return "PayPal pending payment has been expired";
			}
		});
		return response;
	}

	public void setHttpService(PayPalHttpService httpService) {
		this.httpService = httpService;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
}