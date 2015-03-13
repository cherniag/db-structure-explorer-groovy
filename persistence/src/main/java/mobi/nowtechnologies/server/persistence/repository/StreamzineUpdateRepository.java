package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StreamzineUpdateRepository extends JpaRepository<Update, Long> {

    @Query("select distinct(u) from Update u left join fetch u.users where u.date >= ?1 and u.date < ?2 and u.community=?3 order by u.date")
    List<Update> findAllByDate(Date from, Date till, Community community);

    @Query("select u.date from Update u where u.date >= ?1 and u.date < ?2 and u.community=?3 order by u.date")
    List<Date> findUpdatePublishDates(Date from, Date till, Community community);

    @Query("select min(u.date) from Update u join u.users user where u.date >= ?1 and user = ?2 and u.community=?3")
    Date findFirstDateAfterForUser(Date date, User user, Community community);

    @Query("select max(u.date) from Update u where u.date <= ?1 and u.community=?2")
    Date findLastDateSince(Date date, Community community);

    @Query("select u from Update u left join u.users left join fetch u.blocks block left join fetch block.deeplinkInfo where u.id=:id")
    Update findById(@Param("id") Long id);

    @Query("select u from Update u where u.date=:publishDate and u.community=:community")
    Update findByPublishDate(@Param("publishDate") Date publishDate, @Param("community") Community community);
}
