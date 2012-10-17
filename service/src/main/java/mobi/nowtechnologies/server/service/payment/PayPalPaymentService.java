package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;

public interface PayPalPaymentService extends PaymentSystemService {
	
	/**
	 * 
	 * @param dto
	 * @param user
	 * @param paymentPolicy
	 * @return An URL to PayPal with token for creating billing agreement.
	 * @throws ServiceException
	 */

	PayPalPaymentDetails createPaymentDetails(String billingDescription, String successUrl, String failUrl, User user, PaymentPolicy paymentPolicy) throws ServiceException;

	PayPalPaymentDetails commitPaymentDetails(String token, User user, PaymentPolicy paymentPolicy, boolean activated) throws ServiceException;

	PayPalPaymentDetails makePaymentWithPaymentDetails(PaymentDetailsDto paymentDto, User user, PaymentPolicy paymentPolicy) throws ServiceException;	
}