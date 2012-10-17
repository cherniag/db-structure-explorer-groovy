package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.service.exception.ServiceException;

// TODO delete this before commit
@Deprecated
public interface PaymentManager {
	
	public void startPayment(PendingPayment pendingPayment) throws ServiceException;
	
	public void commitPayment() throws ServiceException;
}