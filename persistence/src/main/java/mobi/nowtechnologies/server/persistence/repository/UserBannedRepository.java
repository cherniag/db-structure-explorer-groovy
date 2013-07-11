package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserBanned;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBannedRepository extends JpaRepository<UserBanned, Integer> {
}
