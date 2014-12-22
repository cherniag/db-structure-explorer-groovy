package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.service.IPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PaymentServiceImpl implements IPaymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

	private SubmittedPaymentRepository submittedPaymentRepository;
	
	public void setSubmittedPaymentRepository(SubmittedPaymentRepository submittedPaymentRepository) {
		this.submittedPaymentRepository = submittedPaymentRepository;
	}

}
