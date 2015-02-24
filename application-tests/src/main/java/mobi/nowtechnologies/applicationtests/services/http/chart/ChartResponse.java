package mobi.nowtechnologies.applicationtests.services.http.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Author: Gennadii Cherniaiev
 * Date: 12/5/2014
 */
@JsonTypeName("response")
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.NONE)
public class ChartResponse {
    @XmlElement(name = "response")
    @JsonProperty(value = "response")
    private Response<ChartDataWrapper> response;

    public Response<ChartDataWrapper> getResponse() {
        return response;
    }
}