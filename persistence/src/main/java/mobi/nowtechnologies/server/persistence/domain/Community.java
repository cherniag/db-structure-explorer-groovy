package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="tb_communities")
public class Community implements Serializable {
	private static final long serialVersionUID = 1L;

    public static final String HL_COMMUNITY_REWRITE_URL = "hl_uk";
    public static final String O2_COMMUNITY_REWRITE_URL = "o2";
    public static final String VF_NZ_COMMUNITY_REWRITE_URL = "vf_nz";

	@Id
	@GeneratedValue(strategy= IDENTITY)
	private Integer id;

	@Column(name="appVersion", insertable=false, updatable=false)
	private byte appVersionId;
	
	@ManyToOne(fetch= EAGER)
	@JoinColumn(name="appVersion")
	private AppVersion appVersion;

	private int communityTypeID;

	@Column(name="name", columnDefinition="char(25)")
	private String name;
	
	private String displayName;
	private String assetName;
	
	@Column(name="rewriteURLParameter")
	private String rewriteUrlParameter;
	
	@ManyToMany(mappedBy = "communities")
	private List<Chart> charts;

    private boolean live;

	public Integer getId() {
		return this.id;
	}

    public void setId(Integer id) {
		this.id = id;
	}

	public AppVersion getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(AppVersion appVersion) {
		this.appVersion = appVersion;
		appVersionId = appVersion.getI();
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

	public void setName(String name) {
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

    public List<Chart> getCharts() {
        return charts;
    }

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public Community withRewriteUrl(String url){
		setRewriteUrlParameter(url);
		return  this;
	}

	public Community withName(String name){
		setName(name);
		return  this;
	}

	public Community withLive(boolean live){
		setLive(live);
		return  this;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this)
				.append("id", id)
				.append("appVersionId", appVersionId)
                .append("communityTypeID", communityTypeID)
                .append("name", name)
                .append("displayName", displayName)
                .append("assetName", assetName)
                .append("rewriteUrlParameter", rewriteUrlParameter)
                .append("live", live)
                .toString();
    }
}