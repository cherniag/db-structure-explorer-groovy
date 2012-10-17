package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;

public interface MigPaymentService extends PaymentSystemService {
	
	public static final String MIG_DELIVERED = "0";
	public static final String MIG_NOT_DELIVERED = "2";
	
	public MigPaymentDetails createPaymentDetails(String phoneNumber, User user, Community community, PaymentPolicy paymentPolicy) throws ServiceException;
	
	public MigPaymentDetails commitPaymnetDetails(User user, String verificationPin) throws ServiceException;
	
	public SubmittedPayment commitPayment(String messageId, String status, String descriptionError)  throws ServiceException;
	
	public boolean sendPin(String numbers, String message) throws ServiceException;
	
}