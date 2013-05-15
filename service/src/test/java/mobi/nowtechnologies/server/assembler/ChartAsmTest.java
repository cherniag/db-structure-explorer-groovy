package mobi.nowtechnologies.server.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.shared.dto.PlaylistDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

public class ChartAsmTest {
	
	@Test
	public void testToChartDto_Success()
		throws Exception {
		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		Chart chart = chartDetail.getChart();

		ChartDto result = ChartAsm.toChartDto(chartDetail);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getName());
		assertEquals(chart.getI(), result.getId());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getPosition(), result.getPosition().byteValue());
		assertEquals(chartDetail.getImageTitle(), result.getImageTitle());
		assertEquals(chartDetail.getInfo(), result.getDescription());
		assertEquals(chartDetail.getI(), result.getChartDetailId());
	}
	
	@Test
	public void testToChartDto_NullTitle_Success()
			throws Exception {
		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		chartDetail.setTitle(null);
		Chart chart = chartDetail.getChart();
		
		ChartDto result = ChartAsm.toChartDto(chartDetail);
		
		assertNotNull(result);
		assertEquals(chart.getName(), result.getName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testToChartDto_IsChartItem_Failure()
			throws Exception {
		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		chartDetail.setMedia(new Media());
		
		ChartAsm.toChartDto(chartDetail);
	}
	
	@Test
	public void testToChart_NullFile_Success()
		throws Exception {
		ChartDto chartDto = ChartAsm.toChartDto(ChartDetailFactory.createChartDetail());

		ChartDetail result = ChartAsm.toChart(chartDto);

		assertNotNull(result);
		assertEquals(chartDto.getName(), result.getTitle());
		assertEquals(chartDto.getId(), result.getChart().getI());
		assertEquals(chartDto.getImageFileName(), result.getImageFileName());
		assertEquals(chartDto.getSubtitle(), result.getSubtitle());
		assertEquals(chartDto.getPosition().byteValue(), result.getPosition());
		assertEquals(chartDto.getDescription(), result.getInfo());
		assertEquals(chartDto.getImageTitle(), result.getImageTitle());
		assertEquals(chartDto.getChartDetailId(), result.getI());
	}
	
	@Test
	public void testToChart_NotNullFile_Success()
		throws Exception {
		ChartDto chartDto = ChartAsm.toChartDto(ChartDetailFactory.createChartDetail());
		chartDto.setFile(new MockMultipartFile("file", "1".getBytes()));

		ChartDetail result = ChartAsm.toChart(chartDto);

		assertNotNull(result);
		assertEquals(chartDto.getName(), result.getTitle());
		assertEquals(chartDto.getId(), result.getChart().getI());
		assertEquals(chartDto.getSubtitle(), result.getSubtitle());
		assertEquals(chartDto.getPosition().byteValue(), result.getPosition());
		assertEquals(chartDto.getDescription(), result.getInfo());
		assertEquals(chartDto.getImageTitle(), result.getImageTitle());
		assertEquals("CHART_", result.getImageFileName().substring(0, 6));
	}
	
	@Test
	public void testToPlaylistDto_Success()
		throws Exception {
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		Chart chart = chartDetail.getChart();

		PlaylistDto result = ChartAsm.toPlaylistDto(chartDetail);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getPlaylistTitle());
		assertEquals(chart.getI().byteValue(), result.getId().byteValue());
		assertEquals(chartDetail.getImageFileName(), result.getImage());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getPosition(), result.getPosition().byteValue());
		assertEquals(chartDetail.getImageTitle(), result.getImageTitle());
		assertEquals(chartDetail.getInfo(), result.getDescription());
		assertEquals(chart.getType(), result.getType());
	}
	
	@Test
	public void testToPlaylistDto_NullTitle_Success()
			throws Exception {
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		chartDetail.setTitle(null);
		Chart chart = chartDetail.getChart();
		
		PlaylistDto result = ChartAsm.toPlaylistDto(chartDetail);
		
		assertNotNull(result);
		assertEquals(chart.getName(), result.getPlaylistTitle());
	}

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}
}