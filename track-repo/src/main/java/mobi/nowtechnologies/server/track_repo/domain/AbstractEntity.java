package mobi.nowtechnologies.server.track_repo.domain;

import javax.persistence.*;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@MappedSuperclass
public abstract class AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "AbstractEntity [id=" + id+"]";
	}
}