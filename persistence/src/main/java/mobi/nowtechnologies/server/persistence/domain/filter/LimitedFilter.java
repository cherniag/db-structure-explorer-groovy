package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import java.io.Serializable;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue(value = "LIMITED")
public class LimitedFilter extends AbstractFilterWithCtiteria implements Serializable {
    @Override
    public boolean doFilter(User user) {
        return UserStatusType.LIMITED.name().equals(user.getStatus().getName());
    }
}
