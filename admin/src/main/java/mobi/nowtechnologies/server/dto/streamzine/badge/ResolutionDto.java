package mobi.nowtechnologies.server.dto.streamzine.badge;

import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

public class ResolutionDto {
    private long id;

    @JsonProperty(value = "deviceType")
    @NotEmpty
    private String deviceType;

    @JsonProperty(value = "width")
    @Min(1)
    private int width;

    @JsonProperty(value = "height")
    @Min(1)
    private int height;

    public static ResolutionDto from(Resolution r) {
        ResolutionDto dto = new ResolutionDto();
        dto.id = r.getId();
        dto.deviceType = r.getDeviceType();
        dto.height = r.getHeight();
        dto.width = r.getWidth();
        return dto;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }
}
