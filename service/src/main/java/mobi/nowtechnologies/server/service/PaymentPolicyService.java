package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
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
	
	public PaymentPolicy getPaymentPolicy(Integer id){
		return paymentPolicyRepository.findOne(id);
	}

	public PaymentPolicy getPaymentPolicy(final int operatorId, String paymentType, int communityId){
		Validate.notNull(paymentType, "The parameter paymentType is null");
        return paymentPolicyDao.getPaymentPolicy(operatorId, paymentType, communityId);
	}
	
	public PaymentPolicyDto getPaymentPolicy(PaymentPolicy paymentPolicy, PromotionPaymentPolicy promotionPaymentPolicy) {
		PaymentPolicyDto dto = null;
		if (null != paymentPolicy) {
			dto = new PaymentPolicyDto(paymentPolicy, promotionPaymentPolicy);
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
    public PaymentPolicy findDefaultO2PsmsPaymentPolicy(User user) {
        return paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(user.getUserGroup().getCommunity(), user.getProvider(), user.getSegment(), user.getContract(), user.getTariff());
    }
}