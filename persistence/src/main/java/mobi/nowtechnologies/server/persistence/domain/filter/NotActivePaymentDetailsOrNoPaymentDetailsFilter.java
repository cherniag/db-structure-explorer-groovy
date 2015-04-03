package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue("NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS")
public class NotActivePaymentDetailsOrNoPaymentDetailsFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails == null || !currentPaymentDetails.isActivated()) {
            return true;
        }

        return false;
    }

}
