package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("creditCard")
public class CreditCardPayment extends Payment {

}
