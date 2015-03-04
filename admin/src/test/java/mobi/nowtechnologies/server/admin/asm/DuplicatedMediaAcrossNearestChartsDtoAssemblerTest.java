package mobi.nowtechnologies.server.admin.asm;

import mobi.nowtechnologies.server.dto.DuplicatedMediaAcrossNearestChartsDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;

import java.util.List;
import static java.util.Arrays.asList;

import org.junit.*;
import org.junit.runner.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class DuplicatedMediaAcrossNearestChartsDtoAssemblerTest {

    DuplicatedMediaAcrossNearestChartsDtoAssembler duplicatedMediaAcrossNearestChartsDtoAssembler = new DuplicatedMediaAcrossNearestChartsDtoAssembler();

    @Test
    public void shouldGetDuplicatedMediaAcrossNearestChartsDtos() {
        //given
        Chart chart1 = new Chart().withI(1).withName("chart1");
        Chart chart2 = new Chart().withI(2).withName("chart2");
        Media media1 = new Media().withIsrc("isrc1").withTrackId(1L);
        Media media2 = new Media().withIsrc("isrc2").withTrackId(2L);
        Media media3 = new Media().withIsrc("isrc3").withTrackId(3L);
        Media media4 = new Media().withIsrc("isrc4").withTrackId(4L);
        ChartDetail chartDetail1 = new ChartDetail().withChart(chart1).withMedia(media1).withPublishTime(1);
        ChartDetail chartDetail2 = new ChartDetail().withChart(chart1).withMedia(media2).withPublishTime(2);
        ChartDetail chartDetail3 = new ChartDetail().withChart(chart2).withMedia(media3).withPublishTime(3);
        ChartDetail chartDetail4 = new ChartDetail().withChart(chart2).withMedia(media4).withPublishTime(4);
        ChartDetail chartDetail5 = new ChartDetail().withChart(chart2).withMedia(media1).withPublishTime(5);
        List<ChartDetail> chartDetails = asList(chartDetail3, chartDetail1, chartDetail4, chartDetail2, chartDetail5);

        //when
        List<DuplicatedMediaAcrossNearestChartsDto> trackIdDuplicatedMediaAcrossNearestChartsDtos =
            duplicatedMediaAcrossNearestChartsDtoAssembler.getDuplicatedMediaAcrossNearestChartsDtos(chartDetails);

        //then
        assertThat(trackIdDuplicatedMediaAcrossNearestChartsDtos.size(), is(5));

        DuplicatedMediaAcrossNearestChartsDto duplicatedMediaAcrossNearestChartsDto = trackIdDuplicatedMediaAcrossNearestChartsDtos.get(0);
        assertThat(duplicatedMediaAcrossNearestChartsDto.getTrackId(), is(chartDetail1.getMedia().getIsrcTrackId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto.getChartId(), is(chartDetail1.getChartId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto.getChartName(), is(chartDetail1.getChart().getName()));
        assertThat(duplicatedMediaAcrossNearestChartsDto.getPosition(), is(chartDetail1.getPosition()));
        assertThat(duplicatedMediaAcrossNearestChartsDto.getPublishTimeMillis(), is(chartDetail1.getPublishTimeMillis()));

        DuplicatedMediaAcrossNearestChartsDto duplicatedMediaAcrossNearestChartsDto1 = trackIdDuplicatedMediaAcrossNearestChartsDtos.get(1);
        assertThat(duplicatedMediaAcrossNearestChartsDto1.getTrackId(), is(chartDetail5.getMedia().getIsrcTrackId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto1.getChartId(), is(chartDetail5.getChartId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto1.getChartName(), is(chartDetail5.getChart().getName()));
        assertThat(duplicatedMediaAcrossNearestChartsDto1.getPosition(), is(chartDetail5.getPosition()));
        assertThat(duplicatedMediaAcrossNearestChartsDto1.getPublishTimeMillis(), is(chartDetail5.getPublishTimeMillis()));

        DuplicatedMediaAcrossNearestChartsDto duplicatedMediaAcrossNearestChartsDto2 = trackIdDuplicatedMediaAcrossNearestChartsDtos.get(2);
        assertThat(duplicatedMediaAcrossNearestChartsDto2.getTrackId(), is(chartDetail2.getMedia().getIsrcTrackId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto2.getChartId(), is(chartDetail2.getChartId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto2.getChartName(), is(chartDetail2.getChart().getName()));
        assertThat(duplicatedMediaAcrossNearestChartsDto2.getPosition(), is(chartDetail2.getPosition()));
        assertThat(duplicatedMediaAcrossNearestChartsDto2.getPublishTimeMillis(), is(chartDetail2.getPublishTimeMillis()));

        DuplicatedMediaAcrossNearestChartsDto duplicatedMediaAcrossNearestChartsDto3 = trackIdDuplicatedMediaAcrossNearestChartsDtos.get(3);
        assertThat(duplicatedMediaAcrossNearestChartsDto3.getTrackId(), is(chartDetail3.getMedia().getIsrcTrackId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto3.getChartId(), is(chartDetail3.getChartId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto3.getChartName(), is(chartDetail3.getChart().getName()));
        assertThat(duplicatedMediaAcrossNearestChartsDto3.getPosition(), is(chartDetail3.getPosition()));
        assertThat(duplicatedMediaAcrossNearestChartsDto3.getPublishTimeMillis(), is(chartDetail3.getPublishTimeMillis()));

        DuplicatedMediaAcrossNearestChartsDto duplicatedMediaAcrossNearestChartsDto4 = trackIdDuplicatedMediaAcrossNearestChartsDtos.get(4);
        assertThat(duplicatedMediaAcrossNearestChartsDto4.getTrackId(), is(chartDetail4.getMedia().getIsrcTrackId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto4.getChartId(), is(chartDetail4.getChartId()));
        assertThat(duplicatedMediaAcrossNearestChartsDto4.getChartName(), is(chartDetail4.getChart().getName()));
        assertThat(duplicatedMediaAcrossNearestChartsDto4.getPosition(), is(chartDetail4.getPosition()));
        assertThat(duplicatedMediaAcrossNearestChartsDto4.getPublishTimeMillis(), is(chartDetail4.getPublishTimeMillis()));
    }
}