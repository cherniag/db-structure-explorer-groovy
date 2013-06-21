package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_artist database table.
 * 
 */
@Entity
@Table(name="tb_artist")
public class Artist extends CNAbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;


	@Column(name="info",columnDefinition="text")
    @Lob()
	private String info;

	@Column(name="name",columnDefinition="char(40)")
	private String name;
	
	@Basic(optional=true)
	private String realName;


    public Artist() {
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

}