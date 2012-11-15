package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * @generated
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "ONE_MONTH_PROMO")
public class OneMonthPromoFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -872622075L;
	
	@Override
	public boolean doFilter(User user) {
		throw new PersistenceException("Not implemented");
	}

}
