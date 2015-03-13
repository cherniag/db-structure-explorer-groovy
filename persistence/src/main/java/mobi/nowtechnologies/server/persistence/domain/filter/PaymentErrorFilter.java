package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @generated
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "PAYMENT_ERROR")
public class PaymentErrorFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentErrorFilter.class);

    /**
     * @generated
     */
    private static final long serialVersionUID = -2061598718L;

    /**
     * @generated
     */
    public PaymentErrorFilter() {
    }

    @Override
    public boolean doFilter(User user) {
        LOGGER.debug("input parameters user: [{}]", user);
        boolean filtrate = false;
        final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails != null && currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.ERROR)) {
            filtrate = true;
        }
        LOGGER.debug("Output parameter [{}]", filtrate);
        return filtrate;
    }

}
