package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * User: Titov Mykhaylo (titov)
 * 09.10.13 15:13
 */
public interface UserGroupRepository extends JpaRepository<UserGroup, Byte> {

    UserGroup findByCommunity(Community community);
}
