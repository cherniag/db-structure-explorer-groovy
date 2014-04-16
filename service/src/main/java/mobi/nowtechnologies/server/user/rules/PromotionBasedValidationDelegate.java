package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.shared.Utils;
import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PromotionBasedValidationDelegate implements ValidationDelegate {

    private final Promotion promotion;

    public PromotionBasedValidationDelegate(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public boolean isValid() {
        return  promotion.getIsActive() &&
                Utils.getEpochSeconds() <= promotion.getEndDate() &&
                Utils.getEpochSeconds() >= promotion.getStartDate();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("promotion", promotion)
                .toString();
    }
}
