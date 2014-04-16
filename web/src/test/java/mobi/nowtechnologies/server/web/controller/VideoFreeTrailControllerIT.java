package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PromoCodeRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration("classpath:web-root-test.xml"),
        @ContextConfiguration("classpath:web-test.xml")
})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class VideoFreeTrailControllerIT {

    @Resource
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Resource
    private UserRepository userRepository;

    @Resource(name = "service.UserService")
    private UserService userService;

    @Resource
    private PromotionRepository promotionRepository;

    @Resource
    private PromoCodeRepository promoCodeRepository;

    private User user;

    @Test
    public void testActivateVideoFreeTrial()
            throws Exception {
        String communityUrl = "o2";
        user = userRepository.findOne(101);
        assertTrue(user.getPaymentDetailsList().isEmpty());
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        user.setTariff(Tariff._4G);
        user.setContract(PAYM);
        user.setSegment(SegmentType.CONSUMER);
        user.setVideoFreeTrialHasBeenActivated(false);
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());
        userService.updateUser(user);

        int now = (int)(System.currentTimeMillis()/1000);
        Promotion promotion = new Promotion();
        promotion.setEndDate(now + 10000000);
        promotion.setStartDate(now - 10000000);
        promotion.setUserGroup(user.getUserGroup());
        promotion.setMaxUsers(30);
        promotion.setNumUsers(1);
        promotion.setIsActive(true);
        promotion.setType(Promotion.ADD_FREE_WEEKS_PROMOTION);
        promotion = promotionRepository.save(promotion);

        PromoCode promoCode = new PromoCode();
        promoCode.setCode("o2.consumer.4g.paym.direct");
        promoCode.setMediaType(MediaType.VIDEO_AND_AUDIO);
        promoCode.setPromotion(promotion);
        promoCode = promoCodeRepository.save(promoCode);
        promotion.setPromoCode(promoCode);
        SecurityContextHolder.setContext(createSecurityContext());
        mockMvc.perform(
                post("/videotrial.html")
                        .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)})
        )
                .andExpect(status().isOk());
        user = userRepository.findOne(user.getId());
        assertFalse(user.getPaymentDetailsList().isEmpty());
    }


    @Before
    public void setUp()
            throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    private SecurityContext createSecurityContext() {
        Authentication authentication = new RememberMeAuthenticationToken("test", new UserDetailsImpl(user, true), null);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
