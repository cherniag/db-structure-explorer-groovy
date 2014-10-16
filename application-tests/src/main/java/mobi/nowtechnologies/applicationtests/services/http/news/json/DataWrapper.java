package mobi.nowtechnologies.applicationtests.services.http.news.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.applicationtests.services.http.common.UserInResponse;
import mobi.nowtechnologies.server.shared.dto.NewsDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DataWrapper {
    @XmlElement(name = "news")
    @JsonProperty(value = "news")
    private NewsDto value;

    @XmlElement(name = "user")
    @JsonProperty(value = "user")
    @JsonIgnore
    private UserInResponse user;

    public NewsDto getValue() {
        return value;
    }

    public UserInResponse getUser() {
        return user;
    }
}
