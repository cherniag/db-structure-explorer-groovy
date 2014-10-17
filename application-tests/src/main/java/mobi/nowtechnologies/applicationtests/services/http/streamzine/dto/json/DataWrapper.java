package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DataWrapper {
    @XmlElement(name = "value")
    @JsonProperty(value = "value")
    private StreamzineUpdateDto value;

    public StreamzineUpdateDto getValue() {
        return value;
    }
}