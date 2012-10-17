package mobi.nowtechnologies.server.service.payment.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.SubmitedPaymentRepository;
import mobi.nowtechnologies.server.service.payment.SubmitedPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class SubmitedPaymentServiceImpl implements SubmitedPaymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubmitedPaymentServiceImpl.class);

	private SubmitedPaymentRepository submitedPaymentRepository;

	public void setSubmitedPaymentRepository(SubmitedPaymentRepository submitedPaymentRepository) {
		this.submitedPaymentRepository = submitedPaymentRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmittedPayment> findByUserIdAndPaymentStatus(List<Integer> userIds, List<PaymentDetailsStatus> paymentDetailsStatuses) {
		LOGGER.debug("input parameters userIds, paymentDetailsStatuses: [{}], [{}]", userIds, paymentDetailsStatuses);

		List<SubmittedPayment> submittedPayments = submitedPaymentRepository.findByUserIdAndPaymentStatus(userIds, paymentDetailsStatuses);

		LOGGER.info("Output parameter submittedPayments=[{}]", submittedPayments);
		return submittedPayments;
	}

}
