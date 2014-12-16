package mobi.nowtechnologies.server.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "playlist")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaylistDto {
    private Integer id;
    private String playlistTitle;
    private String subtitle;
    private String image;
    private String imageTitle;
    private String description;
    private Byte position;
    private Boolean switchable;
    private ChartType type;
    private Boolean locked;

    @XmlElement(name = "badge_icon")
    @JsonProperty(value = "badge_icon")
    private String badgeIcon;

    public Integer getId() {
        return id;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getPosition() {
        return position;
    }

    public void setPosition(Byte position) {
        this.position = position;
    }

    public Boolean getSwitchable() {
        return switchable;
    }

    public void setSwitchable(Boolean switchable) {
        this.switchable = switchable;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("playlistTitle", playlistTitle)
                .append("subtitle", subtitle)
                .append("image", image)
                .append("imageTitle", imageTitle)
                .append("description", description)
                .append("position", position)
                .append("switchable", switchable)
                .append("type", type)
                .append("locked", locked)
                .append("badgeIcon", badgeIcon)
                .toString();
    }
}