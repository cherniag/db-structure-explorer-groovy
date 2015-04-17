package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// @author Alexander Kolpakov (akolpakov)
public interface ChartRepository extends JpaRepository<Chart, Integer> {

    @Query("select chart from Chart chart join chart.communities community where community.rewriteUrlParameter=:communityURL and chart.i!=:excludedChartId")
    List<Chart> findByCommunityURLAndExcludedChartId(@Param("communityURL") String communityURL, @Param("excludedChartId") Integer excludedChartId);

    @Query("select chart from Chart chart join chart.communities community where community.rewriteUrlParameter like ?1 order by chart.name asc")
    List<Chart> findByCommunityURL(String communityURL);

    @Query("select chart from Chart chart join chart.communities community where community.name = ?1 order by chart.name asc")
    List<Chart> findByCommunityName(String communityName);

    @Query("select chart from Chart chart join chart.communities community where community.rewriteUrlParameter like ?1 and chart.type=?2 order by chart.name asc")
    List<Chart> findByCommunityURLAndChartType(String communityURL, ChartType chartType);

    @Query("select chart from Chart chart join chart.communities community where community.name = ?1 and chart.type=?2 order by chart.name asc")
    List<Chart> findByCommunityNameAndChartType(String communityName, ChartType chartType);
}
