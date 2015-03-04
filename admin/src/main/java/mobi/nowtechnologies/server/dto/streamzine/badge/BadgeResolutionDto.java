package mobi.nowtechnologies.server.dto.streamzine.badge;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BadgeResolutionDto {

    @JsonProperty(value = "alias")
    private long aliasId;

    @JsonProperty(value = "resolution")
    private long resolutionId;

    @JsonProperty(value = "width")
    @Min(1)
    private int width;

    @JsonProperty(value = "height")
    @Min(1)
    private int height;

    public long getAliasId() {
        return aliasId;
    }

    public long getResolutionId() {
        return resolutionId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "BadgeResolutionDto{" +
               "aliasId=" + aliasId +
               ", resolutionId=" + resolutionId +
               ", width=" + width +
               ", height=" + height +
               '}';
    }
}
