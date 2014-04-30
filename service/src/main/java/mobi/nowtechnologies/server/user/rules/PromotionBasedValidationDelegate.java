package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.service.PromotionProvider;
import mobi.nowtechnologies.server.shared.Utils;
import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PromotionBasedValidationDelegate implements ValidationDelegate {

    private final PromotionProvider.PromotionProxy promotionProxy;

    public PromotionBasedValidationDelegate(PromotionProvider.PromotionProxy promotion) {
        this.promotionProxy = promotion;
    }

    @Override
    public boolean isValid() {
        Promotion promotion = promotionProxy.getPromotion();
        return isValidImpl(promotion);
    }

    boolean isValidImpl(Promotion promotion) {
        return  promotion.getIsActive() &&
                Utils.getEpochSeconds() <= promotion.getEndDate() &&
                Utils.getEpochSeconds() >= promotion.getStartDate();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("promoCode", promotionProxy.getPromoCodeName())
                .toString();
    }
}
