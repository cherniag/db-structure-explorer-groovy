package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="tb_charts")
@XmlRootElement
public class Chart implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlTransient
	@Id
	@GeneratedValue(strategy = IDENTITY)
    @Column(name="i", columnDefinition="tinyint(4)")
	private Integer i;

	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "genre")
	private Genre genre;
	
	@Column(name="genre", insertable=false, updatable=false)
	private Integer genreId;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

	@Enumerated(STRING)
	private ChartType type;
	
	private byte numTracks;
	
	private byte numBonusTracks;

	private int timestamp;
	
	@OneToMany(mappedBy="chart",fetch = LAZY)
	@LazyCollection(LazyCollectionOption.TRUE)
	private Set<ChartDetail> chartDetails = new HashSet<ChartDetail>();

	@ManyToMany(fetch = LAZY)
	@JoinTable(name="community_charts",
    joinColumns=
        @JoinColumn(name="chart_id", referencedColumnName="i"),
    inverseJoinColumns=
        @JoinColumn(name="community_id", referencedColumnName="id")
    )
	private List<Community> communities = new ArrayList<Community>();
	
    public Chart() {
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public List<Community> getCommunities() {
		return communities;
	}

	public void setCommunities(List<Community> communites) {
		this.communities = communites;
	}

	public Genre getGenre() {
		return this.genre;
	}

	public ChartType getType() {
		return type;
	}

	public void setType(ChartType type) {
		this.type = type;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
		genreId=genre.getI();
	}
	
	public Integer getGenreId() {
		return genreId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getNumTracks() {
		return this.numTracks;
	}

	public void setNumTracks(byte numTracks) {
		this.numTracks = numTracks;
	}
	
	public byte getNumBonusTracks() {
		return numBonusTracks;
	}

	public void setNumBonusTracks(byte numBonusTracks) {
		this.numBonusTracks = numBonusTracks;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public Set<ChartDetail> getChartDetails() {
		return chartDetails;
	}

	public void setChartDetails(Set<ChartDetail> chartDetails) {
		this.chartDetails = chartDetails;
	}

	public String getGenreName() {
		return genre.getName();
	}

    public Chart withI(Integer i) {
        setI(i);
        return this;
    }

    public Chart withGenre(Genre genre) {
        setGenre(genre);
        return this;
    }

    public Chart withName(String name) {
        setName(name);
        return this;
    }

    public Chart withCommunity(Community community) {
        communities.add(community);
        return this;
    }

	public  Chart withChartType(ChartType chartType){
		setType(chartType);
		return this;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("genreId", genreId)
                .append("name", name)
                .append("type", type)
                .append("numTracks", numTracks)
                .append("numBonusTracks", numBonusTracks)
                .append("timestamp", timestamp)
                .toString();
    }
}