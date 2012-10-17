package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_appTypes database table.
 * 
 */
@Entity
@Table(name="tb_appTypes")
public class AppType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="name",columnDefinition="char(20)")
	private String name;

    public AppType() {
    }

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}