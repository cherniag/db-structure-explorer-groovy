package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.SubscriptionCampaignRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Author: Gennadii Cherniaiev Date: 4/8/2014
 */
public interface SubscriptionCampaignRepository extends JpaRepository<SubscriptionCampaignRecord, Long> {

    @Query("select count(scr) from SubscriptionCampaignRecord scr where scr.mobile = :mobile and scr.campaignId = :campaignId")
    public long countForMobile(@Param("mobile") String mobile, @Param("campaignId") String campaignId);
}
