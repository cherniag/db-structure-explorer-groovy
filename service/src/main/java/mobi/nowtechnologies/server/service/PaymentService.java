package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Deprecated
public class PaymentService {
private static final Logger LOGGER = LoggerFactory
		.getLogger(PaymentService.class.getName());
	private PaymentDao paymentDao;

	public void setPaymentDao(PaymentDao paymentDao) {
		this.paymentDao = paymentDao;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<PaymentHistoryItemDto> findByUserIdOrderedByLogTimestampDesc(int userId, Integer maxResults) {
		LOGGER.debug("input parameters userId, maxResults: [{}], [{}]", userId, maxResults);
		
		List<AbstractPayment> abstractPayments = paymentDao.findByUserIdOrderedByTimestampDesc(userId, maxResults);
		
		List<PaymentHistoryItemDto> paymentHistoryItemDtos = AbstractPayment.toPaymentHistoryItemDto(abstractPayments);
		
		LOGGER.debug("Output parameter paymentHistoryItemDtos=[{}]", paymentHistoryItemDtos);
		return paymentHistoryItemDtos;
	}
}
