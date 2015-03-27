package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 * Author: Gennadii Cherniaiev Date: 3/18/2015
 */
public interface UserTransactionRepository extends JpaRepository<UserTransaction, Integer> {

    @Modifying
    @Query("delete UserTransaction ut where ut.user = :user")
    int deleteByUser(@Param("user")User user);

    List<UserTransaction> findByUser(User user);
}
