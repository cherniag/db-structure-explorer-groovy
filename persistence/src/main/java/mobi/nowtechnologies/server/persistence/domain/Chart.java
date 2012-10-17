package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * The persistent class for the tb_charts database table.
 * 
 */

@Entity
@Table(name="tb_charts")
@XmlRootElement
public class Chart implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlTransient
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Byte i;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "community")
	private Community community;

	@Column(name="community", insertable=false, updatable=false)
	private byte communityId;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "genre")
	private Genre genre;
	
	@Column(name="genre", insertable=false, updatable=false)
	private Integer genreId;

	@Column(name="name",columnDefinition="char(25)")
	private String name;

	private byte numTracks;
	
	private byte numBonusTracks;

	private int timestamp;
	
	@OneToMany(mappedBy="chart",fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.TRUE)
	private Set<ChartDetail> chartDetails = new HashSet<ChartDetail>();

    public Chart() {
    }

	public Byte getI() {
		return this.i;
	}

	public void setI(Byte i) {
		this.i = i;
	}
	
	public byte getCommunityId() {
		return communityId;
	}

	public void setCommunity(Community community) {
		this.community = community;
		communityId = community.getId();
	}
	
	public Community getCommunity() {
		return community;
	}

	public Genre getGenre() {
		return this.genre;
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

	@Override
	public String toString() {
		return "Chart [communityId=" + communityId + ", genreId=" + genreId + ", i=" + i + ", name=" + name + ", numTracks=" + numTracks
		+ ", numBonusTracks="+numBonusTracks+ ", timestamp=" + timestamp + "]";
	}	

}