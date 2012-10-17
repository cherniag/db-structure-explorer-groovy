package mobi.nowtechnologies.server.service;

import java.util.List;

import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PaymentPolicyService {
	private static final Logger LOGGER = 
		LoggerFactory.getLogger(PaymentPolicyService.class);
	
	private PaymentPolicyDao paymentPolicyDao;
	
	public void setPaymentPolicyDao(PaymentPolicyDao paymentPolicyDao) {
		this.paymentPolicyDao = paymentPolicyDao;
	}
	
	public List<PaymentPolicy> getPaymentPoliciesGroupdeByPaymentType(String communityName) {
		if (communityName == null)
			throw new ServiceException(
					"The parameter communityName is null");
		
		return paymentPolicyDao.getPaymentPoliciesGroupdeByPaymentType(communityName);
	}
	
	public PaymentPolicy getPaymentPolicy(final int operatorId, String paymentType, byte communityId){
		if (paymentType == null)
			throw new ServiceException("The parameter paymentType is null");
		
		LOGGER.debug("Input params: operatorId = [{}], paymentType = [{}], communityName = [{}] ", new Object[]{operatorId,paymentType, communityId});
		
		//PaymentSystem paymentSystem = PaymentSystem.getPaymentSystem(paymentType);
		
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(operatorId,
				paymentType, communityId);
		
		LOGGER.debug("Output param: [{}]", paymentPolicy);
		return paymentPolicy;
	}
	
	public PaymentPolicyDto getPaymentPolicy(PaymentPolicy paymentPolicy, PromotionPaymentPolicy promotionPaymentPolicy) {
		PaymentPolicyDto dto = null;
		if (null != paymentPolicy) {
			dto = new PaymentPolicyDto();
			dto.setId(paymentPolicy.getId());
			dto.setOldSubcost(paymentPolicy.getSubcost());
			dto.setOldSubweeks(Integer.valueOf(paymentPolicy.getSubweeks()));
			dto.setSubcost(paymentPolicy.getSubcost());
			dto.setSubweeks(Integer.valueOf(paymentPolicy.getSubweeks()));
			if (null!=paymentPolicy.getOperator()) {
				dto.setOperator(paymentPolicy.getOperator().getId());
				dto.setOperatorName(paymentPolicy.getOperator().getName());
			}
			dto.setPaymentType(paymentPolicy.getPaymentType());
			dto.setShortCode(paymentPolicy.getShortCode());
			dto.setCurrencyISO(paymentPolicy.getCurrencyISO());
			
			if (null != promotionPaymentPolicy) {
				dto.setSubcost(promotionPaymentPolicy.getSubcost());
				dto.setSubweeks(promotionPaymentPolicy.getSubweeks());
			}
		}
		return dto;
	}
	
	public PaymentPolicyDto getPaymentPolicy(PaymentDetails paymentDetails) {
		if (null != paymentDetails) {
			return getPaymentPolicy(paymentDetails.getPaymentPolicy(), paymentDetails.getPromotionPaymentPolicy());
		}
		return null;
	}

	public List<OfferPaymentPolicyDto> getOfferPaymentPolicyDto(String communityURL) {
		LOGGER.debug("input parameters communityURL: [{}]", communityURL);
		
		List<PaymentPolicy> paymentPolicies = paymentPolicyDao.getPaymentPolicies(communityURL, true);
		List<OfferPaymentPolicyDto> offerPaymentPolicyDtos = PaymentPolicy.toOfferPaymentPolicyDtos(paymentPolicies);
		
		LOGGER.debug("Output parameter [{}]", offerPaymentPolicyDtos);
		return offerPaymentPolicyDtos;
	}
}