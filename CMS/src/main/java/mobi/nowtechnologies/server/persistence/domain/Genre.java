package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_genres database table.
 * 
 */
@Entity
@Table(name="tb_genres")
public class Genre extends CNAbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public Genre() {
    }


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}