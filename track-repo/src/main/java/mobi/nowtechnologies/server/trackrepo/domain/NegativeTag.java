package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

// @author Titov Mykhaylo (titov) on 07.11.2014.
@Entity
@Table
public class NegativeTag extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "trackId")
    private Track track;

    @Column(nullable = false)
    private String tag;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public NegativeTag withTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("tag", tag).toString();
    }
}
