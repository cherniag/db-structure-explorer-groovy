package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface UserIPhoneDetailsRepository extends PagingAndSortingRepository<UserIPhoneDetails, Integer> {

	@Query(value="SELECT userIPhoneDetails "
			+ "FROM UserIPhoneDetails userIPhoneDetails "
			+ "JOIN userIPhoneDetails.userGroup userGroup "
			+ "WHERE "
			+ "userGroup.community=:community "
			+ "and userIPhoneDetails.lastPushOfContentUpdateMillis<:nearestLatestPublishTimeMillis")
	List<UserIPhoneDetails> getUserIPhoneDetailsListForPushNotification(@Param("community") Community community, @Param("nearestLatestPublishTimeMillis") final long nearestLatestPublishTimeMillis, Pageable pageable);
	
	

}
