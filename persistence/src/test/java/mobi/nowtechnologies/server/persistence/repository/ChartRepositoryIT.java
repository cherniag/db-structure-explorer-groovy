package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Titov Mykhaylo (titov)
*/
public class ChartRepositoryIT extends AbstractRepositoryIT {

	@Resource(name = "chartRepository")
	private ChartRepository chartRepository;

	@Test
	public void testGetByCommuntityName() throws Exception {

		List<Chart> charts = chartRepository.getByCommunityName("CN Commercial Beta");

		assertNotNull(charts);
		assertEquals(2, charts.size());
	}
	
	@Test
	public void testGetByCommuntityUrl() throws Exception {

		List<Chart> charts = chartRepository.getByCommunityURL("ChartsNow");

		assertNotNull(charts);
		assertEquals(2, charts.size());
	}
}