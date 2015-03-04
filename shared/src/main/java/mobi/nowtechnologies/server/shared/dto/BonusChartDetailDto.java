package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bonusTrack")
public class BonusChartDetailDto extends ChartDetailDto {

    public BonusChartDetailDto() {
    }

    public BonusChartDetailDto(ChartDetailDto chartDetailDto) {
        super(chartDetailDto);
    }

    @Override
    public String toString() {
        return "BonusChartDetailDto [" + super.toString() + "]";
    }
}
