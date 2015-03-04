package mobi.nowtechnologies.server.persistence.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Titov Mykhaylo (titov)
 */
@XmlRootElement(name = "deviceSet")
public class DeviceSet {

    private Status status = Status.FAIL;

    @XmlTransient
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @XmlValue
    public String getStatusValue() {
        return status.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).toString();
    }

    public enum Status {
        OK(), FAIL();
    }
}
