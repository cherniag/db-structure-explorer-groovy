package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_apps database table.
 * @author Alexander Kolpakov (akolpakov)
 */
@Entity
@Table(name="tb_apps")
public class App implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i",columnDefinition="smallint(5) unsigned")
	private int i;

	private byte appType;

	@Column(name="jad",columnDefinition="char(255)")
	private String jad;

	@Column(name="jar",columnDefinition="char(40)")
	private String jar;

	@Column(name="model",columnDefinition="char(40)")
	private String model;
	
	private int communityID;

    public App() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public byte getAppType() {
		return this.appType;
	}

	public void setAppType(byte appType) {
		this.appType = appType;
	}

	public String getJad() {
		return this.jad;
	}

	public void setJad(String jad) {
		this.jad = jad;
	}

	public String getJar() {
		return this.jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
