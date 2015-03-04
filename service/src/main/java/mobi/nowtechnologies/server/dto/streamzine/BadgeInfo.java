package mobi.nowtechnologies.server.dto.streamzine;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class BadgeInfo {

    @XmlElement(name = "image")
    @JsonProperty(value = "image")
    private String image;

    @XmlElement(name = "width")
    @JsonProperty(value = "width")
    private int width;

    @XmlElement(name = "height")
    @JsonProperty(value = "height")
    private int height;

    public BadgeInfo() {
    }

    public BadgeInfo(String image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }

    public String getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
