package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/5/2015
 */
@Entity
@DiscriminatorValue(PaymentDetails.MTVNZ_PSMS_TYPE)
public class MTVNZPSMSPaymentDetails extends PSMSPaymentDetails {

    @Override
    public String getPaymentType() {
        return PaymentDetails.MTVNZ_PSMS_TYPE;
    }

}
