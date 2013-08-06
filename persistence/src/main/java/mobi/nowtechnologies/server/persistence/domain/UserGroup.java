package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the tb_userGroups database table.
 * 
 */
@Entity
@Table(name="tb_userGroups")
public class UserGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields {
		i,chartId,communityId,drmPolicyId,name,newsId
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="chart", insertable=false,updatable=false, columnDefinition = "tinyint(4)")
	private Integer chartId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="chart", columnDefinition = "tinyint(4)")
	private Chart chart;

	@Column(name="community", insertable=false,updatable=false)
	private byte communityId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="community")
	private Community community;

	@Column(name="drmPolicy", insertable=false,updatable=false)
	private byte drmPolicyId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="drmPolicy")
	private DrmPolicy drmPolicy;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

	@Column(name="news", insertable=false,updatable=false, columnDefinition="tinyint(4)")
	private Integer newsId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="news")
	private News news;

    public UserGroup() {
    }

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	public Integer getChartId() {
		return this.chartId;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
		chartId=chart.getI();
	}

	public byte getCommunityId() {
		return communityId;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
		communityId = community.getId();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
		newsId = new Integer(news.getI());
	}

	public byte getNewsId() {
		return newsId.byteValue();
	}

	public DrmPolicy getDrmPolicy() {
		return drmPolicy;
	}

	public void setDrmPolicy(DrmPolicy drmPolicy) {
		this.drmPolicy = drmPolicy;
		drmPolicyId = drmPolicy.getI();
	}

	public byte getDrmPolicyId() {
		return drmPolicyId;
	}

    public UserGroup withCommunity(Community community){
        setCommunity(community);
        return this;
    }

	@Override
	public String toString() {
		return "UserGroup [chartId=" + chartId + ", communityId=" + communityId + ", drmPolicyId=" + drmPolicyId + ", i=" + i + ", name=" + name
				+ ", newsId=" + newsId + "]";
	}
	
}