package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

@XmlRootElement(name = "lockedTrack")
public class LockedTrackDto {

    private String media;

    public static LockedTrackDto[] fromChartDetailList(List<ChartDetail> chartDetails) {
        LockedTrackDto[] dtos = new LockedTrackDto[chartDetails.size()];

        int i = 0;
        for (ChartDetail chart : chartDetails) {
            dtos[i] = fromChartDetail(chart);
            i++;
        }

        return dtos;
    }

    public static LockedTrackDto fromChartDetail(ChartDetail chartDetail) {
        LockedTrackDto dto = new LockedTrackDto();
        dto.setMedia(chartDetail.getMedia().getIsrcTrackId());

        return dto;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("media", media).toString();
    }
}
