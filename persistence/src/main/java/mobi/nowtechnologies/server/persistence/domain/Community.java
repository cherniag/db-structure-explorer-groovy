package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="tb_communities")
public class Community implements Serializable {
	private static final long serialVersionUID = 1L;

    public static final String HL_COMMUNITY_REWRITE_URL = "hl_uk";
    public static final String O2_COMMUNITY_REWRITE_URL = "o2";
    public static final String VF_NZ_COMMUNITY_REWRITE_URL = "vf_nz";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

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
	
	@ManyToMany(mappedBy = "communities", fetch=FetchType.LAZY)
	private List<Chart> charts;

    public Community() {
    }

    public Community withId(Integer id) {
        setId(id);
        return  this;
    }

    public Community withRewriteUrl(String url){
        setRewriteUrlParameter(url);
        return  this;
    }

    public Community withName(String name){
        setName(name);
        return  this;
    }

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
	
	public static Map<String,Community> getMapAsNames() {
		return CommunityDao.getMapAsNames();
	}

    public boolean isO2Community(){
        return O2_COMMUNITY_REWRITE_URL.equals(rewriteUrlParameter);
    }

    public boolean isVFNZCommunity(){
        return VF_NZ_COMMUNITY_REWRITE_URL.equals(rewriteUrlParameter);
    }

    public boolean isHLZCommunity(){
        return HL_COMMUNITY_REWRITE_URL.equals(rewriteUrlParameter);
    }

    public List<Chart> getCharts() {
        return charts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("appVersionId", appVersionId)
                .append("id", id)
                .append("communityTypeID", communityTypeID)
                .append("name", name)
                .append("displayName", displayName)
                .append("assetName", assetName)
                .append("rewriteUrlParameter", rewriteUrlParameter)
                .toString();
    }
}