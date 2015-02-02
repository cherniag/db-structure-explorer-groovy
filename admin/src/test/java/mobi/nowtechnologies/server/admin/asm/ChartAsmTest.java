package mobi.nowtechnologies.server.admin.asm;

import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.ChartDetailFactory;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChartAsmTest {

    @Mock
    private FilenameAliasRepository filenameAliasRepository;

    @InjectMocks
    private ChartAsm chartAsm;

    @Test
    public void testToChartDto_Success() throws Exception {
        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        Chart chart = chartDetail.getChart();

        ChartDto result = chartAsm.toChartDto(chartDetail);

        assertNotNull(result);
        assertEquals(chartDetail.getTitle(), result.getName());
        assertEquals(chart.getI(), result.getId());
        assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
        assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
        assertEquals(chartDetail.getPosition(), result.getPosition().byteValue());
        assertEquals(chartDetail.getImageTitle(), result.getImageTitle());
        assertEquals(chartDetail.getInfo(), result.getDescription());
        assertEquals(chartDetail.getI(), result.getChartDetailId());
        assertEquals(chartDetail.getDefaultChart(), result.getDefaultChart());
        assertEquals(chartDetail.getChart().getType(), result.getChartType());
    }

    @Test
    public void testToChartDtoWithBadge_Success() throws Exception {
        final long id = 123L;

        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        chartDetail.setBadgeId(id);

        FilenameAlias filenameAlias = mock(FilenameAlias.class);
        when(filenameAlias.getId()).thenReturn(id);
        when(filenameAlias.getFileName()).thenReturn("FileName");
        when(filenameAlias.getAlias()).thenReturn("FileAlias");

        when(filenameAliasRepository.findOne(id)).thenReturn(filenameAlias);

        ChartDto result = chartAsm.toChartDto(chartDetail);

        assertEquals(id, result.getFileNameAlias().getId());
        assertEquals("FileName", result.getFileNameAlias().getFileName());
        assertEquals("FileAlias", result.getFileNameAlias().getAlias());
    }

    @Test
    public void testToChartDto_NullTitle_Success() throws Exception {
        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        chartDetail.setTitle(null);
        Chart chart = chartDetail.getChart();

        ChartDto result = chartAsm.toChartDto(chartDetail);

        assertNotNull(result);
        assertEquals(chart.getName(), result.getName());
        assertEquals(null, result.getPosition());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToChartDto_IsChartItem_Failure() throws Exception {

        ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
        chartDetail.setMedia(new Media());

        chartAsm.toChartDto(chartDetail);
    }

    @Test
    public void testToChart_NullFile_Success() throws Exception {
        ChartDto chartDto = chartAsm.toChartDto(ChartDetailFactory.createChartDetail());

        ChartDetail result = chartAsm.toChart(chartDto);

        assertNotNull(result);
        assertEquals(chartDto.getName(), result.getTitle());
        assertEquals(chartDto.getId(), result.getChart().getI());
        assertEquals(chartDto.getImageFileName(), result.getImageFileName());
        assertEquals(chartDto.getSubtitle(), result.getSubtitle());
        assertEquals(chartDto.getPosition().byteValue(), result.getPosition());
        assertEquals(chartDto.getDescription(), result.getInfo());
        assertEquals(chartDto.getImageTitle(), result.getImageTitle());
        assertEquals(chartDto.getDefaultChart(), result.getDefaultChart());
        assertEquals(chartDto.getChartDetailId(), result.getI());
    }

    @Test
    public void testToChartWithFileNameAlias_Success() throws Exception {
        final Long fileNameAliasId = 123L;
        ChartDto chartDto = chartAsm.toChartDto(ChartDetailFactory.createChartDetail());
        chartDto.setBadgeId(fileNameAliasId);
        ChartDetail result = chartAsm.toChart(chartDto);

        assertNotNull(result);
        assertEquals(chartDto.getName(), result.getTitle());
        assertEquals(chartDto.getId(), result.getChart().getI());
        assertEquals(chartDto.getImageFileName(), result.getImageFileName());
        assertEquals(chartDto.getSubtitle(), result.getSubtitle());
        assertEquals(chartDto.getPosition().byteValue(), result.getPosition());
        assertEquals(chartDto.getDescription(), result.getInfo());
        assertEquals(chartDto.getImageTitle(), result.getImageTitle());
        assertEquals(chartDto.getDefaultChart(), result.getDefaultChart());
        assertEquals(chartDto.getChartDetailId(), result.getI());
        assertEquals(fileNameAliasId, result.getBadgeId());
    }

    @Test
    public void testToChart_NotNullFile_Success() throws Exception {
        ChartDto chartDto = chartAsm.toChartDto(ChartDetailFactory.createChartDetail());
        chartDto.setFile(new MockMultipartFile("file", "1".getBytes()));

        ChartDetail result = chartAsm.toChart(chartDto);

        assertNotNull(result);
        assertEquals(chartDto.getName(), result.getTitle());
        assertEquals(chartDto.getId(), result.getChart().getI());
        assertEquals(chartDto.getSubtitle(), result.getSubtitle());
        assertEquals(chartDto.getPosition().byteValue(), result.getPosition());
        assertEquals(chartDto.getDescription(), result.getInfo());
        assertEquals(chartDto.getImageTitle(), result.getImageTitle());
        assertEquals(chartDto.getDefaultChart(), result.getDefaultChart());
        assertEquals("CHART_", result.getImageFileName().substring(0, 6));
    }

}