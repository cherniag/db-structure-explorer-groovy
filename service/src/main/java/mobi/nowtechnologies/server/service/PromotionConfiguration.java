package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.SubscriptionCampaignRepository;
import mobi.nowtechnologies.server.service.configuration.Configuration;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import mobi.nowtechnologies.server.user.criteria.IsInCampaignTableUserMatcher;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;

import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static org.apache.commons.lang.Validate.notNull;

public class PromotionConfiguration extends Configuration<PromotionService.PromotionTriggerType,Promotion, PromotionRuleBuilder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionConfiguration.class);
    private EntityService entityService;
    private CommunityService communityService;
    private PromotionRepository promotionRepository;
    private CommunityResourceBundleMessageSource messageSource;
    private SubscriptionCampaignRepository subscriptionCampaignRepository;

    @Override
    protected RuleServiceSupport<PromotionService.PromotionTriggerType> createRuleServiceSupport(Map<PromotionService.PromotionTriggerType, SortedSet<Rule>> actionRules) {
        return  new RuleServiceSupport<PromotionService.PromotionTriggerType>(actionRules);
    }

    @Override
    protected PromotionRuleBuilder createBuilder(PromotionService.PromotionTriggerType trigger) {
        return new PromotionRuleBuilder();
    }

    @Override
    protected void configure() {
        String campaign3GPromoCode = messageSource.getMessage(O2_COMMUNITY_REWRITE_URL, "o2.promotion.campaign.3g.promoCode", null, null);
        String campaign4GPromoCode = messageSource.getMessage(O2_COMMUNITY_REWRITE_URL, "o2.promotion.campaign.4g.promoCode", null, null);

        Promotion promotion3G = getActivePromotion(campaign3GPromoCode, O2_COMMUNITY_REWRITE_URL);
        Promotion promotion4G = getActivePromotion(campaign4GPromoCode, O2_COMMUNITY_REWRITE_URL);

        rule(PromotionService.PromotionTriggerType.AUTO_OPT_IN).priority(10).match(
                and(
                        is(userCommunityRewriteUrl(), equalTo(O2_COMMUNITY_REWRITE_URL)),
                        is(userTariff(), equalTo(Tariff._3G)),
                        not(is(userLastPromoCodeId(), equalTo(promotion3G.getPromoCode().getId()))),
                        new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId")
                )
        ).result(promotion3G);

        rule(PromotionService.PromotionTriggerType.AUTO_OPT_IN).priority(9).match(
                and(
                        is(userCommunityRewriteUrl(), equalTo(O2_COMMUNITY_REWRITE_URL)),
                        is(userTariff(), equalTo(Tariff._4G)),
                        not(is(userLastPromoCodeId(), equalTo(promotion4G.getPromoCode().getId()))),
                        new IsInCampaignTableUserMatcher(subscriptionCampaignRepository, "campaignId")
                )
        ).result(promotion4G);
    }

   public Promotion getActivePromotion(String promotionCode, String communityName) {
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

    public void setSubscriptionCampaignRepository(SubscriptionCampaignRepository subscriptionCampaignRepository) {
        this.subscriptionCampaignRepository = subscriptionCampaignRepository;
    }
}
