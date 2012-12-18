package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;


/**
 * The persistent class for the tb_communities database table.
 * 
 */
@Entity
@Table(name="tb_communities")
public class Community implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i")
	private byte id;

	@Column(name="appVersion", insertable=false,updatable=false)
	private byte appVersionId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="appVersion")
	private AppVersion appVersion;

	private int communityTypeID;

	@Column(name="name",columnDefinition="char(25)")
	private String name;
	
	private String displayName;
	private String assetName;
	
	@Column(name="rewriteURLParameter")
	private String rewriteUrlParameter;

    public Community() {
    }

	public byte getId() {
		return this.id;
	}

	void setId(byte id) {
		this.id = id;
	}

	public AppVersion getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(AppVersion appVersion) {
		this.appVersion = appVersion;
		appVersionId = appVersion.getI();
	}

	public byte getAppVersionId() {
		return appVersionId;
	}

	public int getCommunityTypeID() {
		return this.communityTypeID;
	}

	protected void setCommunityTypeID(int communityTypeID) {
		this.communityTypeID = communityTypeID;
	}

	public String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	protected void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAssetName() {
		return assetName;
	}

	protected void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getRewriteUrlParameter() {
		return rewriteUrlParameter;
	}

	public void setRewriteUrlParameter(String rewriteUrlParameter) {
		this.rewriteUrlParameter = rewriteUrlParameter;
	}
	
	public static Map<String,Community> getMapAsNames() {
		return CommunityDao.getMapAsNames();
	}
	
	public static Map<String, Community> getMapAsUrls() {
		return CommunityDao.getMapAsUrls();
	}

	@Override
	public String toString() {
		return "Community [appVersionId=" + appVersionId + ", assetName=" + assetName + ", communityTypeID=" + communityTypeID + ", displayName="
				+ displayName + ", id=" + id + ", name=" + name + ", rewriteUrlParameter=" + rewriteUrlParameter + "]";
	}

}