package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.UnsubscribeValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static mobi.nowtechnologies.server.web.controller.PaymentsController.SCOPE_PREFIX;

@Controller
public class UnsubscribeController extends CommonController {

    public static final String UNSUBSCRIBE_BY_PAY_PAL_HTML = "/unsubscribeByPayPal.html";
    public static final String PAGE_UNSUBSCRIBE_BY_PAYPAL = SCOPE_PREFIX + UNSUBSCRIBE_BY_PAY_PAL_HTML;
    public static final String REDIRECT_UNSUBSCRIBE_BY_PAY_PAL_HTML = "redirect:/payments_inapp" + UNSUBSCRIBE_BY_PAY_PAL_HTML;


    private UserService userService;

    @Resource
    private PaymentPolicyService paymentPolicyService;

    @InitBinder(UnsubscribeDto.NAME)
    public void initBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new UnsubscribeValidator());
    }

    @RequestMapping(value = SCOPE_PREFIX + "/unsubscribe.html", method = RequestMethod.GET)
    public ModelAndView getUnsubscribePage(@PathVariable("scopePrefix") String scopePrefix) {
        ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribe");
        modelAndView.addObject(UnsubscribeDto.NAME, new UnsubscribeDto());
        return modelAndView;
    }

    @RequestMapping(value = PAGE_UNSUBSCRIBE_BY_PAYPAL, method = RequestMethod.GET)
    public ModelAndView getUnsubscribePageForPayPal(@PathVariable("scopePrefix") String scopePrefix) {
        ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribeByPayPal");
        User user = userService.findById(getSecurityContextDetails().getUserId());
        if (userService.isUnsubscribedUser(user)){
            return new ModelAndView("redirect:/payments_inapp/startPayPal.html") ;
        }
        PaymentPolicy paymentPolicy = user.getCurrentPaymentDetails().getPaymentPolicy();
        PaymentPolicyDto paymentPolicyDto = paymentPolicyService.getPaymentPolicyDto(paymentPolicy.getId());
        if (paymentPolicyDto != null){
            modelAndView.addObject("paymentPolicy", paymentPolicyDto);
        }
        return modelAndView;
    }

    @RequestMapping(value = SCOPE_PREFIX + "/unsubscribe.html", method = RequestMethod.POST)
    public ModelAndView unsubscribe(HttpServletRequest request, @PathVariable("scopePrefix") String scopePrefix,
                                    @Valid @ModelAttribute(UnsubscribeDto.NAME) UnsubscribeDto dto,
                                    BindingResult result,
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
    public ModelAndView unsubscribeAndRedirect(@PathVariable("scopePrefix") String scopePrefix,
                                               @Valid @ModelAttribute(UnsubscribeDto.NAME) UnsubscribeDto dto,
                                               BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(scopePrefix + "/unsubscribe");
            modelAndView.addObject("result", "fail");
            return modelAndView;
        } else {
            userService.unsubscribeUser(getSecurityContextDetails().getUserId(), dto);
            return new ModelAndView(scopePrefix + "/redirectAfterUnsubscribe");
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}