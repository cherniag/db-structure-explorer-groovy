package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;
import org.junit.Test;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class PaymentsControllerIT extends AbstractWebControllerIT{


    @Resource
    private UserRepository userRepository;

    @Test
    public void testGetManagePaymentsPage_nonO2User_Successful()
            throws Exception {
        String communityUrl = "o2";

        SecurityContextHolder.setContext(createSecurityContext(107));

        ResultActions resultActions = mockMvc.perform(
                get("/payments.html")
                        .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))
                .andExpect(status().isOk()).andDo(print());

        ModelAndView modelAndView = resultActions.andReturn().getModelAndView();
        ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();

        String viewName = modelAndView.getViewName();
        PaymentsPage paymentsPage = (PaymentsPage)modelMap.get("paymentsPage");
        List<PaymentPolicyDto> paymentPolicies = paymentsPage.getPaymentPolicies();

        assertEquals("payments", viewName);
        assertEquals(5, paymentPolicies.size());
    }

    @Test
    public void testGetManagePaymentsPage_O2UserDTB_Successful()
            throws Exception {
        String communityUrl = "o2";

        SecurityContextHolder.setContext(createSecurityContext(101));

        ResultActions resultActions = mockMvc.perform(
                get("/payments.html")
                        .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))
                .andExpect(status().isOk());

        ModelAndView modelAndView = resultActions.andReturn().getModelAndView();
        ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();


        String viewName = modelAndView.getViewName();
        PaymentsPage paymentsPage = (PaymentsPage)modelMap.get("paymentsPage");
        List<PaymentPolicyDto> paymentPolicies = paymentsPage.getPaymentPolicies();

        assertEquals("payments", viewName);
        assertEquals(5, paymentPolicies.size());
    }

    @Test
    public void testGetManagePaymentsPage_O2UserNonDTB_Successful()
            throws Exception {
        String communityUrl = "o2";

        SecurityContextHolder.setContext(createSecurityContext(102));

        ResultActions resultActions = mockMvc.perform(
                get("/payments.html")
                        .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)}))
                .andExpect(status().isOk());

        ModelAndView modelAndView = resultActions.andReturn().getModelAndView();
        ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();

        String viewName = modelAndView.getViewName();
        PaymentsPage paymentsPage = (PaymentsPage)modelMap.get("paymentsPage");
        List<PaymentPolicyDto> paymentPolicies = paymentsPage.getPaymentPolicies();


        assertEquals("payments", viewName);
        assertEquals(5, paymentPolicies.size());
    }


    @Test
    public void testPaymentPageUnavailableForVFNZ() throws Exception {
        String communityUrl = "vf_nz";
        SecurityContextHolder.setContext(createSecurityContext(110));
        mockMvc.perform(get("/payments.html") .cookie(new Cookie[]{new Cookie(DEFAULT_COMMUNITY_COOKIE_NAME, communityUrl)})).
                andExpect(view().name("payments_coming_soon"));
    }

    private SecurityContext createSecurityContext(int userId) {
        User user = userRepository.findOne(userId);
        if ( userId == 101 ) {
            user.setProvider(O2);
            user.setContract(PAYM);
            user.setSegment(CONSUMER);
        }
        user.setFreeTrialExpiredMillis(System.currentTimeMillis());
        Authentication authentication = new RememberMeAuthenticationToken("test", new UserDetailsImpl(user, true), null);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}