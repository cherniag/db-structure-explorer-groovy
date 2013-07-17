package mobi.nowtechnologies.server.trackrepo.domain;

import mobi.nowtechnologies.server.persistence.domain.Artist;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class DropContent extends AbstractEntity {

	@Basic(optional = false)
    @Column(name="ISRC")
	protected String isrc;

	@Basic(optional = false)
    @Column(name="Artist")
    protected String artist;

	@Basic(optional = false)
    @Column(name="Title")
    protected String title;

	@Basic(optional=false)
    @Column(name="updated")
    protected Boolean updated;
	
	@ManyToOne
	@JoinColumn(name = "DropId", insertable = false, updatable = false)

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "DropContent{" +
                "isrc='" + isrc + '\'' +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", updated=" + updated +
                "} " + super.toString();
    }
}
