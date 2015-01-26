package mobi.nowtechnologies.applicationtests.services.http.chart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.applicationtests.services.http.common.UserInResponse;
import mobi.nowtechnologies.server.shared.dto.ChartDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ChartDataWrapper {
    @XmlElement(name = "chart")
    @JsonProperty(value = "chart")
    private ChartDto chart;

    @XmlElement(name = "user")
    @JsonProperty(value = "user")
    @JsonIgnore
    private UserInResponse user;

    public ChartDto getValue() {
        return chart;
    }

    public UserInResponse getUser() {
        return user;
    }
}
