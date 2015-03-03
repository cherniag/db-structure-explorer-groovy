package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.dao.PaymentPolicyDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentPolicyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentPolicyService.class);

    private PaymentPolicyDao paymentPolicyDao;

    private PaymentPolicyRepository paymentPolicyRepository;

    public void setPaymentPolicyDao(PaymentPolicyDao paymentPolicyDao) {
        this.paymentPolicyDao = paymentPolicyDao;
    }

    public void setPaymentPolicyRepository(PaymentPolicyRepository paymentPolicyRepository) {
        this.paymentPolicyRepository = paymentPolicyRepository;
    }

    @Transactional(readOnly = true)
    public List<String> findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(Community community) {
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

    public PaymentPolicy getPaymentPolicy(Integer id) {
        return paymentPolicyRepository.findOne(id);
    }

    public PaymentPolicy getPaymentPolicy(final int operatorId, String paymentType, int communityId) {
        Validate.notNull(paymentType, "The parameter paymentType is null");
        return paymentPolicyDao.getPaymentPolicy(operatorId, paymentType, communityId);
    }

    public PaymentPolicyDto getPaymentPolicy(PaymentPolicy paymentPolicy, PromotionPaymentPolicy promotionPaymentPolicy) {
        PaymentPolicyDto dto = null;
        if (isNotNull(paymentPolicy)) {
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
        Contract contract = user.getContract();
        if (isNull(contract)) {
            contract = Contract.PAYM;
        }
        Community community = user.getUserGroup().getCommunity();
        return paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(community, user.getProvider(), user.getSegment(), contract, user.getTariff());
    }

    @Transactional(readOnly = true)
    public List<PaymentPolicyDto> getPaymentPolicyDtos(User user) {
        List<PaymentPolicyDto> paymentPolicyDtos;
        if (user.isO2User() || (user.isO2CommunityUser() && isNull(user.getProvider()))) {
            paymentPolicyDtos = getPaymentPolicyForO2UserOrO2CommunityUserWithNullProvider(user);
        }
        else {
            paymentPolicyDtos = getMergedPaymentPolicies(user, null, null);
        }

        return unmodifiableList(paymentPolicyDtos);
    }

    private List<PaymentPolicyDto> getMergedPaymentPolicies(User user, SegmentType defaultSegment, ProviderType defaultProvider) {
        SegmentType segment = getSegmentType(user, defaultSegment);
        ProviderType provider = getProviderType(user, defaultProvider);
        List<MediaType> mediaTypes = getMediaTypes(user);
        Community community = user.getUserGroup().getCommunity();
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.getPaymentPolicies(community, provider, segment, user.getContract(), user.getTariff(), mediaTypes);

        sort(paymentPolicies, new Comparator<PaymentPolicy>() {
            @Override
            public int compare(PaymentPolicy p1, PaymentPolicy p2) {
                int result = p2.getPeriod().getDurationUnit().compareTo(p1.getPeriod().getDurationUnit());
                if (result == 0) {
                    return new Integer(p2.getPeriod().getDuration()).compareTo(p1.getPeriod().getDuration());
                }
                return result;
            }
        });
        return mergePaymentPolicies(user, paymentPolicies);
    }

    private ProviderType getProviderType(User user, ProviderType defaultProvider) {
        ProviderType provider = user.getProvider();
        if (isNotNull(defaultProvider)) {
            provider = defaultProvider;
        }
        return provider;
    }

    private SegmentType getSegmentType(User user, SegmentType defaultSegment) {
        SegmentType segment = user.getSegment();
        if (isNotNull(defaultSegment)) {
            segment = defaultSegment;
        }
        return segment;
    }

    private List<MediaType> getMediaTypes(User user) {
        if (user.isVideoFreeTrialHasBeenActivated()) {
            return Arrays.asList(AUDIO, VIDEO_AND_AUDIO);
        }
        else {
            return Arrays.asList(AUDIO);
        }
    }

    private List<PaymentPolicyDto> getPaymentPolicyForO2UserOrO2CommunityUserWithNullProvider(User user) {
        SegmentType segment = user.getSegment();
        if (isNull(segment)) {
            segment = BUSINESS;
        }
        ProviderType provider = user.getProvider();
        if (isNull(provider)) {
            provider = O2;
        }
        return getMergedPaymentPolicies(user, segment, provider);
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
            }
            else {
                result.add(getPaymentPolicy(paymentPolicy, null));
            }
        }
        return result;
    }

    public PaymentPolicyDto getPaymentPolicyDto(Integer paymentPolicyId) {
        PaymentPolicy paymentPolicy = getPaymentPolicy(paymentPolicyId);
        return getPaymentPolicy(paymentPolicy, null);
    }

    @Transactional(readOnly = true)
    public PaymentPolicy getPaymentPolicy(Community community, ProviderType providerType, String paymentType) {
        return paymentPolicyRepository.getPaymentPolicy(community, providerType, paymentType);
    }

}