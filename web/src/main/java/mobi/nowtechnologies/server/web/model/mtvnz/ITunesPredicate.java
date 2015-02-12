package mobi.nowtechnologies.server.web.model.mtvnz;

import com.google.common.base.Predicate;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;

class ITunesPredicate implements Predicate<PaymentPolicy> {
    @Override
    public boolean apply(PaymentPolicy paymentPolicy) {
        return PaymentDetails.ITUNES_SUBSCRIPTION.equals(paymentPolicy.getPaymentType());
    }
}
