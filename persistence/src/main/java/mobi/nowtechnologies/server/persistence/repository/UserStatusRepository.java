package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserStatusRepository extends JpaRepository<UserStatus, Integer> {

    @Query("select userStatus from UserStatus userStatus " +
           "where userStatus.name = ?1")
    UserStatus findByName(String name);
}
