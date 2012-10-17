package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_artist database table.
 * 
 */
@Entity
@Table(name="tb_artist")
public class Artist implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer i;

	@Column(name="info",columnDefinition="text")
    @Lob()
	private String info;

	@Column(name="name",columnDefinition="char(40)")
	private String name;
	
	@Column(name="realName",columnDefinition="char(255)")
	private String realName;

    public Artist() {
    }

	public Integer getI() {
		return this.i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Override
	public String toString() {
		return "Artist [i=" + i + ", info=" + info + ", name=" + name +", realName=" + realName + "]";
	}

}