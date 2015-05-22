package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "ONE_MONTH_PROMO")
public class OneMonthPromoFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        throw new PersistenceException("Not implemented");
    }

}
