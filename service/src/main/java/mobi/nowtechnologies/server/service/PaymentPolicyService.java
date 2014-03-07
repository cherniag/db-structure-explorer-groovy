package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

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

    @Transactional(readOnly = true)
    public List<PaymentPolicyDto> getPaymentPolicyDtos(User user) {
        Community community = user.getUserGroup().getCommunity();
        SegmentType segment = user.getSegment();

        List<PaymentPolicyDto> paymentPolicyDtos;
        if ( user.isVFNZCommunityUser() ) {
            paymentPolicyDtos = getPaymentPolicyWithNullSegment(community, user);
        } else {
            if( isNotFromNetwork(user) ) {
                paymentPolicyDtos = getPaymentPolicyWithOutSegment(community, user);
            } else {
                paymentPolicyDtos = getPaymentPolicy(community, user, segment);
                paymentPolicyDtos = filterPaymentPoliciesForUser(paymentPolicyDtos, user);
            }
        }

        if(isEmpty(paymentPolicyDtos)) {
            return Collections.emptyList();
        }

        return paymentPolicyDtos;
    }

    private boolean isNotFromNetwork(User user) {
        return NON_O2.equals(user.getProvider()) || NON_VF.equals(user.getProvider());
    }

    /**
     * For 3G users we'll only display 3G payment options, for 4G users, we'll display only 4G payment options
     */
    private List<PaymentPolicyDto> filterPaymentPoliciesForUser(List<PaymentPolicyDto> paymentPolicyList, User user) {
        List<PaymentPolicyDto> ret = new ArrayList<PaymentPolicyDto>();

        if ( paymentPolicyList == null || user == null ) {
            return ret;
        }

        if(user.isO2Business()){
            //no filtering required
            ret.addAll(paymentPolicyList);
            return ret;
        }

        for ( PaymentPolicyDto pp : paymentPolicyList ) {
            if ( user.is3G() && pp.isThreeG() ) {
                ret.add( pp );
            } else if ( user.is4G() && pp.isFourG() ) {
                if ( !pp.isVideoAndAudio4GSubscription() || (pp.isVideoAndAudio4GSubscription() && user.isVideoFreeTrialHasBeenActivated()) ) {
                    ret.add( pp );
                }
            }
        }

        return ret;
    }

    private List<PaymentPolicyDto> getPaymentPolicyWithOutSegment(Community community, User user) {
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPoliciesWithOutSegment(community);
        return mergePaymentPolicies(user, paymentPolicies);
    }

    private List<PaymentPolicyDto> getPaymentPolicyWithNullSegment(Community community, User user) {
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPoliciesWithNullSegment(community, user.getProvider());
        return mergePaymentPolicies(user, paymentPolicies);
    }

    private List<PaymentPolicyDto> getPaymentPolicy(Community community, User user, SegmentType segment) {
        if(user.isNonO2Community() && !user.isVFNZCommunityUser()){
            return mergePaymentPolicies(user, paymentPolicyRepository.getPaymentPoliciesWithOutSegment(community));
        }
        if(isNull(segment))
            segment = SegmentType.BUSINESS;
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPolicies(community, segment);
        return mergePaymentPolicies(user, paymentPolicies);
    }

    private List<PaymentPolicyDto> mergePaymentPolicies(User user, List<PaymentPolicy> paymentPolicies) {
        List<PaymentPolicyDto> result = new LinkedList<PaymentPolicyDto>();
        for (PaymentPolicy paymentPolicy : paymentPolicies) {
            Promotion potentialPromotion = user.getPotentialPromotion();
            if (null != potentialPromotion) {
                potentialPromotion = user.getPotentialPromotion();
                List<PromotionPaymentPolicy> promotionPaymentPolicies = potentialPromotion.getPromotionPaymentPolicies();
                boolean inList = false;
                for (PromotionPaymentPolicy promotionPolicy : promotionPaymentPolicies) {
                    if (promotionPolicy.getPaymentPolicies().contains(paymentPolicy)) {
                        result.add(new PaymentPolicyDto(paymentPolicy, promotionPolicy));
                        inList = true;
                    }
                }
                if (!inList) {
                    result.add(getPaymentPolicy(paymentPolicy, null));
                }
            } else {
                result.add(getPaymentPolicy(paymentPolicy, null));
            }
        }
        return result;
    }

    public PaymentPolicyDto getPaymentPolicyDto(Integer paymentPolicyId){
        PaymentPolicy paymentPolicy = getPaymentPolicy(paymentPolicyId);
        return getPaymentPolicy(paymentPolicy, null);
    }
}