package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.asm.SubscriptionInfoAsm;
import mobi.nowtechnologies.server.web.subscription.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

import static mobi.nowtechnologies.server.persistence.domain.PromoCode.PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Controller
public class PaymentsController extends CommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsController.class);
	
	private static final String PAYMENTS_NOTE_MSG_CODE = "pays.page.h1.options.note";
    public static final String POLICY_REQ_PARAM = "paymentPolicyId";
    public static final String SCOPE_PREFIX = "{scopePrefix:payments|payments_inapp}";

    public static final String VIEW_MANAGE_PAYMENTS = "payments";
    public static final String VIEW_MANAGE_PAYMENTS_INAPP = "payments_inapp";
    public static final String PAGE_MANAGE_PAYMENTS = PATH_DELIM + VIEW_MANAGE_PAYMENTS + PAGE_EXT;
    public static final String ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + PATH_DELIM + "paymentDetails" + PATH_DELIM + "{paymentDetailsId}";
    public static final String SUCCESS_ACTIVATE_PAYMENT_DETAILS_BY_PAYMENT = SCOPE_PREFIX + "/one_click_subscription_successful.html";
    public static final String PAGE_MANAGE_PAYMENTS_INAPP = PATH_DELIM + VIEW_MANAGE_PAYMENTS_INAPP + PAGE_EXT;

    private PaymentDetailsService paymentDetailsService;
    private PaymentPolicyService paymentPolicyService;
    private UserService userService;
    private CommunityService communityService;
    private PromotionService promotionService;

    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    private SubscriptionInfoAsm subscriptionInfoAsm;

    public CommunityResourceBundleMessageSource getCommunityResourceBundleMessageSource() {
        return communityResourceBundleMessageSource;
    }

    public void setCommunityResourceBundleMessageSource(CommunityResourceBundleMessageSource communityResourceBundleMessageSource) {
        this.communityResourceBundleMessageSource = communityResourceBundleMessageSource;
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCommunityService(CommunityService communityService) {
        this.communityService = communityService;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public void setSubscriptionInfoAsm(SubscriptionInfoAsm subscriptionInfoAsm) {
        this.subscriptionInfoAsm = subscriptionInfoAsm;
    }

    protected ModelAndView getManagePaymentsPage(String viewName, String communityUrl, Locale locale, String scopePrefix) {
        User user = userService.findById(getUserId());
        Community community = communityService.getCommunityByUrl(communityUrl);
        PaymentsPage paymentsPage = new PaymentsPage();
        // the following check was added to show a static page instead of the
        // vf payment options. Once the options are enabled, the following
        // lines can be removed
        String disableVFPaymentOptions = messageSource.getMessage("pays.notimplemented.dispalypage.for.providers", null, locale);
        if (user.isVFNZCommunityUser() && displayHoldingPageForProvider(user.getProvider(), disableVFPaymentOptions)) {
        	// for vf users we display a not implemented page until the vf billing pages are done
        	LOGGER.info("Showing holding page for user [{}] with provider [{}]", user.getId(), user.getProvider());
        	return new ModelAndView(scopePrefix+"/notimplemented");
        }
        if (!paymentRequired(user, community)){
            return new ModelAndView("payments_coming_soon");
        }

        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        paymentsPage.setAwaitingPaymentStatus(calcIsAwaitingPaymentStatus(user));
        paymentsPage.setMobilePhoneNumber(user.getMobile());
        paymentsPage.setPaymentPolicies(paymentPolicyDtos);
        paymentsPage.setConsumerUser(isConsumerUser(user));
        paymentsPage.setPaymentDetails(user.getCurrentPaymentDetails());
        paymentsPage.setPaymentPoliciesNote(paymentsMessage(locale, user, PAYMENTS_NOTE_MSG_CODE));
        paymentsPage.setUserCanGetVideo(user.is4G());
        paymentsPage.setUserIsOptedInToVideo(user.is4G() && user.isVideoFreeTrialHasBeenActivated());
        paymentsPage.setAppleIOSAndNotBusiness(user.isIOSDevice() && !(isBusinessUser(user)));
        paymentsPage.setFreeTrialPeriod(user.isOnFreeTrial());
        paymentsPage.setSubscriptionInfo(subscriptionInfoAsm.createSubscriptionInfo(user, paymentPolicyDtos));
        SubscriptionState subscriptionState = new SubscriptionStateFactory().getInstance(user);
        SubscriptionTexts subscriptionTexts = new SubscriptionTextsGenerator(messageSource, locale).generate(subscriptionState);
        PaymentPageData paymentPageData = new PaymentPageData(subscriptionState, subscriptionTexts);
        
        paymentsPage.setPaymentPageData( paymentPageData );

        ModelAndView mav = new ModelAndView(viewName);

        PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsByPaymentDto(user);
        mav.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);
        mav.addObject("showTwoWeeksPromotion", userIsLimitedAndPromotionIsActive(user, community));
        mav.addObject("paymentsPage", paymentsPage);

        return mav;
    }

    private boolean paymentRequired(User user, Community community) {
        final String allDevicesKey = "web.portal.payment.enabled";

        boolean paymentEnabled = communityResourceBundleMessageSource.readBoolean(community.getRewriteUrlParameter(), allDevicesKey, true);

        if(paymentEnabled) {
            final String perDeviceKey = allDevicesKey + "." + user.getDeviceType().getName();
            return communityResourceBundleMessageSource.readBoolean(community.getRewriteUrlParameter(), perDeviceKey, true);
        }

        return false;
    }

    private boolean calcIsAwaitingPaymentStatus(User user) {
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        if(currentPaymentDetails != null) {
            return currentPaymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
        }

        return false;
    }

    private boolean displayHoldingPageForProvider(ProviderType provider, String message) {
    	if ( provider == null || message == null || message.trim().isEmpty() ) {
    		return false;
    	}
    	String[] allProvidersFromMessage = message.split(",");
    	for ( String providerToSkip : allProvidersFromMessage ) {
    		if ( providerToSkip != null && providerToSkip.trim().equalsIgnoreCase(provider.getKey()) ) {
    			return true;
    		}
    	}
    	
    	return false;
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

        return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS, communityUrl, locale, VIEW_MANAGE_PAYMENTS);
    }

    @RequestMapping(value = {PAGE_MANAGE_PAYMENTS_INAPP}, method = RequestMethod.GET)
    public ModelAndView getManagePaymentsPageInApp(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityUrl, Locale locale) {
    	
        return getManagePaymentsPage(VIEW_MANAGE_PAYMENTS_INAPP, communityUrl, locale, VIEW_MANAGE_PAYMENTS_INAPP);
    }
    
    private boolean isConsumerUser(User user) {
    	return (user.isO2Consumer() && user.getSegment() == SegmentType.CONSUMER) || user.isVFNZUser();
    }
    
    private boolean isBusinessUser(User u) {
    	return u.isO2Business(); // for VF NZ we don't have business users, so always will be false
    }

    private PaymentDetailsByPaymentDto paymentDetailsByPaymentDto(User user) {
        if (!user.isIOsNonO2ITunesSubscribedUser()) {
            return paymentDetailsService.getPaymentDetailsTypeByPayment(user.getId());
        }
        return null;
    }

    private String paymentsMessage(Locale locale, User user, String msgCodeBase) {
        String paymentsNoteMsg;
        if (user.isO2User()) {
        	String[] codes = new String[4];
    		codes[3] = msgCodeBase;
    		if (user.getProvider() != null) {
    			codes[2] = msgCodeBase + "." + user.getProvider().getKey();
    			if (user.getSegment() != null) {
    				codes[1] = codes[2] + "." + user.getSegment();
    				if (user.getContract() != null) {
    					codes[0] = codes[1] + "." + user.getContract();
    				}
    			}
    		}

            paymentsNoteMsg = getFirstSutableMessage(locale, codes);
        } else {
            if (user.isIOsNonO2ITunesSubscribedUser())
                paymentsNoteMsg = message(msgCodeBase+".not.o2.inapp.subs", null, locale);
            else
                paymentsNoteMsg = message(msgCodeBase, null, locale);
        }
        return paymentsNoteMsg;
    }

    private String getFirstSutableMessage(Locale locale, String... codes) {
        for (String code : codes) {
        	if(code != null){        		
        		String msg = messageSource.getMessage(code, null, "", locale);
        		if (isNotEmpty(msg))
        			return msg;
        	}
        }
        return "";
    }

    private String message(String messageCode, Object[] args, Locale locale) {
        return messageSource.getMessage(messageCode, args, "",  locale);
    }

    private boolean userIsLimitedAndPromotionIsActive(User user, Community community) {
    	if ( user.isLimited() ) {
    		
			Promotion twoWeeksTrial = promotionService.getActivePromotion(PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE, community.getName());
			long now = System.currentTimeMillis();
			int dbSecs = (int)(now / 1000); // in db we keep time in seconds not milliseconds
			if ( twoWeeksTrial != null && twoWeeksTrial.getStartDate() < dbSecs && dbSecs < twoWeeksTrial.getEndDate() ) {
				return true;
			}
    	}
    	
    	return false;
    }
}
