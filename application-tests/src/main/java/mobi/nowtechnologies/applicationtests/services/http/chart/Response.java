package mobi.nowtechnologies.applicationtests.services.http.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Author: Gennadii Cherniaiev
 * Date: 12/5/2014
 */
@JsonTypeName("response")
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response<T> {
    @XmlElement(name = "data")
    @JsonProperty(value = "data")
    List<T> data;

    public List<T> getData() {
        return data;
    }
}
