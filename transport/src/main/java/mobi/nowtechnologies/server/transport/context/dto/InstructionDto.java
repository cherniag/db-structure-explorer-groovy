package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.NONE)
public class InstructionDto {
    @XmlElement(name = "behavior")
    @JsonProperty(value = "behavior")
    private ContentBehaviorType contentBehaviorType = ContentBehaviorType.DISABLED;

    @JsonProperty("validFrom")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT")
    private Date validFrom;

    protected InstructionDto() {
    }

    public InstructionDto(boolean enabled, Date time) {
        this.contentBehaviorType = ContentBehaviorType.valueOf(enabled);
        this.validFrom = time;
    }
}
