package mobi.nowtechnologies.server.dto.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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
