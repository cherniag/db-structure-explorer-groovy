package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_genres database table.
 * 
 */
@Entity
@Table(name="tb_genres")
public class Genre implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields{
		i,name;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer i;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public Genre() {
    }

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}