package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static mobi.nowtechnologies.server.persistence.dao.UserGroupDao.*;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.*;
import static mobi.nowtechnologies.server.shared.enums.MediaType.*;

/**
 * User: Titov Mykhaylo (titov)
 * 02.08.13 15:20
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PromotionRepositoryIT {

    @Resource(name = "promotionRepository")
    PromotionRepository promotionRepository;

    @Resource(name = "promoCodeRepository")
    PromoCodeRepository promoCodeRepository;

    @Resource(name = "communityRepository")
    CommunityRepository communityRepository;

    private String promotionCode;

    private PromoCode promoCode;
    private UserGroup o2UserGroup;
    private Promotion activePromoCodePromotion;
    private Promotion o2PromotionByPromoCodeBefore2014EndDate;
    private PromoCode promoCodeForO2PromotionBefore2014EndDate;
    private PromoCode promoCodeForO2PromotionAfter2014StartDate;
    private Promotion o2PromotionByPromoCodeAfter2014StartDate;

    @Before
    public void setUp(){
        Community o2Community = communityRepository.findByRewriteUrlParameter("o2");

        o2UserGroup = getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2Community.getId());
    }

    @Test
    public void shouldDoNotReturnNotActivePromotion(){
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate().withIsActive(false));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnLimitedPromotion(){
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate().withNumUsers(1).withMaxUsers(1));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnExpiredPromotion(){
        //given
        promotionCode = "code";
        int currentTime2015Seconds = 2015;

        saved(o2PromotionByPromoCodeBefore2014EndDate());
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTime2015Seconds, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnAddSubBalancePromotion(){
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate().withType(ADD_SUBBALANCE_PROMOTION));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnNotStartedPromotion(){
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeAfter2014StartDate());
        saved(promoCodeForO2PromotionAfter2014StartDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldReturnPromotionBefore2014EndDate(){
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate());
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsReturnedPromotionBefore2014();
    }

    @Test
    public void shouldReturnPromotionAfter2014StartDate(){
        //given
        promotionCode = "code";
        int currentTime2015Seconds = 2015;

        saved(o2PromotionByPromoCodeBefore2014EndDate());
        saved(promoCodeForO2PromotionBefore2014EndDate());

        saved(o2PromotionByPromoCodeAfter2014StartDate());
        saved(promoCodeForO2PromotionAfter2014StartDate());

        // when
        activePromoCodePromotion = promotionRepository.getActivePromoCodePromotion(promotionCode, o2UserGroup, currentTime2015Seconds, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsReturnedPromotionAfter2014StartDate();
    }

    Promotion o2PromotionByPromoCodeAfter2014StartDate() {
        o2PromotionByPromoCodeAfter2014StartDate = new Promotion().withStartDate(2014).withEndDate(2016).withIsActive(true).withMaxUsers(0).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription("");
        return o2PromotionByPromoCodeAfter2014StartDate;
    }

    PromoCode promoCodeForO2PromotionAfter2014StartDate(){
        promoCodeForO2PromotionAfter2014StartDate = new PromoCode().withCode(promotionCode).withPromotion(o2PromotionByPromoCodeAfter2014StartDate).withMediaType(VIDEO_AND_AUDIO);
        return promoCodeForO2PromotionAfter2014StartDate;
    }

    PromoCode promoCodeForO2PromotionBefore2014EndDate() {
        promoCodeForO2PromotionBefore2014EndDate = new PromoCode().withCode(promotionCode).withPromotion(o2PromotionByPromoCodeBefore2014EndDate).withMediaType(VIDEO_AND_AUDIO);
        return promoCodeForO2PromotionBefore2014EndDate;
    }

    Promotion o2PromotionByPromoCodeBefore2014EndDate() {
        o2PromotionByPromoCodeBefore2014EndDate = new Promotion().withStartDate(0).withEndDate(2014).withIsActive(true).withMaxUsers(0).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription("");
        return o2PromotionByPromoCodeBefore2014EndDate;
    }

    void validateAsReturnedPromotionBefore2014() {
        assertNotNull(activePromoCodePromotion);
        assertEquals(o2PromotionByPromoCodeBefore2014EndDate.getI(), activePromoCodePromotion.getI());
    }

    void validateAsReturnedPromotionAfter2014StartDate(){
        assertNotNull(activePromoCodePromotion);
        assertEquals(o2PromotionByPromoCodeAfter2014StartDate.getI(), activePromoCodePromotion.getI());
    }

    void validateAsDoNotReturnedPromotion(){
        assertNull(activePromoCodePromotion);
    }

    void saved(Promotion promotion){
        promotionRepository.save(promotion);
    };

    void saved(PromoCode promoCode){
        promoCodeRepository.save(promoCode);
    };
}
