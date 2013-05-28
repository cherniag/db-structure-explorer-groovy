package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public interface ChartRepository extends JpaRepository<Chart, Byte> {

	@Query("select chart from Chart chart join chart.communities community where community.rewriteUrlParameter like ?1 order by chart.name asc")
	List<Chart> getByCommunityURL(String communityURL);

	@Query("select chart from Chart chart join chart.communities community where community.name = ?1 order by chart.name asc")
	List<Chart> getByCommunityName(String communityName);
	
	@Query("select chart from Chart chart join chart.communities community where community.rewriteUrlParameter like ?1 and chart.type=?2 order by chart.name asc")
	List<Chart> getByCommunityURLAndChartType(String communityURL, ChartType chartType);
	
	@Query("select chart from Chart chart join chart.communities community where community.name = ?1 and chart.type=?2 order by chart.name asc")
	List<Chart> getByCommunityNameAndChartType(String communityName, ChartType chartType);
}
