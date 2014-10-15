package mobi.nowtechnologies.server.persistence.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="tb_userGroups")
public class UserGroup implements Serializable {
	private static final long serialVersionUID = 1L;

    public static enum Fields {
		i,chartId,communityId,drmPolicyId,name
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="chart", insertable=false,updatable=false, columnDefinition = "tinyint(4)")
	private Integer chartId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="chart", columnDefinition = "tinyint(4)")
	private Chart chart;

	@Column(name="community", insertable=false,updatable=false)
	private Integer communityId;
	
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

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getCommunityId() {
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

    public UserGroup withId(Integer id) {
        setId(id);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("chartId", chartId)
                .append("communityId", communityId)
                .append("drmPolicyId", drmPolicyId)
                .append("name", name)
                .toString();
    }
}