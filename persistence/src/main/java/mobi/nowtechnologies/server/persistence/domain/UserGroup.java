package mobi.nowtechnologies.server.persistence.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;


@Entity
@Table(name = "tb_userGroups")
public class UserGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chart", insertable = false, updatable = false, columnDefinition = "tinyint(4)")
    private Integer chartId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chart", columnDefinition = "tinyint(4)")
    private Chart chart;
    @Column(name = "community", insertable = false, updatable = false)
    private Integer communityId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "community")
    private Community community;
    @Column(name = "name", columnDefinition = "char(25)")
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
        chartId = chart.getI();
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

    public UserGroup withCommunity(Community community) {
        setCommunity(community);
        return this;
    }

    public UserGroup withId(Integer id) {
        setId(id);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("chartId", chartId).append("communityId", communityId).append("name", name).toString();
    }
}