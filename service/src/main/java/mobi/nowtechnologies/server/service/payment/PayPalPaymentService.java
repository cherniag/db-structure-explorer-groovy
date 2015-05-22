package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.exception.ServiceException;

public interface PayPalPaymentService extends PaymentSystemService {

    PayPalPaymentDetails createPaymentDetails(String billingDescription, String successUrl, String failUrl, User user, PaymentPolicy paymentPolicy) throws ServiceException;

    PayPalPaymentDetails commitPaymentDetails(String token, User user, PaymentPolicy paymentPolicy, boolean activated) throws ServiceException;
}