package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.PayPalDto;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.PAY_PAL;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.PAYPAL_TYPE;
import static mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME;
import static mobi.nowtechnologies.server.web.controller.UnsubscribeController.REDIRECT_UNSUBSCRIBE_BY_PAY_PAL_HTML;
import static org.springframework.util.Assert.notNull;

@Controller
public class PaymentsPayPalController extends CommonController {

    public static final String VIEW_PAYMENTS_PAYPAL = "/paypal";

    public static final String PAGE_PAYMENTS_PAYPAL = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_PAYPAL + PAGE_EXT;

    public static final String PAGE_PAYMENTS_START_PAYPAL = PaymentsController.SCOPE_PREFIX + "/startPayPal" + PAGE_EXT;

    public static final String REQUEST_PARAM_PAYPAL = "result";
    private static final String REQUEST_PARAM_PAYPAL_TOKEN = "token";
    public static final String REQUEST_PARAM_PAYPAL_PAYMENT_POLICY = "paymentPolicyId";

    public static final String SUCCESSFUL_RESULT = "successful";
    public static final String FAIL_RESULT = "fail";

    private PaymentDetailsService paymentDetailsService;
    private PaymentPolicyService paymentPolicyService;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
    private UserService userService;

    @RequestMapping(value = PAGE_PAYMENTS_PAYPAL, method = RequestMethod.GET)
    public ModelAndView getPayPalPage(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(value = REQUEST_PARAM_PAYPAL, required = false) String result,
                                      @RequestParam(value = REQUEST_PARAM_PAYPAL_TOKEN, required = false) String token,
                                      @RequestParam(value = REQUEST_PARAM_PAYPAL_PAYMENT_POLICY, required = true) Integer paymentPolicyId,
                                      @CookieValue(value = DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl, Locale locale) {
        ModelAndView modelAndModel = new ModelAndView(scopePrefix + VIEW_PAYMENTS_PAYPAL);

        if (StringUtils.hasText(result)) {
            if (StringUtils.hasText(token)) {
                paymentDetailsService.commitPayPalPaymentDetails(token, paymentPolicyId, communityUrl.getValue(), getSecurityContextDetails().getUserId());
            }
            modelAndModel.addObject(REQUEST_PARAM_PAYPAL, result);
            PaymentPolicyDto dto = paymentPolicyService.getPaymentPolicyDto(paymentPolicyId);
            modelAndModel.addObject("currentPaymentPolicy", dto);
        }else{
            PaymentPolicyDto paymentPolicy = paymentPolicyService.getPaymentPolicyDto(paymentPolicyId);
            modelAndModel.addObject(PaymentPolicyDto.PAYMENT_POLICY_DTO, paymentPolicy);
        }

        addModel(modelAndModel, communityUrl.getValue());

        return modelAndModel;
    }

    private void addModel(ModelAndView modelAndModel, String communityUrl) {
        boolean paymentEnabled = communityResourceBundleMessageSource.readBoolean(communityUrl, "web.portal.social.info.for.paypal.enabled", false);

        if(paymentEnabled) {
            User user = userService.getWithSocial(getSecurityContextDetails().getUserId());
            List<SocialInfo> socialInfo = new ArrayList<SocialInfo>(user.getSocialInfo());
            Assert.isTrue(!socialInfo.isEmpty(), "No social info for " + user.getId());

            //to get predictable socialInfo from set
            Collections.sort(socialInfo, new Comparator<SocialInfo>() {
                @Override
                public int compare(SocialInfo o1, SocialInfo o2) {
                    return o2.getSocialId().compareTo(o1.getSocialId());
                }
            });

            SocialInfo first = socialInfo.iterator().next();
            modelAndModel.addObject("customerName", getFormattedName(first));
            modelAndModel.addObject("customerAvatar", first.getAvatarUrl());
        }
    }

    private String getFormattedName(SocialInfo socialInfo) {
        final int maxLength = 15;
        String customerName = socialInfo.getFirstName();
        if(customerName.length() > maxLength) {
            customerName = customerName.substring(0, maxLength) + "...";
        }
        return customerName.toUpperCase();
    }

    @RequestMapping(value = PAGE_PAYMENTS_START_PAYPAL, method = RequestMethod.GET)
    public String startPaypal(@CookieValue(value = DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
        User user = userService.findById(getSecurityContextDetails().getUserId());
        if (user.isSubscribedUserByPaymentType(PAYPAL_TYPE)){
            return REDIRECT_UNSUBSCRIBE_BY_PAY_PAL_HTML;
        }
        Community community = CommunityDao.getCommunity(communityUrl.getValue());
        PaymentPolicy paymentPolicy = paymentPolicyService.getPaymentPolicy(community, user.getProvider(), PAY_PAL);
        notNull(paymentPolicy);
        return "redirect:/payments_inapp/paypal.html?"+REQUEST_PARAM_PAYPAL_PAYMENT_POLICY + "=" + paymentPolicy.getId() ;
    }

    @RequestMapping(value = PAGE_PAYMENTS_PAYPAL, method = RequestMethod.POST)
    public ModelAndView createPaymentDetails(@PathVariable("scopePrefix") String scopePrefix, HttpServletRequest request,
                                             @ModelAttribute(PayPalDto.NAME) PayPalDto dto,
                                             @CookieValue(value = DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl, Locale locale) {
        PaymentPolicyDto paymentPolicyDto = paymentPolicyService.getPaymentPolicyDto(dto.getPaymentPolicyId());
        dto.setBillingAgreementDescription(messageSource.getMessage("pay.paypal.billing.agreement.description", new Object[]{paymentPolicyDto.getDuration(), paymentPolicyDto.getDurationUnit(), paymentPolicyDto.getSubcost()}, locale));
        StringBuilder callbackUrl = new StringBuilder(RequestUtils.getServerURL()).append(PATH_DELIM).append(scopePrefix).append(VIEW_PAYMENTS_PAYPAL).append(PAGE_EXT)
                .append(START_PARAM_DELIM)
                .append(REQUEST_PARAM_PAYPAL_PAYMENT_POLICY).append("=").append(dto.getPaymentPolicyId()).append("&")
                .append(REQUEST_PARAM_PAYPAL).append("=");
        dto.setFailUrl(callbackUrl+FAIL_RESULT);
        dto.setSuccessUrl(callbackUrl + SUCCESSFUL_RESULT);
        PayPalPaymentDetails payPalPamentDetails = paymentDetailsService.createPayPalPaymentDetails(dto, communityUrl.getValue(), getSecurityContextDetails().getUserId());
        return new ModelAndView(REDIRECT + payPalPamentDetails.getBillingAgreementTxId());
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleExceptions(HttpServletRequest request, ServiceException exception, Locale locale) {
        String scopeView = request.getRequestURI().contains(PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP) ? PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP
                : PaymentsController.VIEW_MANAGE_PAYMENTS;

        ModelAndView modelAndView = new ModelAndView(scopeView + VIEW_PAYMENTS_PAYPAL);

        final String message = messageSource.getMessage(exception.getErrorCodeForMessageLocalization(), null, locale);
        if (exception instanceof ExternalServiceException)
            modelAndView.addObject("external_error", message);
        else
            modelAndView.addObject("internal_error", message);
        modelAndView.addObject(REQUEST_PARAM_PAYPAL, FAIL);

        return modelAndView;
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

    public void setCommunityResourceBundleMessageSource(CommunityResourceBundleMessageSource communityResourceBundleMessageSource) {
        this.communityResourceBundleMessageSource = communityResourceBundleMessageSource;
    }
}