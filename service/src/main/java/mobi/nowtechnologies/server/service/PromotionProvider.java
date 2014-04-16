package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static org.apache.commons.lang.Validate.notNull;

public class PromotionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionProvider.class);

    private EntityService entityService;
    private CommunityService communityService;
    private PromotionRepository promotionRepository;
    private CommunityResourceBundleMessageSource messageSource;

    public Promotion getActivePromotionByPropertyName(String propertyName, String communityName){
        String promoCode = messageSource.getMessage(communityName, propertyName, null, null);
        Promotion promotion = getActivePromotion(promoCode, O2_COMMUNITY_REWRITE_URL);
        return promotion;
    }


    private Promotion getActivePromotion(String promotionCode, String communityName) {
        notNull(promotionCode, "The parameter promotionCode is null");
        notNull(communityName, "The parameter communityName is null");
        LOGGER.info("Get active promotion for promo code {}, community {}", promotionCode, communityName);

        Community community = communityService.getCommunityByName(communityName);
        UserGroup userGroup = entityService.findByProperty(UserGroup.class,	UserGroup.Fields.communityId.toString(), community.getId());
        Promotion promotion = promotionRepository.getActivePromoCodePromotion(promotionCode, userGroup, Utils.getEpochSeconds(), ADD_FREE_WEEKS_PROMOTION);

        return promotion;
    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
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
}
