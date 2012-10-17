package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_systemLog database table.
 * 
 */
@Entity
@Table(name="tb_systemLog")
public class SystemLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@Column(name="entry",columnDefinition="text")
    @Lob()
	private String entry;

	private int timestamp;

    public SystemLog() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getEntry() {
		return this.entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

}