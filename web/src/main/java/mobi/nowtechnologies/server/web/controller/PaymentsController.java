package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.subscription.PaymentPageData;
import mobi.nowtechnologies.server.web.subscription.SubscriptionState;
import mobi.nowtechnologies.server.web.subscription.SubscriptionStateFactory;
import mobi.nowtechnologies.server.web.subscription.SubscriptionTexts;
import mobi.nowtechnologies.server.web.subscription.SubscriptionTextsGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Controller
public class PaymentsController extends CommonController {
    private static final String PAYMENTS_NOTE_MSG_CODE = "pays.page.h1.options.note";
    private static final String PAYMENTS_HEADER_MSG_CODE = "pays.page.h1.options";
    
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
    private PromotionService promotionService;

    protected ModelAndView getManagePaymentsPage(String viewName, String communityUrl, Locale locale) {
        User user = userService.findById(getUserId());
        Community community = communityService.getCommunityByUrl(communityUrl);

        ModelAndView mav = new ModelAndView(viewName);

        List<PaymentPolicyDto> paymentPolicies = getPaymentPolicy(user, checkNotNull(community), user.getSegment(), user.getOperator());
        mav.addObject("paymentPolicies", paymentPolicies);

        mav.addObject("isO2Consumer", user.isO2Consumer());
        PaymentDetails paymentDetails = user.getCurrentPaymentDetails();
        mav.addObject("paymentDetails", paymentDetails);
        PaymentPolicy activePolicy = paymentDetails != null ? paymentDetails.getPaymentPolicy() : null;
        mav.addObject("activePolicy", activePolicy);
        //TODO: move logic to controller <c:if test="${paymentDetails != null && activePolicy != null && paymentDetails.activated && activePolicy.subcost == paymentPolicy.subcost && activePolicy.subweeks == paymentPolicy.subweeks}">
        
        mav.addObject("paymentPoliciesNote", paymentsMessage(locale, user, PAYMENTS_NOTE_MSG_CODE));
        mav.addObject("paymentPoliciesHeader", paymentsMessage(locale, user, PAYMENTS_HEADER_MSG_CODE));
        mav.addObject("mobilePhoneNumber", user.getMobile());
        
        boolean userIsOptedInToVideo = user.is4G() && user.isVideoFreeTrialHasBeenActivated();
        
        mav.addObject("userIsOptedInToVideo", userIsOptedInToVideo);
        mav.addObject("userCanGetVideo", user.is4G());
        
        SubscriptionState subscriptionState = new SubscriptionStateFactory().getInstance(user);
        SubscriptionTexts subscriptionTexts = new SubscriptionTextsGenerator(messageSource, locale).generate(subscriptionState);
        
        PaymentPageData paymentPageData = new PaymentPageData(subscriptionState, subscriptionTexts);
        paymentPageData.setAppleIOSNonO2Business(user.isIOSDevice() && !(user.isO2Business()));
        mav.addObject("paymentPageData", paymentPageData);

        String paymentType = null;
        if ( paymentDetails != null ) {
        	if ( PaymentDetails.PAYPAL_TYPE.equalsIgnoreCase(paymentDetails.getPaymentType()) ) {
        		paymentType = "paypal";
        	} else if ( PaymentDetails.SAGEPAY_CREDITCARD_TYPE.equalsIgnoreCase( paymentDetails.getPaymentType()) ) {
        		paymentType = "creditcard";
        	}
        }
        mav.addObject("paymentDetailsType", paymentType);

        PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = paymentDetailsByPaymentDto(user);
        mav.addObject(PaymentDetailsByPaymentDto.NAME, paymentDetailsByPaymentDto);
        mav.addObject("showTwoWeeksPromotion", userIsLimitedAndPromotionIsActive(user, community));

        return mav;
    }

    private List<PaymentPolicyDto> getPaymentPolicy(User user, Community community, SegmentType segment, int operator2) {
        List<PaymentPolicyDto> paymentPolicy;
        
        if(user.isnonO2User()) {
            paymentPolicy = paymentDetailsService.getPaymentPolicyWithOutSegment(community, user);
        } else {
            paymentPolicy = paymentDetailsService.getPaymentPolicy(community, user, segment);
            paymentPolicy = filterPaymentPoliciesForUser(paymentPolicy, user);
        }
        
        if(isEmpty(paymentPolicy)) {
            return Collections.emptyList();
        }
        
        return paymentPolicy;
    }
    
    /**
     * For 3G users we'll only display 3G payment options, for 4G users, we'll display only 4G payment options
     */
    private List<PaymentPolicyDto> filterPaymentPoliciesForUser(List<PaymentPolicyDto> paymentPolicyList, User user) {
    	List<PaymentPolicyDto> ret = new ArrayList<PaymentPolicyDto>();
    	
    	if ( paymentPolicyList == null || user == null ) {
    		return ret;
    	}
    	
    	if(user.isO2Business()){     		
    		//no filtering required
    		ret.addAll(paymentPolicyList);
    		return ret;
    	}
    	
    	for ( PaymentPolicyDto pp : paymentPolicyList ) {
    		if ( user.is3G() && pp.isThreeG() ) {
    			ret.add( pp );
    		} else if ( user.is4G() && pp.isFourG() ) {
    			if ( !pp.isVideoAndAudio4GSubscription() || (pp.isVideoAndAudio4GSubscription() && user.isVideoFreeTrialHasBeenActivated()) ) {
    				ret.add( pp );
    			}
    		}
    	}
    	
    	return ret;
    }

    private PaymentDetailsByPaymentDto paymentDetailsByPaymentDto(User user) {
        if (!user.isIOsnonO2ItunesSubscribedUser()) {
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
    			codes[2] = msgCodeBase + "." + user.getProvider();
    			if (user.getSegment() != null) {
    				codes[1] = codes[2] + "." + user.getSegment();
    				if (user.getContract() != null) {
    					codes[0] = codes[1] + "." + user.getContract();
    				}
    			}
    		}

            paymentsNoteMsg = getFirstSutableMessage(locale, codes);
        } else {
            if (user.isIOsnonO2ItunesSubscribedUser())
                paymentsNoteMsg = message(locale, msgCodeBase+".not.o2.inapp.subs");
            else
                paymentsNoteMsg = message(locale, msgCodeBase);
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
    
    private boolean userIsLimitedAndPromotionIsActive(User user, Community community) {
    	if ( user.isLimited() ) {
    		
			Promotion twoWeeksTrial = promotionService.getActivePromotion(PromotionService.PROMO_CODE_FOR_FREE_TRIAL_BEFORE_SUBSCRIBE, community.getName());
			long now = System.currentTimeMillis();
			int dbSecs = (int)(now / 1000); // in db we keep time in seconds not milliseconds
			if ( twoWeeksTrial != null && twoWeeksTrial.getStartDate() < dbSecs && dbSecs < twoWeeksTrial.getEndDate() ) {
				return true;
			}
    	}
    	
    	return false;
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

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}
}
