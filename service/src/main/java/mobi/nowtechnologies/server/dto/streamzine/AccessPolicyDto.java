package mobi.nowtechnologies.server.dto.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;
import mobi.nowtechnologies.server.persistence.domain.user.GrantedToType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    public Permission getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("permission", permission).append("grantedTo", grantedTo).toString();
    }
}
