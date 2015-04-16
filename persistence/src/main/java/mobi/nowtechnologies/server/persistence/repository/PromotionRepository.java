package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    @Query(value = "select promotion from Promotion promotion " +
                   "join promotion.promoCode promoCode " +
                   "where " +
                   "promoCode.code=?1 " +
                   "and promotion.userGroup=?2 " +
                   "and promotion.startDate<?3 " +
                   "and promotion.endDate>?3 " +
                   "and (promotion.maxUsers=0 or promotion.numUsers<promotion.maxUsers) " +
                   "and promotion.isActive=true " +
                   "and promotion.type=?4")
    Promotion getActivePromoCodePromotion(String promotionCode, UserGroup userGroup, int epochSeconds, String promotionType);


    @Query(value = "select promotion from Promotion promotion " +
                   "join promotion.promoCode promoCode " +
                   "where " +
                   "promoCode.code=?1 " +
                   "and promotion.userGroup=?2 " +
                   "and promotion.type=?3")
    Promotion getPromotionByPromoCode(String promotionCode, UserGroup userGroup, String promotionType);


    @Modifying
    @Query(value = "update Promotion p " +
                   "set p.numUsers=p.numUsers+1 " +
                   "where " +
                   "p=?1 " +
                   "and (p.maxUsers=0 or p.numUsers<p.maxUsers) ")
    int updatePromotionNumUsers(Promotion promotion);


    @Query("select distinct p from Promotion p " +
           "join p.filters " +
           "where p.isActive = true " +
           "and (p.numUsers < p.maxUsers or p.maxUsers = 0) " +
           "and p.endDate > :timestamp " +
           "and p.startDate < :timestamp  " +
           "and p.userGroup.id = :userGroupId")
    List<Promotion> findPromotionWithFilters(@Param("userGroupId") int userGroupId, @Param("timestamp") int timestamp);
}
