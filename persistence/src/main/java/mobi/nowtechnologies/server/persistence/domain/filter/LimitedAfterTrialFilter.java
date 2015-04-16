package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue(value = "LIMITED_AFTER_TRIAL")
public class LimitedAfterTrialFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        return user.getCurrentPaymentDetails() == null && UserStatusType.LIMITED.name().equals(user.getStatus().getName());
    }
}
