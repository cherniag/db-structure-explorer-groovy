package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import static mobi.nowtechnologies.server.shared.enums.ChartType.BASIC_CHART;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

// @author Titov Mykhaylo (titov)
public class ChartRepositoryIT extends AbstractRepositoryIT {

    @Resource
    ChartRepository chartRepository;
    @Resource
    GenreRepository genreRepository;
    @Resource
    CommunityRepository communityRepository;

    @Test
    public void testGetByCommunityName() throws Exception {

        List<Chart> charts = chartRepository.findByCommunityName("CN Commercial Beta");

        assertNotNull(charts);
        assertEquals(2, charts.size());
    }

    @Test
    public void testGetByCommunityUrl() throws Exception {

        List<Chart> charts = chartRepository.findByCommunityURL("ChartsNow");

        assertNotNull(charts);
        assertEquals(2, charts.size());
    }

    @Test
    public void shouldGetByCommunityURLAndExcludedChartId() {
        //given
        String communityUrl = "g";
        Community community = communityRepository.save(new Community().withRewriteUrl(communityUrl).withName(communityUrl));

        Genre rockGenre = genreRepository.save(new Genre().withName("Rock"));

        Chart chart1 = chartRepository.save(new Chart().withCommunity(community).withName("chart 1").withGenre(rockGenre).withChartType(BASIC_CHART));
        Chart chart2 = chartRepository.save(new Chart().withCommunity(community).withName("chart 2").withGenre(rockGenre).withChartType(BASIC_CHART));

        //when
        List<Chart> charts = chartRepository.findByCommunityURLAndExcludedChartId(communityUrl, chart1.getI());

        //then
        assertThat(charts.size(), is(1));
        assertThat(charts.get(0).getI(), is(chart2.getI()));
    }
}