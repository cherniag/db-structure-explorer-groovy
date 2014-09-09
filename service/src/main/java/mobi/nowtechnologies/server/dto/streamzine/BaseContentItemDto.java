package mobi.nowtechnologies.server.dto.streamzine;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseContentItemDto {
    @XmlElement(name = "id")
    @JsonProperty(value = "id")
    private String id;

    @XmlElement(name = "title")
    @JsonProperty(value = "title")
    private String title;

    @XmlElement(name = "sub_title")
    @JsonProperty(value = "sub_title")
    private String subTitle;

    @XmlElement(name = "image")
    @JsonProperty(value = "image")
    private String image;

    @XmlElement(name = "badge_icon")
    @JsonProperty(value = "badge_icon")
    private String badgeIcon;

    @XmlElement(name = "link_type")
    @JsonProperty(value = "link_type")
    private DeeplinkType linkType;

    protected BaseContentItemDto() {
    }

    public BaseContentItemDto(String id, DeeplinkType linkType) {
        this.id = id;
        this.linkType = linkType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getImage() {
        return image;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .append("subTitle", subTitle)
                .append("image", image)
                .append("badgeIcon", badgeIcon)
                .append("linkType", linkType)
                .toString();
    }
}
