package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.Utils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue(value = "LAST_TRIAL_DAY")
public class LastTrialDayFilter extends AbstractFilterWithCtiteria {

    private static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    private static final Logger LOGGER = LoggerFactory.getLogger(LastTrialDayFilter.class);

    @Override
    public boolean doFilter(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        boolean filtrate = false;
        final UserStatus userStatus = user.getStatus();
        final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (UserStatusType.SUBSCRIBED.name().equals(userStatus.getName()) && currentPaymentDetails == null) {
            int nextSubPaymentSeconds = user.getNextSubPayment();
            int currentTimeSeconds = Utils.getEpochSeconds();
            if (nextSubPaymentSeconds - currentTimeSeconds < ONE_DAY_SECONDS) {
                filtrate = true;
            }
        }
        LOGGER.debug("Output parameter [{}]", filtrate);
        return filtrate;
    }
}
