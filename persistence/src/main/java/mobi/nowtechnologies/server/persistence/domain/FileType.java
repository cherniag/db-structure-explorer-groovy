package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_fileTypes database table.
 * 
 */
@Entity
@Table(name="tb_fileTypes")
public class FileType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public FileType() {
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