package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.repository.SubmitedPaymentRepository;
import mobi.nowtechnologies.server.service.IPaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PaymentServiceImpl implements IPaymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

	private SubmitedPaymentRepository submitedPaymentRepository;
	
	public void setSubmitedPaymentRepository(SubmitedPaymentRepository submitedPaymentRepository) {
		this.submitedPaymentRepository = submitedPaymentRepository;
	}

}
