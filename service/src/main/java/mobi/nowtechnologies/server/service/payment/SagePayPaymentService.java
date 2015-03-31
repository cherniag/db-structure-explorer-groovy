package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.dto.PaymentDetailsDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.service.exception.ServiceException;

public interface SagePayPaymentService extends PaymentSystemService {
    public SagePayCreditCardPaymentDetails createPaymentDetails(PaymentDetailsDto paymentDto, User user, PaymentPolicy paymentPolicy) throws ServiceException;
}