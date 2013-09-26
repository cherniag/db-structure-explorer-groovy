package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType;
import mobi.nowtechnologies.server.persistence.dao.PaymentDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 * @deprecated  As of release 3.5, replaced by {@link mobi.nowtechnologies.server.service.IPaymentService}
 */
@Deprecated
public class PaymentService {
private static final Logger LOGGER = LoggerFactory
		.getLogger(PaymentService.class.getName());
	private EntityService entityService;
	private PaymentDao paymentDao;

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
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
