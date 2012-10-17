package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the tb_news database table.
 * 
 */
@Entity
@Table(name="tb_news")
@XmlRootElement
public class News implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
	private byte i;

	@XmlTransient
	@Column(name="community", insertable=false, updatable=false)
	private byte communityId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="community")
	private Community community;

	@XmlTransient
	@Column(name="name",columnDefinition="char(25)")
	private String name;

	@XmlTransient
	private byte numEntries;

	@XmlTransient
	private int timestamp;
	
	@OneToMany(mappedBy="news",fetch=FetchType.LAZY)
	@XmlElement(name="item")
	private Set<NewsDetail> newsDetailsSet;

    public News() {
    }

    @XmlTransient
	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	@XmlTransient
	public Community getCommunity() {
		return this.community;
	}

	public void setCommunity(Community community) {
		this.community = community;
		communityId=community.getId();
	}
	
	public byte getCommunityId() {
		return communityId;
	}

	@XmlTransient
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public byte getNumEntries() {
		return this.numEntries;
	}

	public void setNumEntries(byte numEntries) {
		this.numEntries = numEntries;
	}

	@XmlTransient
	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@XmlTransient
	public Set<NewsDetail> getNewsDetails() {
		return newsDetailsSet;
	}

	public void setNewsDetails(Set<NewsDetail> newsDetails) {
		this.newsDetailsSet = newsDetails;
	}

	@Override
	public String toString() {
		return "News [communityId=" + communityId + ", i=" + i + ", name=" + name + ", numEntries=" + numEntries + ", timestamp=" + timestamp + "]";
	}
}