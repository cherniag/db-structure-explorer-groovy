package mobi.nowtechnologies.applicationtests.services.http.news.json;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Response {

    @XmlElement(name = "data")
    @JsonProperty(value = "data")
    private List<DataWrapper> data;

    public DataWrapper get() {
        // first is for User, second for News
        return data.get(1);
    }
}
