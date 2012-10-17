package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Chart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface ChartRepository extends JpaRepository<Chart, Byte> {

	@Query("select chart from Chart chart where chart.community.rewriteUrlParameter like ?1 order by chart.name asc")
	List<Chart> getByCommunityURL(String communityURL);

}
