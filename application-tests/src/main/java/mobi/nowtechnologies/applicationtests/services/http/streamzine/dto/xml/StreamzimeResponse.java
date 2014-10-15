package mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.xml;

import mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json.StreamzineUpdateDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.NONE)
public class StreamzimeResponse {
    @XmlElement(name = "update")
    private StreamzineUpdateDto value;

    public StreamzineUpdateDto getValue() {
        return value;
    }
}
