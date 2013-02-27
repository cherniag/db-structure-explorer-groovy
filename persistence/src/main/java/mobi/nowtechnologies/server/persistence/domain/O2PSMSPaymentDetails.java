package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Entity
@DiscriminatorValue(PaymentDetails.O2_PSMS_TYPE)
public class O2PSMSPaymentDetails extends PaymentDetails {

	@Override
	public String getPaymentType() {
		return PaymentDetails.O2_PSMS_TYPE;
	}

}
