package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.service.payment.SubmittedPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class SubmittedPaymentServiceImpl implements SubmittedPaymentService {

	static final PageRequest ONE = new PageRequest(0, 1);

	private static final Logger LOGGER = LoggerFactory.getLogger(SubmittedPaymentServiceImpl.class);

	private SubmittedPaymentRepository submittedPaymentRepository;

	public void setSubmittedPaymentRepository(SubmittedPaymentRepository submittedPaymentRepository) {
		this.submittedPaymentRepository = submittedPaymentRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmittedPayment> findByUserIdAndPaymentStatus(List<Integer> userIds, List<PaymentDetailsStatus> paymentDetailsStatuses) {
		LOGGER.debug("input parameters userIds, paymentDetailsStatuses: [{}], [{}]", userIds, paymentDetailsStatuses);

		List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(userIds, paymentDetailsStatuses);

		LOGGER.info("Output parameter submittedPayments=[{}]", submittedPayments);
		return submittedPayments;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public SubmittedPayment save(SubmittedPayment submittedPayment) {
		LOGGER.debug("input parameters submittedPayment: [{}]", submittedPayment);
		
		submittedPayment = submittedPaymentRepository.save(submittedPayment);
		
		LOGGER.debug("Output parameter submittedPayment=[{}]", submittedPayment);
		return submittedPayment;
	}


	@Transactional(readOnly = true)
	@Override
	public SubmittedPayment getLatest(User user) {
		List<SubmittedPayment> topByUser = submittedPaymentRepository.findTopByUser(user, ONE);
		if (topByUser != null && topByUser.size() == 1) {
			return topByUser.get(0);
		}
		return null;
	}
}
