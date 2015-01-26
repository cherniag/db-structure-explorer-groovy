package mobi.nowtechnologies.server.transport.context.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

// Created by zam on 11/21/2014.
@XmlRootElement(name = "context")
@JsonRootName("context")
public class ContextDto {
    @JsonProperty("serverTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT")
    private Date serverTime;

    @JsonProperty("referrals")
    private ReferralsContextDto referralsContextDto = new ReferralsContextDto();

    @JsonProperty("playlists")
    private ChartContextDto chartContextDto = new ChartContextDto();

    @JsonProperty("favorites")
    private InstructionsDto favoritesContextDto = new InstructionsDto();

    @XmlElement(name = "ads")
    @JsonProperty(value = "ads")
    private InstructionsDto adsContextDto = new InstructionsDto();

    protected ContextDto() {
    }

    public static ContextDto empty() {
        ContextDto dto = new ContextDto();
        dto.chartContextDto = null;
        dto.favoritesContextDto = null;
        dto.adsContextDto = null;
        return dto;
    }

    public static ContextDto normal(Date serverTime) {
        ContextDto dto = new ContextDto();
        dto.serverTime = serverTime;
        return dto;
    }

    public ReferralsContextDto getReferralsContextDto() {
        return referralsContextDto;
    }

    public ChartContextDto getChartContextDto() {
        return chartContextDto;
    }

    public InstructionsDto getFavoritesContextDto() {
        return favoritesContextDto;
    }

    public InstructionsDto getAdsContextDto() {
        return adsContextDto;
    }
}
