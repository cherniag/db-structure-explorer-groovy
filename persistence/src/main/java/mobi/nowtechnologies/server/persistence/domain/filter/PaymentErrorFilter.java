package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue(value = "PAYMENT_ERROR")
public class PaymentErrorFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails != null && currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.ERROR)) {
            return true;
        }
        return false;
    }

}
