package mobi.nowtechnologies.server.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mobi.nowtechnologies.server.persistence.domain.Chart;
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
		Chart chart = new Chart();
		chart.setI(new Byte((byte) 1));
		chart.setSubtitle("subtitle");
		chart.setName("title");
		chart.setImageFileName("imageFilename");

		ChartDto result = ChartAsm.toChartDto(chart);

		assertNotNull(result);
		assertEquals(chart.getName(), result.getName());
		assertEquals(chart.getI(), result.getId());
		assertEquals(chart.getImageFileName(), result.getImageFileName());
		assertEquals(chart.getSubtitle(), result.getSubtitle());
	}
	
	@Test
	public void testToChart_NullFile_Success()
		throws Exception {
		ChartDto chartDto = new ChartDto();
		chartDto.setId(new Byte((byte) 1));
		chartDto.setSubtitle("subtitle");
		chartDto.setName("title");
		chartDto.setImageFileName("imageFilename");
		chartDto.setFile(null);

		Chart result = ChartAsm.toChart(chartDto);

		assertNotNull(result);
		assertEquals(chartDto.getName(), result.getName());
		assertEquals(chartDto.getId(), result.getI());
		assertEquals(chartDto.getImageFileName(), result.getImageFileName());
		assertEquals(chartDto.getSubtitle(), result.getSubtitle());
	}
	
	@Test
	public void testToChart_NotNullFile_Success()
		throws Exception {
		ChartDto chartDto = new ChartDto();
		chartDto.setId(new Byte((byte) 1));
		chartDto.setSubtitle("subtitle");
		chartDto.setName("title");
		chartDto.setImageFileName("imageFilename");
		chartDto.setFile(new MockMultipartFile("file", "1".getBytes()));

		Chart result = ChartAsm.toChart(chartDto);

		assertNotNull(result);
		assertEquals(chartDto.getName(), result.getName());
		assertEquals(chartDto.getId(), result.getI());
		assertEquals("CHART_", result.getImageFileName().substring(0, 6));
		assertEquals(chartDto.getSubtitle(), result.getSubtitle());
	}
	
	@Test
	public void testToPlaylistDto_Success()
		throws Exception {
		Chart chart = new Chart();
		chart.setI(new Byte((byte) 1));
		chart.setSubtitle("subtitle");
		chart.setName("title");
		chart.setImageFileName("imageFilename");

		PlaylistDto result = ChartAsm.toPlaylistDto(chart);

		assertNotNull(result);
		assertEquals(chart.getName(), result.getPlaylistTitle());
		assertEquals(chart.getI(), result.getId());
		assertEquals(chart.getImageFileName(), result.getImage());
		assertEquals(chart.getSubtitle(), result.getSubtitle());
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