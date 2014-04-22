package mobi.nowtechnologies.server.user.autooptin;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.PromotionProvider;
import mobi.nowtechnologies.server.service.configuration.Configuration;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.user.rules.Rule;
import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;

import java.util.Map;
import java.util.SortedSet;

import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.DeviceType.BLACKBERRY;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.lessThan;
import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.currentTimestamp;


public class AutoOptInConfiguration extends Configuration<
        AutoOptInRuleService.AutoOptInTriggerType,
        Boolean,
        AutoOptInRuleBuilder> {

    private static final UserStatus USER_STATUS_LIMITED = new UserStatus(UserStatus.LIMITED);
    private PromotionProvider promotionProvider;

    @Override
    protected RuleServiceSupport<AutoOptInRuleService.AutoOptInTriggerType> createRuleServiceSupport(Map<AutoOptInRuleService.AutoOptInTriggerType, SortedSet<Rule>> actionRules) {
        return new RuleServiceSupport<AutoOptInRuleService.AutoOptInTriggerType>(actionRules);
    }

    @Override
    protected AutoOptInRuleBuilder createBuilder(AutoOptInRuleService.AutoOptInTriggerType trigger) {
        return new AutoOptInRuleBuilder();
    }

    public void setPromotionProvider(PromotionProvider promotionProvider) {
        this.promotionProvider = promotionProvider;
    }

    @Override
    protected void configure() {

        PromotionProvider.PromotionProxy promotion3G = promotionProvider.getPromotionProxyByPropertyName("o2.promotion.campaign.3g.promoCode", O2_COMMUNITY_REWRITE_URL);
        PromotionProvider.PromotionProxy promotion4G = promotionProvider.getPromotionProxyByPropertyName("o2.promotion.campaign.4g.promoCode", O2_COMMUNITY_REWRITE_URL);

        rule(AutoOptInRuleService.AutoOptInTriggerType.ALL).priority(10).validAsPer(promotion3G).match(
                and(
                        //O2 Community
                        is(userCommunityRewriteUrl(), equalTo(O2_COMMUNITY_REWRITE_URL)),
                        is(userProviderType(), equalTo(O2)),
                        is(userSegment(), equalTo(CONSUMER)),
                        is(userTariff(), equalTo(Tariff._3G)),
                        not(is(oldUser(), nullValue(User.class))),
                        withOldUser(
                                and(
                                        is(userStatus(), equalTo(USER_STATUS_LIMITED)),
                                        and(
                                                not(is(userFreeTrialExpiredMillis(), nullValue(Long.class))),
                                                is(userFreeTrialExpiredMillis(), lessThan(currentTimestamp()))
                                        ),
                                        or(
                                                is(userCurrentPaymentDetails(), nullValue(PaymentDetails.class)),
                                                is(userCurrentPaymentDetailsActivated(), equalTo(false))
                                        )
                                )
                        ),
                        not(is(userDeviceTypeName(), equalTo(BLACKBERRY))),
                        campaignUser("O2reengagement")
                )
        ).result(true);

        rule(AutoOptInRuleService.AutoOptInTriggerType.ALL).priority(11).validAsPer(promotion4G).match(
                and(
                        //O2 Community
                        is(userCommunityRewriteUrl(), equalTo(O2_COMMUNITY_REWRITE_URL)),
                        is(userProviderType(), equalTo(O2)),
                        is(userSegment(), equalTo(CONSUMER)),
                        is(userTariff(), equalTo(Tariff._4G)),
                        not(is(oldUser(), nullValue(User.class))),
                        withOldUser(
                                and(
                                        is(userStatus(), equalTo(USER_STATUS_LIMITED)),
                                        and(
                                                not(is(userFreeTrialExpiredMillis(), nullValue(Long.class))),
                                                is(userFreeTrialExpiredMillis(), lessThan(currentTimestamp()))
                                        ),
                                        or(
                                                is(userCurrentPaymentDetails(), nullValue(PaymentDetails.class)),
                                                is(userCurrentPaymentDetailsActivated(), equalTo(false))
                                        )
                                )
                        ),
                        not(is(userDeviceTypeName(), equalTo(BLACKBERRY))),
                        campaignUser("O2reengagement")
                )
        ).result(true);
    }


}
