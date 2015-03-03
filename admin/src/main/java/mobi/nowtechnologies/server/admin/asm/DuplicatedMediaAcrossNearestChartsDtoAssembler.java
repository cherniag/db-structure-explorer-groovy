package mobi.nowtechnologies.server.admin.asm;

import mobi.nowtechnologies.server.dto.DuplicatedMediaAcrossNearestChartsDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static java.util.Collections.sort;

// @author Titov Mykhaylo (titov) on 13.11.2014.
public class DuplicatedMediaAcrossNearestChartsDtoAssembler {

    private static final Comparator<DuplicatedMediaAcrossNearestChartsDto> DUPLICATED_MEDIA_ACROSS_NEAREST_CHARTS_DTO_BY_TRACK_ID_COMPARATOR = new Comparator<DuplicatedMediaAcrossNearestChartsDto>() {
        @Override
        public int compare(DuplicatedMediaAcrossNearestChartsDto t0, DuplicatedMediaAcrossNearestChartsDto t1) {
            return t0.getTrackId().compareTo(t1.getTrackId());
        }
    };

    public List<DuplicatedMediaAcrossNearestChartsDto> getDuplicatedMediaAcrossNearestChartsDtos(List<ChartDetail> chartDetails) {
        List<DuplicatedMediaAcrossNearestChartsDto> duplicatedMediaAcrossNearestChartsDtos = new ArrayList<DuplicatedMediaAcrossNearestChartsDto>(chartDetails.size());
        for (ChartDetail chartDetail : chartDetails) {
            duplicatedMediaAcrossNearestChartsDtos.add(getDuplicatedMediaAcrossNearestChartsDto(chartDetail));
        }

        sort(duplicatedMediaAcrossNearestChartsDtos, DUPLICATED_MEDIA_ACROSS_NEAREST_CHARTS_DTO_BY_TRACK_ID_COMPARATOR);
        return duplicatedMediaAcrossNearestChartsDtos;
    }

    private DuplicatedMediaAcrossNearestChartsDto getDuplicatedMediaAcrossNearestChartsDto(ChartDetail chartDetail) {
        DuplicatedMediaAcrossNearestChartsDto duplicatedMediaAcrossNearestChartsDto = new DuplicatedMediaAcrossNearestChartsDto();

        duplicatedMediaAcrossNearestChartsDto.setChartName(chartDetail.getChart().getName());
        duplicatedMediaAcrossNearestChartsDto.setChartId(chartDetail.getChart().getI());
        duplicatedMediaAcrossNearestChartsDto.setPosition(chartDetail.getPosition());
        duplicatedMediaAcrossNearestChartsDto.setPublishTimeMillis(chartDetail.getPublishTimeMillis());
        duplicatedMediaAcrossNearestChartsDto.setTrackId(chartDetail.getMedia().getIsrcTrackId());

        return duplicatedMediaAcrossNearestChartsDto;
    }
}
