package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Arrays;

/**
 * @author Titov Mykhaylo (titov)
 */
@XmlRootElement(name = "chart")
public class PurchasedChartDto {

    private PurchasedChartDetailDto[] purchasedChartDetailDtos;

    @XmlAnyElement
    public PurchasedChartDetailDto[] getPurchasedChartDetailDtos() {
        return purchasedChartDetailDtos;
    }

    public void setPurchasedChartDetailDtos(PurchasedChartDetailDto[] purchasedChartDetailDtos) {
        this.purchasedChartDetailDtos = purchasedChartDetailDtos;
    }

    @Override
    public String toString() {
        return "ChartDto [purchasedChartDetailDtos=" + Arrays.toString(purchasedChartDetailDtos) + "]";
    }

}
