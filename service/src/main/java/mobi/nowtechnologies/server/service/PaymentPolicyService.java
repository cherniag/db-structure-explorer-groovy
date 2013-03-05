package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PaymentPolicyService {
	private static final Logger LOGGER = 
		LoggerFactory.getLogger(PaymentPolicyService.class);
	
	private PaymentPolicyDao paymentPolicyDao;
	
	private PaymentPolicyRepository paymentPolicyRepository;
	
	public void setPaymentPolicyDao(PaymentPolicyDao paymentPolicyDao) {
		this.paymentPolicyDao = paymentPolicyDao;
	}
	
	public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
		this.paymentPolicyRepository = paymentPolicyRepository;
	}
	
	@Transactional(readOnly = true)
	public List<String> findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(Community community){
		LOGGER.debug("input parameters community: [{}]", community);
		List<String> appStoreProductIds = paymentPolicyRepository.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(community);
		LOGGER.debug("Output parameter appStoreProductIds=[{}]", appStoreProductIds);
		return appStoreProductIds;
	}
	
	@Transactional(readOnly = true)
	public PaymentPolicy findByCommunityAndAppStoreProductId(Community community, String appStoreProductId) {
		LOGGER.debug("input parameters community, appStoreProductId: [{}], [{}]", community, appStoreProductId);
		PaymentPolicy paymentPolicy = paymentPolicyRepository.findByCommunityAndAppStoreProductId(community, appStoreProductId);
		LOGGER.debug("Output parameter paymentPolicies=[{}]", paymentPolicy);
		return paymentPolicy;
	}

	public PaymentPolicy getPaymentPolicy(final int operatorId, String paymentType, byte communityId){
		if (paymentType == null)
			throw new ServiceException("The parameter paymentType is null");
		
		LOGGER.debug("Input params: operatorId = [{}], paymentType = [{}], communityName = [{}] ", new Object[]{operatorId,paymentType, communityId});
		
		//PaymentSystem paymentSystem = PaymentSystem.getPaymentSystem(paymentType);
		
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(operatorId, paymentType, communityId);
		
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

	@Transactional(readOnly = true)
	public List<PaymentPolicy> getPaymentPoliciesWithouSelectedPaymentTypeGroupdeByPaymentType(Community community, String paymentType) {
		LOGGER.debug("input parameters community, paymentType: [{}], [{}]", community, paymentType);
		
		List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPoliciesWithoutSelectedPaymentType(community, paymentType);
		
		LOGGER.debug("Output parameter paymentPolicies=[{}]", paymentPolicies);
		return paymentPolicies;
	}
}