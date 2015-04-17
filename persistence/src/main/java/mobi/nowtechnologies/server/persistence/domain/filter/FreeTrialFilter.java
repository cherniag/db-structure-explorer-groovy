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
@DiscriminatorValue(value = "FREE_TRIAL")
public class FreeTrialFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        return UserStatusType.SUBSCRIBED.name().equals(user.getStatus().getName()) && user.getCurrentPaymentDetails() == null;
    }
}
