package mobi.nowtechnologies.server.dto.streamzine;

import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.GrantedToType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AccessPolicyDto {
    @XmlElement(name = "permission")
    @JsonProperty(value = "permission")
    private Permission permission;

    @XmlElement(name = "grantedTo")
    @JsonProperty(value = "grantedTo")
    private List<GrantedToType> grantedTo = new ArrayList<GrantedToType>();

    protected AccessPolicyDto() {
    }

    public AccessPolicyDto(Permission permission) {
        this.permission = permission;
    }

    public List<GrantedToType> getGrantedTo() {
        return grantedTo;
    }
}
