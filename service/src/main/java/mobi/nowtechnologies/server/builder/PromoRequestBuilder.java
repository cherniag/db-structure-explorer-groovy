package mobi.nowtechnologies.server.builder;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PromoRequestBuilder {
    private User user;
    private User mobileUser;
    private String otac;
    private boolean isMajorApiVersionNumberLessThan4;
    private boolean isApplyingWithoutEnterPhone;
    private boolean isSubjectToAutoOptIn;

    public static class PromoRequest {
        public final User user;
        public final User mobileUser;
        public final String otac;
        public final boolean isMajorApiVersionNumberLessThan4;
        public final boolean isApplyingWithoutEnterPhone;
        public final boolean isSubjectToAutoOptIn;

        private PromoRequest(User user, User mobileUser, String otac, boolean isMajorApiVersionNumberLessThan4, boolean isApplyingWithoutEnterPhone, boolean isSubjectToAutoOptIn) {
            this.user = user;
            this.mobileUser = mobileUser;
            this.otac = otac;
            this.isMajorApiVersionNumberLessThan4 = isMajorApiVersionNumberLessThan4;
            this.isApplyingWithoutEnterPhone = isApplyingWithoutEnterPhone;
            this.isSubjectToAutoOptIn = isSubjectToAutoOptIn;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("user", user)
                    .append("mobileUser", mobileUser)
                    .append("otac", otac)
                    .append("isMajorApiVersionNumberLessThan4", isMajorApiVersionNumberLessThan4)
                    .append("isApplyingWithoutEnterPhone", isApplyingWithoutEnterPhone)
                    .append("isSubjectToAutoOptIn", isSubjectToAutoOptIn)
                    .toString();
        }
    }

    public PromoRequestBuilder() {
    }

    public PromoRequestBuilder(PromoRequest promoRequest) {
        user = promoRequest.user;
        mobileUser = promoRequest.mobileUser;
        otac = promoRequest.otac;
        isMajorApiVersionNumberLessThan4 = promoRequest.isMajorApiVersionNumberLessThan4;
        isApplyingWithoutEnterPhone = promoRequest.isApplyingWithoutEnterPhone;
        isSubjectToAutoOptIn = promoRequest.isSubjectToAutoOptIn;
    }

    public PromoRequestBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public PromoRequestBuilder setMobileUser(User mobileUser) {
        this.mobileUser = mobileUser;
        return this;
    }

    public PromoRequestBuilder setOtac(String otac) {
        this.otac = otac;
        return this;
    }

    public PromoRequestBuilder setIsMajorApiVersionNumberLessThan4(boolean isMajorApiVersionNumberLessThan4) {
        this.isMajorApiVersionNumberLessThan4 = isMajorApiVersionNumberLessThan4;
        return this;
    }

    public PromoRequestBuilder setIsApplyingWithoutEnterPhone(boolean isApplyingWithoutEnterPhone) {
        this.isApplyingWithoutEnterPhone = isApplyingWithoutEnterPhone;
        return this;
    }

    public PromoRequestBuilder setIsSubjectToAutoOptIn(boolean isSubjectToAutoOptIn) {
        this.isSubjectToAutoOptIn = isSubjectToAutoOptIn;
        return this;
    }

    public PromoRequest createPromoRequest() {
        return new PromoRequest(user, mobileUser, otac, isMajorApiVersionNumberLessThan4, isApplyingWithoutEnterPhone, isSubjectToAutoOptIn);
    }
}