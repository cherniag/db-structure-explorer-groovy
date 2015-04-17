package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.enums.MessageType;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(
        "select distinct message from Message message left join FETCH  message.filterWithCtiteria where message.community= :community and message.messageType in :messageTypes and message" +
        ".publishTimeMillis=:publishTimeMillis order by  message.position asc")
    List<Message> findByCommunityAndMessageTypesAndPublishTimeMillis(@Param("community") Community community, @Param("messageTypes") List<MessageType> messageTypes,
                                                                     @Param("publishTimeMillis") long publishTimeMillis);

    @Query("select distinct message from Message message left join FETCH message.filterWithCtiteria where message.id=?")
    Message findOneWithFilters(Integer messageId);

    @Query("select max(message.position) from Message message where message.community=? and message.messageType=? and message.publishTimeMillis=?")
    Integer findMaxPosition(Community community, MessageType messageType, long publishTimeMillis);

    @Query(
        "select max(message.publishTimeMillis) from Message message where message.community=:community and message.messageType=:messageType and message.publishTimeMillis<=:choosedPublishTimeMillis")
    Long findNearestLatestPublishDate(@Param("choosedPublishTimeMillis") long choosedPublishTimeMillis, @Param("community") Community community, @Param("messageType") MessageType messageType);

    @Query(
        "select max(message.publishTimeMillis) from Message message where message.community=:community and message.messageType='NEWS' and message.publishTimeMillis>:choosedPublishTimeMillis and " +
        "message.publishTimeMillis<:currentTimeMillis")
    Long findNextNewsPublishDate(@Param("choosedPublishTimeMillis") long choosedPublishTimeMillis, @Param("community") Community community, @Param("currentTimeMillis") long currentTimeMillis);

    @Query("select distinct message from Message message " +
           "left join FETCH  message.filterWithCtiteria " +
           "where message.community=?1 " +
           "and message.activated=true " +
           "and ((message.messageType='NEWS' " +
           "and message.publishTimeMillis=?2) " +
           "or message.messageType<>'NEWS') " +
           "order by  message.position asc")
    List<Message> findByCommunityAndPublishTimeMillisAfterOrderByPositionAsc(Community community, long nextNewsPublishTimeMillis);

    @Query("select distinct message from Message message " +
           "left join FETCH  message.filterWithCtiteria " +
           "where message.community=?1 " +
           "and message.activated=true " +
           "and ((message.messageType='NEWS' " +
           "and message.publishTimeMillis=?2) " +
           "or (message.messageType<>'NEWS' " +
           "and message.messageType not in (?3) )) " +
           "order by  message.position asc")
    List<Message> findWithoutBannersByCommunityAndPublishTimeMillisAfterOrderByPositionAsc(Community community, long nextNewsPublishTimeMillis, List<MessageType> banners);

    @Query("select count(message) from Message message where message.community=?1 and message.publishTimeMillis=?2 and message.messageType=?3")
    long countMessages(Community community, long publishTimeMillis, MessageType messageType);

    @Query(
        "select distinct message from Message message left join FETCH  message.filterWithCtiteria where message.community= :community and message.messageType in :messageTypes order by  message" +
        ".position asc")
    List<Message> findByCommunityAndMessageTypes(@Param("community") Community community, @Param("messageTypes") List<MessageType> messageTypes);


    @Query(
        "select distinct message from Message message left join FETCH  message.filterWithCtiteria where message.community= :community and message.messageType='NEWS' and  message" +
        ".publishTimeMillis=:publishTimeMillis order by  message.position asc")
    List<Message> findActualNews(@Param("community") Community community, @Param("publishTimeMillis") long publishTimeMillis);

    @Query("select distinct message from Message message where message.id in :ids")
    List<Message> findAll(@Param("ids") Collection<Integer> ids);

    @Query("select message.publishTimeMillis from Message message where message.community=?1 group by message.publishTimeMillis")
    List<Long> findAllPublishTimeMillis(Community community);
}
