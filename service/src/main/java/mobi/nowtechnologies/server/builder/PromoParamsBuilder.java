package mobi.nowtechnologies.server.builder;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PromoParamsBuilder {

    private User user;
    private Promotion promotion;
    private int freeTrialStartedTimestampSeconds;

    public PromoParamsBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public PromoParamsBuilder setPromotion(Promotion promotion) {
        this.promotion = promotion;
        return this;
    }

    public PromoParamsBuilder setFreeTrialStartedTimestampSeconds(int freeTrialStartedTimestampSeconds) {
        this.freeTrialStartedTimestampSeconds = freeTrialStartedTimestampSeconds;
        return this;
    }

    public PromoParams createPromoParams() {
        return new PromoParams(user, promotion, freeTrialStartedTimestampSeconds);
    }

    public static class PromoParams {

        public User user;
        public Promotion promotion;
        public int freeTrialStartedTimestampSeconds;

        private PromoParams(User user, Promotion promotion, int freeTrialStartedTimestampSeconds) {
            this.user = user;
            this.promotion = promotion;
            this.freeTrialStartedTimestampSeconds = freeTrialStartedTimestampSeconds;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("user", user).append("promotion", promotion).append("freeTrialStartedTimestampSeconds", freeTrialStartedTimestampSeconds).toString();
        }
    }
}