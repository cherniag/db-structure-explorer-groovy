package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VisualBlock {

    @XmlElement(name = "ref")
    @JsonProperty(value = "ref")
    private String ref;

    @XmlElement(name = "block_type")
    @JsonProperty(value = "block_type")
    private ShapeType shapeType;

    @XmlElement(name = "access_policy")
    @JsonProperty(value = "access_policy")
    private AccessPolicyDto policyDto;

    protected VisualBlock() {
    }

    public VisualBlock(ShapeType shapeType, String ref) {
        this.shapeType = shapeType;
        this.ref = ref;
    }

    public AccessPolicyDto getPolicyDto() {
        return policyDto;
    }

    public void setPolicyDto(AccessPolicyDto policyDto) {
        this.policyDto = policyDto;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ref", ref).append("shapeType", shapeType).append("policyDto", policyDto).toString();
    }
}

