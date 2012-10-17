package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("premiumUser")
public class PremiumUserPayment extends Payment {

}
