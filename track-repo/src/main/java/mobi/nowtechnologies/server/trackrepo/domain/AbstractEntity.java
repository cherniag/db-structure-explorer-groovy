package mobi.nowtechnologies.server.trackrepo.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import static javax.persistence.GenerationType.AUTO;

// @author Alexander Kolpakov (akolpakov)
@MappedSuperclass
public abstract class AbstractEntity {

	@Id
	@GeneratedValue(strategy = AUTO)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", id)
				.toString();
	}
}