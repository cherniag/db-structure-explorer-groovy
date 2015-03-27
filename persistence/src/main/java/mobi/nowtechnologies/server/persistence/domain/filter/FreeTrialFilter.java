package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue(value = "FREE_TRIAL")
public class FreeTrialFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        return UserStatusDao.getSubscribedUserStatus().equals(user.getStatus()) && user.getCurrentPaymentDetails() == null;
    }
}
