package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Controller
public class PaymentsController extends CommonController {
    private static final String PAYMENTS_NOTE_MSG_CODE = "pays.page.h1.options.note";
    
    public static final String POLICY_REQ_PARAM = "paymentPolicyId";

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsController.class);

    public static final String SCOPE_PREFIX = "{scopePrefix:payments|payments_inapp}";

    public static final String VIEW_MANAGE_PAYMENTS = "payments";
    public static final String VIEW_MANAGE_PAYMENTS_INAPP = "payments_inapp";
    public static final String PAGE_MANAGE_PAYMENTS = PATH_DELIM + VIEW_MANAGE_PAYMENTS + PAGE_EXT;
    public static final String ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + PATH_DELIM + "paymentDetails" + PATH_DELIM + "{paymentDetailsId}";
    public static final String SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + "/one_click_subscription_successful.html";
    public static final String PAGE_MANAGE_PAYMENTS_INAPP = PATH_DELIM + VIEW_MANAGE_PAYMENTS_INAPP + PAGE_EXT;

    private PaymentDetailsService paymentDetailsService;

    private UserService userService;
    private CommunityService communityService;

    protected ModelAndView getManagePaymentsPage(String viewName, String communityUrl, Locale locale) {
        User user = userService.findById(getUserId());
        Community community = communityService.getCommunityByUrl(communityUrl);

        ModelAndView mav = new ModelAndView(viewName);

        List<PaymentPolicyDto> paymentPolicies = getPaymentPolicy(user, checkNotNull(community), user.getSegment(), user.getOperator());
        mav.addObject("paymentPolicies", paymentPolicies);

        mav.addObject("nonIOSDevice", !user.isIOSDevice())
        .addObject("isIOSDevice", user.isIOSDevice())
        .addObject("trialExpiredOrLimited", user.isTrialExpired() || user.isLimited())
        .addObject("isO2User", user.isO2User());
        PaymentDetails paymentDetails = user.getCurrentPaymentDetails();
        mav.addObject("paymentDetails", paymentDetails);
        String accountNotesMsgCode = getMessageCodeForAccountNotes(user);
        PaymentPolicy activePolicy = paymentDetails != null ? paymentDetails.getPaymentPolicy() : null;
        mav.addObject("activePolicy", activePolicy);
        mav.addObject("paymentAccountNotes", message(locale, accountNotesMsgCode));
        mav.addObject("paymentAccountBanner", message(locale, accountNotesMsgCode + ".img"));
        mav.addObject("paymentPoliciesNote", paymentsNoteMessage(locale, user));

        PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsByPaymentDto(user);
        mav.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);

        return mav;
    }

    private List<PaymentPolicyDto> getPaymentPolicy(User user, Community community, SegmentType segment, int operator) {
        List<PaymentPolicyDto> paymentPolicy;
        if(user.isnonO2User())
            paymentPolicy = paymentDetailsService.getPaymentPolicyWithOutSegment(community, user);
        else
            paymentPolicy = paymentDetailsService.getPaymentPolicy(community, user, segment);
        if(isEmpty(paymentPolicy))
            return Collections.emptyList();
        return paymentPolicy;
    }

    private PaymentDetailsByPaymentDto paymentDetailsByPaymentDto(User user) {
        if (!user.isIOsnonO2ItunesSubscribedUser()) {
            return paymentDetailsService.getPaymentDetailsTypeByPayment(user.getId());
        }
        return null;
    }

    private String paymentsNoteMessage(Locale locale, User user) {
        String paymentsNoteMsg;
        if (user.isO2User()) {
            String code_1 = PAYMENTS_NOTE_MSG_CODE + "." + user.getProvider() + "." + user.getContract();
            String code_2 = PAYMENTS_NOTE_MSG_CODE + "." + user.getProvider();

            paymentsNoteMsg = getFirstSutableMessage(locale, code_1, code_2, PAYMENTS_NOTE_MSG_CODE);
        } else {
            if (user.isIOsnonO2ItunesSubscribedUser())
                paymentsNoteMsg = message(locale, "pays.page.h1.options.note.not.o2.inapp.subs");
            else
                paymentsNoteMsg = message(locale, PAYMENTS_NOTE_MSG_CODE);
        }
        return paymentsNoteMsg;
    }

    private String getFirstSutableMessage(Locale locale, String... codes) {
        for (String code : codes) {
            String msg = messageSource.getMessage(code, null, "", locale);
            if (isNotEmpty(msg))
                return msg;
        }
        return "";
    }

    public String getMessageCodeForAccountNotes(User user) {
        String messageCode = "pays.page.note.account";
        if (user.isLimited())
            messageCode = "pays.page.note.account.limited";
        else if (user.isOnFreeTrial())
            messageCode = "pays.page.note.account.freetrial";
        else if (user.isSubscribed())
            messageCode = "pays.page.note.account.subscribed";
        else if (user.isSubscribedViaInApp())
            messageCode = "pays.page.note.account.subscribed_via_inapp";
        else if (user.isTrialExpired())
            messageCode = "pays.page.note.account.trial_expired";
        else if (user.isUnsubscribedWithFullAccess())
            messageCode = "pays.page.note.account.unsubscribed_with_full_access";

        return messageCode;
    }

    private String message(Locale locale, String messageCode) {
        return messageSource.getMessage(messageCode, null, "",  locale);
    }

    @RequestMapping(value = {ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT}, method = RequestMethod.POST)
    public ModelAndView activatePaymentDetailsByPayment(@PathVariable("scopePrefix") String scopePrefix, @PathVariable("paymentDetailsId") Long paymentDetailsId) {
        LOGGER.debug("input parameters paymentDetailsId: [{}]", paymentDetailsId);

        paymentDetailsService.activatePaymentDetailsByPayment(paymentDetailsId);

        ModelAndView modelAndView = new ModelAndView("redirect:/" + scopePrefix + "/one_click_subscription_successful.html");
        LOGGER.debug("Output parameter [{}]", modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = {SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT}, method = RequestMethod.GET)
    public ModelAndView getOneClickSubscriptionSuccessfulPage(@PathVariable("scopePrefix") String scopePrefix) {

        final int userId = getSecurityContextDetails().getUserId();
        PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsService.getPaymentDetailsTypeByPayment(userId);

        final ModelAndView modelAndView;
        if (paymentDetailsByPaymentDto == null || !paymentDetailsByPaymentDto.isActivated()) {
            modelAndView = new ModelAndView("redirect:account.html");
        } else {
            modelAndView = new ModelAndView(scopePrefix + "/one_click_subscription_successful");
            modelAndView.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);
        }

        LOGGER.debug("Output parameter [{}]", modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = {PAGE_MANAGE_PAYMENTS}, method = RequestMethod.GET)
    public ModelAndView getManagePaymentsPage(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl, Locale locale) {
        LOGGER.info("Request for [{}] with communityUrl [{}], locale [{}]", PAGE_MANAGE_PAYMENTS, communityUrl, locale);

        return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS, communityUrl, locale);
    }

    @RequestMapping(value = {PAGE_MANAGE_PAYMENTS_INAPP}, method = RequestMethod.GET)
    public ModelAndView getManagePaymentsPageInApp(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl, Locale locale) {
    	
        return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS_INAPP, communityUrl, locale);
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }
}
