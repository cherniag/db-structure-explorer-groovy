package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_drmTypes database table.
 * 
 */
@Entity
@Table(name="tb_drmTypes")
public class DrmType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public DrmType() {
    }

	public byte getI() {
		return this.i;
	}

	public String getName() {
		return this.name;
	}

}