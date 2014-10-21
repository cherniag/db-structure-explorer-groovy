package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;
import org.junit.Test;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.math.BigDecimal;

import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.PAY_PAL;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.PeriodUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.GOOGLE_PLUS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;
import static mobi.nowtechnologies.server.web.controller.PaymentsPayPalController.REQUEST_PARAM_PAYPAL_PAYMENT_POLICY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by Oleg Artomov on 8/18/2014.
 */
public class PaymentsPayPalControllerIT extends AbstractWebControllerIT {

    @Resource
    private UserRepository userRepository;

    @Resource
    private PaymentPolicyRepository paymentPolicyRepository;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Test
    public void testStartPaypalForUnsubscribedUser() throws Exception {
        String communityUrl = "o2";
        SecurityContextHolder.setContext(createSecurityContext(101));
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        Community o2Community = o2UserGroup.getCommunity();

        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withPeriodUnit(MONTHS)).withSubCost(new BigDecimal("4.99")
        ).withPaymentType(PAY_PAL).withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(GOOGLE_PLUS)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false)).withOnline(true);
        paymentPolicyRepository.save(paymentPolicy);
        mockMvc.perform(
                get("/payments_inapp/startPayPal.html")
                        .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))
                .andExpect(status().isMovedTemporarily()).andExpect(
                redirectedUrl("/payments_inapp/paypal.html?" +
                        REQUEST_PARAM_PAYPAL_PAYMENT_POLICY + "=" + paymentPolicy.getId()));
    }

    @Test
    public void testStartPaypalWhenNoPaypalPolicy() throws Exception {
        String communityUrl = "o2";
        SecurityContextHolder.setContext(createSecurityContext(101));
        mockMvc.perform(
                get("/payments_inapp/startPayPal.html")
                        .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))
                .andExpect(status().isInternalServerError()).andExpect(view().name("errors/500"));
    }


    private SecurityContext createSecurityContext(int userId) {
        User user = userRepository.findOne(userId);
        user.setProvider(GOOGLE_PLUS);
        user.setContract(PAYM);
        user.setSegment(CONSUMER);
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());
        Authentication authentication = new RememberMeAuthenticationToken("test", new UserDetailsImpl(user, true), null);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
