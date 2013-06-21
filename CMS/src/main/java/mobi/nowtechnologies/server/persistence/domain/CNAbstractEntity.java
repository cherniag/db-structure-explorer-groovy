package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class CNAbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long i;

	public Long getI() {
		return i;
	}

	public void setI(Long i) {
		this.i = i;
	}

}
