package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;

@Entity
@Table(name = "auto_opt_in_exempt_ph_number")
public class AutoOptInExemptPhoneNumber {
    @Id
    @Column(name = "phone", insertable = false, updatable = false)
	private String userName;

    public String getUserName() {
        return userName;
    }
}
