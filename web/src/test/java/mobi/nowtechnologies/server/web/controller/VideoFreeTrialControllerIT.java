package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromoCodeRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.web.service.impl.UserDetailsImpl;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import org.junit.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class VideoFreeTrialControllerIT extends AbstractWebControllerIT {


    @Resource
    private UserRepository userRepository;

    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private PromotionRepository promotionRepository;

    @Resource
    private PromoCodeRepository promoCodeRepository;

    private User user;

    @Test
    public void testActivateVideoFreeTrial() throws Exception {
        String communityUrl = "o2";
        user = userRepository.findOne(101);
        assertTrue(paymentDetailsRepository.findPaymentDetailsByOwner(user).isEmpty());
        user.setActivationStatus(ACTIVATED);
        user.setTariff(_4G);
        user.setContract(PAYM);
        user.setSegment(CONSUMER);
        user.setVideoFreeTrialHasBeenActivated(false);
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());

        Promotion lastPromotion = promotionRepository
            .save(new Promotion().withStartDate(0).withEndDate(2014).withIsActive(true).withMaxUsers(5).withType(ADD_FREE_WEEKS_PROMOTION).withUserGroup(user.getUserGroup()).withDescription(""));
        PromoCode lastPromoCode = promoCodeRepository.save(new PromoCode().withMediaType(AUDIO).withPromotion(lastPromotion).withCode(""));

        userService.updateUser(user.withLastPromo(lastPromoCode));

        int now = (int) (System.currentTimeMillis() / 1000);
        Promotion promotion = new Promotion();
        promotion.setEndDate(now + 10000000);
        promotion.setStartDate(now - 10000000);
        promotion.setUserGroup(user.getUserGroup());
        promotion.setMaxUsers(30);
        promotion.setNumUsers(1);
        promotion.setIsActive(true);
        promotion.setType(ADD_FREE_WEEKS_PROMOTION);
        promotion = promotionRepository.save(promotion);

        PromoCode promoCode = new PromoCode();
        promoCode.setCode("o2.consumer.4g.paym.direct");
        promoCode.setMediaType(VIDEO_AND_AUDIO);
        promoCode.setPromotion(promotion);
        promoCode = promoCodeRepository.save(promoCode);
        promotion.setPromoCode(promoCode);
        SecurityContextHolder.setContext(createSecurityContext());
        mockMvc.perform(post("/videotrial.html").cookie(new Cookie[] {new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)})).andExpect(status().isOk());
        user = userRepository.findOne(user.getId());
        assertFalse(paymentDetailsRepository.findPaymentDetailsByOwner(user).isEmpty());
    }


    private SecurityContext createSecurityContext() {
        Authentication authentication = new RememberMeAuthenticationToken("test", new UserDetailsImpl(user, true), null);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
