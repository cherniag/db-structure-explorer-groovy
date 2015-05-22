package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Author: Gennadii Cherniaiev Date: 11/21/2014
 */
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    @Query("select r from Referral r where r.contact = :contact and r.communityId = :communityId")
    Referral findByContactAndCommunityId(@Param("contact") String contact, @Param("communityId") int communityId);

    @Query("select r from Referral r where r.contact = :contact and r.userId = :userId")
    Referral findByContactAndUserId(@Param("contact") String contact, @Param("userId") int userId);

    @Modifying
    @Query("update Referral r set r.state = :newState where r.contact in (:contacts) and r.communityId = :communityId and r.state = :inState")
    void updateReferrals(@Param("contacts") List<String> contacts, @Param("communityId") int communityId, @Param("newState") ReferralState newState, @Param("inState") ReferralState inState);

    @Query("select count(referral) from Referral referral where referral.userId=:userId and referral.communityId=:communityId and referral.state in :states")
    int countByCommunityIdUserIdAndStates(@Param("communityId") Integer communityId, @Param("userId") Integer userId, @Param("states") ReferralState... states);

    @Query("select r from Referral r where r.userId=:userId and r.communityId=:communityId")
    List<Referral> findByCommunityIdUserId(@Param("communityId") Integer communityId, @Param("userId") Integer userId);

    @Query("select r from Referral r where r.userId=:userId and r.communityId=:communityId and r.contact=:contact")
    List<Referral> findByCommunityIdUserIdAndContact(@Param("communityId") Integer communityId, @Param("userId") Integer userId, @Param("contact") String contact);

    @Query("select r.userId from Referral r where r.communityId=:communityId and r.contact in (:contacts)")
    List<Integer> findReferralUserIdsByContacts(@Param("communityId") Integer communityId, @Param("contacts") List<String> contacts);
}
