package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_drmPolicy database table.
 * 
 */
@Entity
@Table(name="tb_drmPolicy")
public class DrmPolicy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="community", insertable=false,updatable=false)
	private byte communityId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="community")
	private Community community;

	@Column(name="drmType", insertable=false,updatable=false)
	private byte drmTypeId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="drmType")
	private DrmType drmType;

	private byte drmValue;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

    public DrmPolicy() {
    }

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
		communityId = community.getId();
	}

	public byte getCommunityId() {
		return communityId;
	}

	public DrmType getDrmType() {
		return drmType;
	}

	public void setDrmType(DrmType drmType) {
		this.drmType = drmType;
		drmTypeId=drmType.getI();
	}

	public byte getDrmTypeId() {
		return drmTypeId;
	}

	public byte getDrmValue() {
		return this.drmValue;
	}

	public void setDrmValue(byte drmValue) {
		this.drmValue = drmValue;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DrmPolicy [communityId=" + communityId + ", drmTypeId=" + drmTypeId + ", drmValue=" + drmValue + ", i=" + i + ", name=" + name + "]";
	}

}