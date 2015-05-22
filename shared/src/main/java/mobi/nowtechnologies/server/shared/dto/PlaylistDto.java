package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.shared.enums.ChartType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    private Integer chartUpdateId;

    @XmlElement(name = "badge_icon")
    @JsonProperty(value = "badge_icon")
    private String badgeIcon;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
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

    public Integer getChartUpdateId() {
        return chartUpdateId;
    }

    public void setChartUpdateId(Integer chartUpdateId) {
        this.chartUpdateId = chartUpdateId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("playlistTitle", playlistTitle).append("subtitle", subtitle).append("image", image).append("imageTitle", imageTitle)
                                        .append("description", description).append("position", position).append("switchable", switchable).append("type", type)
                                        .append("badgeIcon", badgeIcon).append("chartUpdateId", chartUpdateId).toString();
    }
}