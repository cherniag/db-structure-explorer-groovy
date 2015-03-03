package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import static javax.persistence.InheritanceType.JOINED;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Entity
@Inheritance(strategy = JOINED)
public class DropContent extends AbstractEntity {

    @Column(name = "ISRC", nullable = false)
    protected String isrc;

    @Column(name = "Artist", nullable = false)
    protected String artist;

    @Column(name = "Title", nullable = false)
    protected String title;

    @Column(name = "updated")
    protected boolean updated;

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

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("isrc", isrc).append("artist", artist).append("title", title).append("updated", updated).toString();
    }
}
