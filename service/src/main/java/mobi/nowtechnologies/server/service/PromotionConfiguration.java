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

public class PromotionConfiguration extends Configuration<PromotionService.PromotionTriggerType,PromotionProvider.PromotionProxy, PromotionRuleBuilder> {

    private PromotionProvider promotionProvider;

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
        PromotionProvider.PromotionProxy promotion3G = promotionProvider.getPromotionProxyByPropertyName("o2.promotion.campaign.3g.promoCode", O2_COMMUNITY_REWRITE_URL);
        PromotionProvider.PromotionProxy promotion4G = promotionProvider.getPromotionProxyByPropertyName("o2.promotion.campaign.4g.promoCode", O2_COMMUNITY_REWRITE_URL);

        rule(PromotionService.PromotionTriggerType.AUTO_OPT_IN).priority(10).match(
                and(
                        is(userCommunityRewriteUrl(), equalTo(O2_COMMUNITY_REWRITE_URL)),
                        is(userTariff(), equalTo(Tariff._3G)),
                        not(is(userLastPromoCodeId(), equalTo(promotion3G.getPromoCode().getId()))),
                        campaignUser("O2reengagement")
                )
        ).result(promotion3G);

        rule(PromotionService.PromotionTriggerType.AUTO_OPT_IN).priority(9).match(
                and(
                        is(userCommunityRewriteUrl(), equalTo(O2_COMMUNITY_REWRITE_URL)),
                        is(userTariff(), equalTo(Tariff._4G)),
                        not(is(userLastPromoCodeId(), equalTo(promotion4G.getPromoCode().getId()))),
                        campaignUser("O2reengagement")
                )
        ).result(promotion4G);
    }


    public void setPromotionProvider(PromotionProvider promotionProvider) {
        this.promotionProvider = promotionProvider;
    }
}
