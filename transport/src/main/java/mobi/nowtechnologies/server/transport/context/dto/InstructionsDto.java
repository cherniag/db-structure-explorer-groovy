package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.NONE)
public class InstructionsDto {
    @XmlElement(name = "instructions")
    @JsonProperty(value = "instructions")
    private List<InstructionDto> instructions = new ArrayList<InstructionDto>();

    public List<InstructionDto> getInstructions() {
        return instructions;
    }
}
