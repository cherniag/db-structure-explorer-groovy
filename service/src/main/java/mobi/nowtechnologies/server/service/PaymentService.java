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

import static mobi.nowtechnologies.server.shared.AppConstants.NOT_AVAILABLE;
import static mobi.nowtechnologies.server.shared.AppConstants.STATUS_PENDING;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

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

	public List<AccountLog> getPayments(int userId) {
		return entityService.findListByProperty(
				AccountLog.class, AccountLog.Fields.userId.toString(), userId);
	}
	
	public Payment createPendingPayment(int userId, String email, String community, long deferredPaymentId, String paymentType) {
		Payment payment;
		if (paymentType.equals(PaymentType.CREDIT_CARD)){
			payment = new CreditCardPayment();	
		}else if(paymentType.equals(PaymentType.PREMIUM_USER)){
			payment = new PremiumUserPayment();	
		}else if(paymentType.equals(PaymentType.PAY_PAL)){
			payment = new PayPalPayment();	
		}else throw new ServiceException("Unknow payment type: ["+paymentType+"]");
		if (email == null)
			throw new ServiceException("The parameter email is null");
		if (community == null)
			throw new ServiceException("The parameter community is null");
		
		payment.setExternalTxCode(NOT_AVAILABLE);
		payment.setExternalSecurityKey(NOT_AVAILABLE);
		payment.setInternalTxCode(NOT_AVAILABLE);
		payment.setExternalAuthCode(NOT_AVAILABLE);
		payment.setStatus(STATUS_PENDING);
		payment.setStatusDetail(STATUS_PENDING);
		payment.setTimestamp(getEpochSeconds());
		payment.setUserUID(userId);
		String description = MessageFormat.format(
				"Pending payment for user with email [{0}] and community [{1}]", email,community);
		payment.setDescription(
				description.length() > 100 ? description.substring(0, 100) : description);
		payment.setRelatedPayment(deferredPaymentId);
		payment.setTxType(0);
		payment.setAmount(0);
		payment.setSubweeks((byte)0);
		return payment;
	}
	
	public boolean isUserAlreadyPaidSuccessfully(int userID) {
		return paymentDao.isUserAlreadyPaidSuccessfully(userID);
	}
	
	public PayPalPayment getLastDeferedPayPalPayment(int userID) {
		return paymentDao.getLastDeferedPayPalPayment(userID);
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
