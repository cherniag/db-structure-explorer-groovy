package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface StreamzineUpdateRepository extends JpaRepository<Update, Long> {
    PageRequest ONE_RECORD_PAGEABLE = new PageRequest(0, 1);

    @Query("select u from Update u left join fetch u.users where u.date >= ?1 and u.date < ?2 order by u.date")
    List<Update> findAllByDate(Date from, Date till);

    @Query("select u.date from Update u where u.date >= ?1 and u.date < ?2 order by u.date")
    List<Date> findUpdatePublishDates(Date from, Date till);

    @Query("select u from Update u where u.date = ( select max(u2.date) from Update u2 where u2.date <=:beforeDate )")
    Update findLatestUpdateBeforeDate(@Param("beforeDate") Date beforeDate);

    @Query("select u from Update u where u.date <= ?1 order by u.date desc")
    List<Update> findLastSince(Date date, Pageable pageable);

    @Query("select u from Update u join u.users user where u.date >= ?1 and user = ?2 order by u.date asc")
    List<Update> findFirstAfterForUser(Date date, User user, Pageable pageable);

    @Query("select u from Update u left join u.users left join fetch u.blocks block left join fetch block.deeplinkInfo where u.id=:id")
    Update findById(@Param("id") Long id);

    @Query("select u from Update u where u.date = :publishDate")
    Update findByPublishDate(@Param("publishDate") Date publishDate);
}
