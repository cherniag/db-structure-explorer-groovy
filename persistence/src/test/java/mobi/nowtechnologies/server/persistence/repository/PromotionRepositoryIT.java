package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_SUBBALANCE_PROMOTION;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

import javax.annotation.Resource;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;

/**
 * User: Titov Mykhaylo (titov) 02.08.13 15:20
 */


public class PromotionRepositoryIT extends AbstractRepositoryIT {

    @Resource
    PromotionRepository promotionRepository;

    @Resource
    PromoCodeRepository promoCodeRepository;

    @Resource
    CommunityRepository communityRepository;

    @Resource
    UserGroupRepository userGroupRepository;

    private String promotionCode;

    private PromoCode promoCode;
    private UserGroup o2UserGroup;
    private Promotion activePromoCodePromotion;
    private Promotion o2PromotionByPromoCodeBefore2014EndDate;
    private PromoCode promoCodeForO2PromotionBefore2014EndDate;
    private PromoCode promoCodeForO2PromotionAfter2014StartDate;
    private Promotion o2PromotionByPromoCodeAfter2014StartDate;

    @Before
    public void setUp() {
        Community o2Community = communityRepository.findByRewriteUrlParameter("o2");

        o2UserGroup = userGroupRepository.findByCommunity(o2Community);
    }


    @Test
    public void shouldReturnDeactivatedPromotion() {
        promotionCode = "code";

        saved(o2PromotionByPromoCodeBefore2014EndDate().withIsActive(false));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        activePromoCodePromotion = promotionRepository.findPromotionByPromoCode(promotionCode, o2UserGroup, ADD_FREE_WEEKS_PROMOTION);
        Assert.assertNotNull(activePromoCodePromotion);
        Assert.assertEquals(activePromoCodePromotion.getI(), o2PromotionByPromoCodeBefore2014EndDate.getI());
    }

    @Test
    public void shouldReturnPromotionInPast() {
        promotionCode = "code";

        saved(o2PromotionByPromoCodeBefore2014EndDate().withIsActive(false).withEndDate(2000));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        activePromoCodePromotion = promotionRepository.findPromotionByPromoCode(promotionCode, o2UserGroup, ADD_FREE_WEEKS_PROMOTION);
        Assert.assertNotNull(activePromoCodePromotion);
        Assert.assertEquals(activePromoCodePromotion.getI(), o2PromotionByPromoCodeBefore2014EndDate.getI());
    }

    private Promotion createDefaultO2Promotion() {
        return new Promotion().withStartDate(0).withEndDate(2014).withIsActive(false).withNumUsers(5).withMaxUsers(0).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription("");
    }

    @Test
    public void shouldDoNotReturnNotActivePromotion() {
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate().withIsActive(false));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnLimitedPromotion() {
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate().withNumUsers(1).withMaxUsers(1));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnExpiredPromotion() {
        //given
        promotionCode = "code";
        int currentTime2015Seconds = 2015;

        saved(o2PromotionByPromoCodeBefore2014EndDate());
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTime2015Seconds, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnAddSubBalancePromotion() {
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate().withType(ADD_SUBBALANCE_PROMOTION));
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldDoNotReturnNotStartedPromotion() {
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeAfter2014StartDate());
        saved(promoCodeForO2PromotionAfter2014StartDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsDoNotReturnedPromotion();
    }

    @Test
    public void shouldReturnPromotionBefore2014EndDate() {
        //given
        promotionCode = "code";
        int currentTimeOneSecond = 1;

        saved(o2PromotionByPromoCodeBefore2014EndDate());
        saved(promoCodeForO2PromotionBefore2014EndDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTimeOneSecond, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsReturnedPromotionBefore2014();
    }

    @Test
    public void shouldReturnPromotionAfter2014StartDate() {
        //given
        promotionCode = "code";
        int currentTime2015Seconds = 2015;

        saved(o2PromotionByPromoCodeBefore2014EndDate());
        saved(promoCodeForO2PromotionBefore2014EndDate());

        saved(o2PromotionByPromoCodeAfter2014StartDate());
        saved(promoCodeForO2PromotionAfter2014StartDate());

        // when
        activePromoCodePromotion = promotionRepository.findActivePromoCodePromotion(promotionCode, o2UserGroup, currentTime2015Seconds, ADD_FREE_WEEKS_PROMOTION);

        //then
        validateAsReturnedPromotionAfter2014StartDate();
    }

    @Test
    public void shouldUpdatePromotionNumUsers() {
        //given
        Promotion maxUsersLimitedPromotion =
            saved(new Promotion().withStartDate(0).withEndDate(2014).withIsActive(true).withMaxUsers(5).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription(""));

        // when
        int updatedRowsCount = promotionRepository.updatePromotionNumUsers(maxUsersLimitedPromotion);

        //then
        assertThat(updatedRowsCount, is(1));
    }

    @Test
    public void shouldUpdateNotMaxUsersLimitedPromotionNumUsers() {
        //given
        Promotion maxUsersLimitedPromotion = saved(
            new Promotion().withStartDate(0).withEndDate(2014).withIsActive(true).withNumUsers(5).withMaxUsers(0).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription(""));

        // when
        int updatedRowsCount = promotionRepository.updatePromotionNumUsers(maxUsersLimitedPromotion);

        //then
        assertThat(updatedRowsCount, is(1));
    }

    @Test
    public void shouldDoNotUpdatePromotionNumUsers() {
        //given
        Promotion maxUsersLimitedPromotion = saved(
            new Promotion().withStartDate(0).withEndDate(2014).withIsActive(true).withNumUsers(5).withMaxUsers(5).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription(""));

        // when
        int updatedRowsCount = promotionRepository.updatePromotionNumUsers(maxUsersLimitedPromotion);

        //then
        assertThat(updatedRowsCount, is(0));
    }

    @Test
    public void testFindActivePromotionByUserGroup() {
        //given
        Promotion promo = new Promotion().withStartDate(0).withEndDate(Integer.MAX_VALUE).withIsActive(true).withUserGroup(o2UserGroup).withType(ADD_SUBBALANCE_PROMOTION);
        promo = saved(promo);

        // when
        Promotion promotion = promotionRepository.findActivePromotion(o2UserGroup, Promotion.ADD_SUBBALANCE_PROMOTION, DateTimeUtils.getEpochSeconds());

        //then
        assertNotNull(promotion);
        assertEquals(promo.getI(), promotion.getI());
    }

    Promotion o2PromotionByPromoCodeAfter2014StartDate() {
        o2PromotionByPromoCodeAfter2014StartDate =
            new Promotion().withStartDate(2014).withEndDate(2016).withIsActive(true).withMaxUsers(0).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription("");
        return o2PromotionByPromoCodeAfter2014StartDate;
    }

    PromoCode promoCodeForO2PromotionAfter2014StartDate() {
        promoCodeForO2PromotionAfter2014StartDate = new PromoCode().withCode(promotionCode).withPromotion(o2PromotionByPromoCodeAfter2014StartDate).withMediaType(VIDEO_AND_AUDIO);
        return promoCodeForO2PromotionAfter2014StartDate;
    }

    PromoCode promoCodeForO2PromotionBefore2014EndDate() {
        promoCodeForO2PromotionBefore2014EndDate = new PromoCode().withCode(promotionCode).withPromotion(o2PromotionByPromoCodeBefore2014EndDate).withMediaType(VIDEO_AND_AUDIO);
        return promoCodeForO2PromotionBefore2014EndDate;
    }

    Promotion o2PromotionByPromoCodeBefore2014EndDate() {
        o2PromotionByPromoCodeBefore2014EndDate =
            new Promotion().withStartDate(0).withEndDate(2014).withIsActive(true).withMaxUsers(0).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(o2UserGroup).withDescription("");
        return o2PromotionByPromoCodeBefore2014EndDate;
    }

    void validateAsReturnedPromotionBefore2014() {
        assertNotNull(activePromoCodePromotion);
        assertEquals(o2PromotionByPromoCodeBefore2014EndDate.getI(), activePromoCodePromotion.getI());
    }

    void validateAsReturnedPromotionAfter2014StartDate() {
        assertNotNull(activePromoCodePromotion);
        assertEquals(o2PromotionByPromoCodeAfter2014StartDate.getI(), activePromoCodePromotion.getI());
    }


    void validateAsDoNotReturnedPromotion() {
        assertNull(activePromoCodePromotion);
    }

    Promotion saved(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    void saved(PromoCode promoCode) {
        promoCodeRepository.save(promoCode);
    }

}
