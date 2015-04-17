package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.Validate.notNull;

public class PromotionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionProvider.class);

    private CommunityService communityService;
    private PromotionRepository promotionRepository;
    private CommunityResourceBundleMessageSource messageSource;
    private UserGroupRepository userGroupRepository;

    public final PromotionProxy getPromotionProxyByPropertyName(String propertyName, String communityName) {
        String promotionCode = messageSource.getMessage(communityName, propertyName, null, null);
        notNull(promotionCode, "The parameter promotionCode is null");
        notNull(communityName, "The parameter communityName is null");
        LOGGER.info("Get active promotion for promo code {}, community {}", promotionCode, communityName);

        Community community = communityService.getCommunityByName(communityName);
        UserGroup userGroup = userGroupRepository.findByCommunity(community);
        PromotionProxy answer = new PromotionProxy(promotionRepository, promotionCode, userGroup);
        notNull(answer.getPromotion(), "Promotion with promo code = " + promotionCode + " should exist");
        return answer;
    }

    public void setUserGroupRepository(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setPromotionRepository(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static class PromotionProxy {

        private final PromotionRepository repository;
        private final String promoCode;
        private final UserGroup userGroup;

        public PromotionProxy(PromotionRepository repository, String promoCode, UserGroup userGroup) {
            this.repository = repository;
            this.promoCode = promoCode;
            this.userGroup = userGroup;
        }


        public Promotion getPromotion() {
            return repository.findPromotionByPromoCode(promoCode, userGroup, ADD_FREE_WEEKS_PROMOTION);
        }

        public PromoCode getPromoCode() {
            return getPromotion().getPromoCode();
        }

        public String getPromoCodeName() {
            return promoCode;
        }
    }


}
