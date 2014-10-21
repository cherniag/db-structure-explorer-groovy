package mobi.nowtechnologies.applicationtests.services.http.news.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JsonTypeName("response")
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.NONE)
public class JsonNewsResponse {
    @XmlElement(name = "response")
    @JsonProperty(value = "response")
    private Response response;

    public Response getResponse() {
        return response;
    }
}
