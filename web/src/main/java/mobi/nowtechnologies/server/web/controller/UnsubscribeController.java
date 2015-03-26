package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.asm.SubscriptionInfoAsm;
import mobi.nowtechnologies.server.web.validator.UnsubscribeValidator;
import static mobi.nowtechnologies.server.web.controller.PaymentsController.SCOPE_PREFIX;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UnsubscribeController extends CommonController {

    public static final String UNSUBSCRIBE_BY_PAY_PAL_HTML = "/unsubscribeByPayPal.html";
    public static final String PAGE_UNSUBSCRIBE_BY_PAYPAL = SCOPE_PREFIX + UNSUBSCRIBE_BY_PAY_PAL_HTML;
    public static final String REDIRECT_UNSUBSCRIBE_BY_PAY_PAL_HTML = "redirect:/payments_inapp" + UNSUBSCRIBE_BY_PAY_PAL_HTML;

    private UserService userService;

    @Resource
    private UserRepository userRepository;
    @Resource
    private PaymentPolicyService paymentPolicyService;
    @Resource
    private SubscriptionInfoAsm subscriptionInfoAsm;

    @InitBinder(UnsubscribeDto.NAME)
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new UnsubscribeValidator());
    }

    @RequestMapping(value = SCOPE_PREFIX + "/unsubscribe.html", method = RequestMethod.GET)
    public ModelAndView getUnsubscribePage(@PathVariable("scopePrefix") String scopePrefix) {
        ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribe");
        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        PaymentPolicyDto currentPaymentPolicy = subscriptionInfoAsm.getCurrentPaymentPolicy(user);
        modelAndView.addObject("currentPaymentPolicy", currentPaymentPolicy);
        modelAndView.addObject(UnsubscribeDto.NAME, new UnsubscribeDto());
        return modelAndView;
    }

    @RequestMapping(value = SCOPE_PREFIX + "/unsubscribeConfirmation.html", method = RequestMethod.GET)
    public ModelAndView getUnsubscribeConfirmationPage(@PathVariable("scopePrefix") String scopePrefix) {
        return new ModelAndView(scopePrefix + "/unsubscribeConfirmation");
    }

    @RequestMapping(value = PAGE_UNSUBSCRIBE_BY_PAYPAL, method = RequestMethod.GET)
    public ModelAndView getUnsubscribePageForPayPal(@PathVariable("scopePrefix") String scopePrefix) {
        ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribeByPayPal");
        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        if (user != null && user.isUnsubscribedUser()) {
            return new ModelAndView("redirect:/payments_inapp/startPayPal.html");
        }
        PaymentPolicy paymentPolicy = user.getCurrentPaymentDetails().getPaymentPolicy();
        PaymentPolicyDto paymentPolicyDto = paymentPolicyService.getPaymentPolicyDto(paymentPolicy.getId());
        if (paymentPolicyDto != null) {
            modelAndView.addObject("paymentPolicy", paymentPolicyDto);
        }
        return modelAndView;
    }

    @RequestMapping(value = SCOPE_PREFIX + "/unsubscribe.html", method = RequestMethod.POST)
    public ModelAndView unsubscribe(HttpServletRequest request, @PathVariable("scopePrefix") String scopePrefix, @Valid @ModelAttribute(UnsubscribeDto.NAME) UnsubscribeDto dto, BindingResult result,
                                    @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {

        ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribe");

        if (result.hasErrors()) {
            modelAndView.addObject("result", "fail");
        } else {
            userService.unsubscribeUser(getSecurityContextDetails().getUserId(), dto);
            modelAndView.addObject("result", "successful");
        }
        return modelAndView;
    }

    @RequestMapping(value = SCOPE_PREFIX + "/unsubscribeAndRedirect.html", method = RequestMethod.POST)
    public ModelAndView unsubscribeAndRedirect(@PathVariable("scopePrefix") String scopePrefix, @Valid @ModelAttribute(UnsubscribeDto.NAME) UnsubscribeDto dto, BindingResult result) {
        User user = userRepository.findOne(getSecurityContextDetails().getUserId());
        PaymentPolicyDto currentPaymentPolicy = subscriptionInfoAsm.getCurrentPaymentPolicy(user);
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribe");
            modelAndView.addObject("result", "fail");
            modelAndView.addObject("currentPaymentPolicy", currentPaymentPolicy);
            return modelAndView;
        } else {
            userService.unsubscribeUser(getSecurityContextDetails().getUserId(), dto);
            ModelAndView modelAndView = new ModelAndView(scopePrefix + "/redirectAfterUnsubscribe");
            modelAndView.addObject("currentPaymentPolicy", currentPaymentPolicy);
            return modelAndView;
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}