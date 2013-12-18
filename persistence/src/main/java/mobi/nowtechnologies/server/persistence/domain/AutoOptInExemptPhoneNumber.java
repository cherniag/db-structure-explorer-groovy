package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;

@Entity
@Table(name = "auto_opt_in_exempt_ph_number")
public class AutoOptInExemptPhoneNumber {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

    @Column(name = "phone", insertable = false, updatable = false, unique = true)
	private String userName;

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}
