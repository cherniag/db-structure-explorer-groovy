package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface CommunityRepository extends JpaRepository<Community, Integer> {
	
	@Query(value="select community from Community community where LOWER(community.rewriteUrlParameter)=LOWER(?1)")
	Community findByRewriteUrlParameter(String rewriteUrlParameter);

    @Query(value="select community from Community community where LOWER(community.name)=LOWER(?1)")
    Community findByName(String name);
}