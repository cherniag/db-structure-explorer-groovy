package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface O2PaymentService {
	
	public O2PSMSPaymentDetails createPaymentDetails(String phoneNumber, User user, PaymentPolicy paymentPolicy) throws ServiceException;
	
	public O2PSMSPaymentDetails commitPaymnetDetails(User user) throws ServiceException;

}
