package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.*;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.ChartType.BASIC_CHART;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.enums.FileType.MOBILE_AUDIO;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

// @author Titov Mykhaylo (titov)
public class ChartRepositoryIT extends AbstractRepositoryIT {

	@Resource ChartRepository chartRepository;
	@Resource GenreRepository genreRepository;
	@Resource CommunityRepository communityRepository;

	@Test
	public void testGetByCommunityName() throws Exception {

		List<Chart> charts = chartRepository.getByCommunityName("CN Commercial Beta");

		assertNotNull(charts);
		assertEquals(2, charts.size());
	}
	
	@Test
	public void testGetByCommunityUrl() throws Exception {

		List<Chart> charts = chartRepository.getByCommunityURL("ChartsNow");

		assertNotNull(charts);
		assertEquals(2, charts.size());
	}

	@Test
	public void shouldGetByCommunityURLAndExcludedChartId() {
		//given
		String communityUrl="g";
		Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

		Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));

		Chart chart1 = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));
		Chart chart2 = chartRepository.save(new Chart().withCommunity(community).withName("chart 2").withGenre(rockGenre).withChartType(BASIC_CHART));

		//when
		List<Chart> charts = chartRepository.getByCommunityURLAndExcludedChartId(communityUrl, chart1.getI());

		//then
		assertThat(charts.size(), is(1));
		assertThat(charts.get(0).getI(), is(chart2.getI()));
	}
}