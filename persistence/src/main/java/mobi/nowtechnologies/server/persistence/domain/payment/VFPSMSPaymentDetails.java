package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(PaymentDetails.VF_PSMS_TYPE)
public class VFPSMSPaymentDetails extends PSMSPaymentDetails {

    @Override
    public String getPaymentType() {
        return PaymentDetails.VF_PSMS_TYPE;
    }

    @Override
    public String toString() {
        return "VFPSMSPaymentDetails [" + super.toString() + "]";
    }

}
