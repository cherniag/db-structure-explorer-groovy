package mobi.nowtechnologies.server.persistence.domain.filter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Entity
@DiscriminatorValue("NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS")
public class NotActivePaymentDetailsOrNoPaymentDetailsFilter extends AbstractFilterWithCtiteria{
	
	private static final long serialVersionUID = 2569915338502914248L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NotActivePaymentDetailsOrNoPaymentDetailsFilter.class);

	@Override
	public boolean doFilter(User user) {
		LOGGER.debug("input parameters user: [{}], [{}]", user);
		
		boolean filtrate = false;
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		if (currentPaymentDetails == null || !currentPaymentDetails.isActivated())
			filtrate = true;
	
		LOGGER.debug("Output parameter [{}]", filtrate);
		return filtrate;
	}

}