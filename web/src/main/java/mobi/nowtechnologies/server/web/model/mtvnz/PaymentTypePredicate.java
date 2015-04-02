package mobi.nowtechnologies.server.web.model.mtvnz;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;

class PaymentTypePredicate implements Predicate<PaymentPolicy> {
    private String paymentType;

    public PaymentTypePredicate(String paymentType) {
        this.paymentType = Preconditions.checkNotNull(paymentType);
    }

    @Override
    public boolean apply(PaymentPolicy paymentPolicy) {
        return paymentType.equals(paymentPolicy.getPaymentType());
    }
}
