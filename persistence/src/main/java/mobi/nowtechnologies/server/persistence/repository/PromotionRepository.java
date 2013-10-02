package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PromotionRepository extends JpaRepository<Promotion, String> {

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

    @Modifying
    @Query(value = "update Promotion p " +
            "set p.numUsers=p.numUsers+1 " +
            "where " +
            "p=?1 " +
            "and (p.maxUsers=0 or p.numUsers<p.maxUsers) ")
    int updatePromotionNumUsers(Promotion promotion);
}
