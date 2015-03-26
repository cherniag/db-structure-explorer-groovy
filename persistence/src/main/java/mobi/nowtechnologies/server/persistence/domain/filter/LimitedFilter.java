package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;

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
        return UserStatusDao.getLimitedUserStatus().equals(user.getStatus());
    }
}
