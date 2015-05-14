package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.web.service.impl.UserDetailsImpl;
import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.PAY_PAL;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.GOOGLE_PLUS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;
import static mobi.nowtechnologies.server.web.controller.PaymentsPayPalController.REQUEST_PARAM_PAYPAL_PAYMENT_POLICY;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import org.junit.*;
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
        User user = findUser(101);

        SecurityContextHolder.setContext(createSecurityContext(user));

        PaymentPolicy payPalPolicy = createPayPalPolicy(user);
        paymentPolicyRepository.save(payPalPolicy);

        mockMvc.perform(get("/payments_inapp/startPayPal.html").cookie(new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl))).andExpect(status().isMovedTemporarily())
               .andExpect(redirectedUrl("/payments_inapp/paypal.html?" +
                                        REQUEST_PARAM_PAYPAL_PAYMENT_POLICY + "=" + payPalPolicy.getId()));
    }

    private PaymentPolicy createPayPalPolicy(User user) {
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.paymentPolicyWithDefaultNotNullFields();
        paymentPolicy.setTariff(_3G);
        paymentPolicy.setProvider(user.getProvider());
        paymentPolicy.setPaymentType(PAY_PAL);
        paymentPolicy.setCommunity(user.getCommunity());
        paymentPolicy.setMediaType(MediaType.AUDIO);
        return paymentPolicy;
    }

    @Test
    public void testStartPaypalWhenNoPaypalPolicy() throws Exception {
        String communityUrl = "hl_uk";

        final UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityUrl);

        final User user = UserFactory.createUser(ACTIVATED);
        user.setUserName("145645");
        user.setMobile("+447766666667");
        user.setUserGroup(userGroup);
        user.setDeviceUID("attg0vs3e98dsddc2a4k9vdkc63");

        userRepository.save(user);

        SecurityContextHolder.setContext(createSecurityContext(findUser(user.getId())));
        mockMvc.perform(get("/payments_inapp/startPayPal.html").cookie(new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl))).andExpect(status().isInternalServerError())
               .andExpect(view().name("errors/500"));
    }

    private SecurityContext createSecurityContext(User user) {
        Authentication authentication = new RememberMeAuthenticationToken("test", new UserDetailsImpl(user, true), null);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

    private User findUser(int userId) {
        User user = userRepository.findOne(userId);
        user.setProvider(GOOGLE_PLUS);
        user.setContract(PAYM);
        user.setSegment(CONSUMER);
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());
        return user;
    }
}
