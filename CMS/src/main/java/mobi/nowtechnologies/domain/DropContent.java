package mobi.nowtechnologies.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class DropContent extends AbstractEntity {

	@Basic(optional = false)
	protected String ISRC;

	@Basic(optional = false)
	protected String Artist;

	@Basic(optional = false)
	protected String Title;

	@Basic(optional=false)
	protected Boolean Updated;
	
	@ManyToOne
	@JoinColumn(name = "DropId", insertable = false, updatable = false)

	public String getISRC() {
		return ISRC;
	}

	public void setISRC(String iSRC) {
		ISRC = iSRC;
	}

	public String getArtist() {
		return Artist;
	}

	public void setArtist(String artist) {
		Artist = artist;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public Boolean getUpdated() {
		return Updated;
	}

	public void setUpdated(Boolean updated) {
		Updated = updated;
	}	

}
