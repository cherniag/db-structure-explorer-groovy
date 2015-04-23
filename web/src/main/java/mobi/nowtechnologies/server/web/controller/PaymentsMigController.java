package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.OperatorRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.MigPaymentDetailsService;
import mobi.nowtechnologies.server.service.payment.PinMigService;
import mobi.nowtechnologies.server.shared.dto.web.payment.PSmsDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.VerifyDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.PaymentsMigValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
public class PaymentsMigController extends CommonController {
    public static final String PAGE_VERIFY = "/verify.html";

    public static final String VIEW_PAYMENTS_PSMS = "/psms";
    public static final String VIEW_VERIFY_PAYMENTS_PSMS = "/psms_verify";

    public static final String PAGE_PAYMENTS_PSMS = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_PSMS + PAGE_EXT;
    public static final String PAGE_VERIFY_PAYMENTS_PSMS = PaymentsController.SCOPE_PREFIX + VIEW_PAYMENTS_PSMS + PAGE_VERIFY;
    public static final String PAGE_VERIFY_PSMS_RESEND_PIN = PaymentsController.SCOPE_PREFIX + "/pin.html";

    public static final String REDIRECT_VERIFY_PAYMENTS_PSMS = "psms" + PAGE_VERIFY;

    private PaymentDetailsService paymentDetailsService;
    private PaymentPolicyService paymentPolicyService;
    private MigPaymentDetailsService migPaymentDetailsService;
    private OperatorRepository operatorRepository;
    private PinMigService pinMigService;

    @InitBinder(PSmsDto.NAME)
    public void initBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new PaymentsMigValidator());
    }

    @RequestMapping(value = PAGE_PAYMENTS_PSMS, method = RequestMethod.GET)
    public ModelAndView getMigPaymentsPage(@PathVariable("scopePrefix") String scopePrefix, @RequestParam(PaymentsController.POLICY_REQ_PARAM) Integer policyId,
                                           @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
        PaymentPolicyDto paymentPolicy = paymentPolicyService.getPaymentPolicyDto(policyId);

        logger.info("Get MIG payments page for user id:{} and payment policy id: {}", getUserId(), paymentPolicy.getId());

        ModelAndView modelAndView = new ModelAndView(scopePrefix + VIEW_PAYMENTS_PSMS);
        modelAndView.addObject(PSmsDto.NAME, new PSmsDto());
        modelAndView.addObject("operators", paymentDetailsService.getAvailableOperators(communityUrl.getValue(), "PSMS"));
        modelAndView.addObject(PaymentPolicyDto.PAYMENT_POLICY_DTO, paymentPolicy);

        return modelAndView;
    }

    @RequestMapping(value = PAGE_PAYMENTS_PSMS, method = RequestMethod.POST)
    public ModelAndView createMigPaymentDetails(@PathVariable("scopePrefix") String scopePrefix, @Valid @ModelAttribute(PSmsDto.NAME) PSmsDto dto, BindingResult result,
                                                @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) Cookie communityUrl) {
        logger.info("Get create MIG payments details page for user id:{}", getUserId());

        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView(scopePrefix + VIEW_PAYMENTS_PSMS);
            modelAndView.addObject("operators", paymentDetailsService.getAvailableOperators(communityUrl.getValue(), "PSMS"));
            return modelAndView;
        }

        Operator operator = operatorRepository.findOne(dto.getOperator());
        migPaymentDetailsService.createPaymentDetails(getUserId(), dto.getPhone(), dto.getPaymentPolicyId(), operator);

        return new ModelAndView(REDIRECT + REDIRECT_VERIFY_PAYMENTS_PSMS);
    }

    @RequestMapping(value = PAGE_VERIFY_PAYMENTS_PSMS, method = RequestMethod.GET)
    public ModelAndView getVerifySmsPage(@PathVariable("scopePrefix") String scopePrefix) {
        logger.info("Get verify MIG payments page for user id:{}", getUserId());

        ModelAndView modelAndView = new ModelAndView(scopePrefix + VIEW_VERIFY_PAYMENTS_PSMS);
        MigPaymentDetails paymentDetails = (MigPaymentDetails) paymentDetailsService.getPendingPaymentDetails(getUserId());
        if (null != paymentDetails) {
            modelAndView.addObject(VerifyDto.NAME, new VerifyDto());
            modelAndView.addObject(PSmsDto.NAME, new PSmsDto(paymentDetails.getMigPhoneNumber(), paymentDetails.getPaymentPolicy().getOperatorId()));
        }

        return modelAndView;
    }

    @RequestMapping(value = PAGE_VERIFY_PAYMENTS_PSMS, method = RequestMethod.POST)
    public ModelAndView commitMigPaymentDetails(@PathVariable("scopePrefix") String scopePrefix, @ModelAttribute(VerifyDto.NAME) VerifyDto dto) {
        logger.info("Get commit MIG payments page for user id:{}", getUserId());

        ModelAndView modelAndView = new ModelAndView(scopePrefix + VIEW_VERIFY_PAYMENTS_PSMS);
        modelAndView.addObject(PSmsDto.NAME, new PSmsDto());
        try {
            migPaymentDetailsService.commitPaymentDetails(getUserId(), dto.getPin());
            modelAndView.addObject("result", "successful");
        } catch (ServiceException e) {
            modelAndView.addObject("result", FAIL);
        }

        return modelAndView;
    }

    @RequestMapping(value = PAGE_VERIFY_PSMS_RESEND_PIN, method = RequestMethod.POST)
    public
    @ResponseBody
    Boolean resendPsms(HttpServletRequest request, @PathVariable("scopePrefix") String scopePrefix, @ModelAttribute(PSmsDto.NAME) PSmsDto dto) {
        logger.info("Post resend PIN for user id:{} and phone: {}", getUserId(), dto.getPhone());

        try {
            pinMigService.sendPin(getUserId(), dto.getPhone());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handlerException(ServiceException serviceException, HttpServletRequest request, HttpServletResponse response, Locale locale) {
        String scopeView = request.getRequestURI().indexOf(PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP) >= 0 ?
                           PaymentsController.VIEW_MANAGE_PAYMENTS_INAPP :
                           PaymentsController.VIEW_MANAGE_PAYMENTS;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(scopeView + VIEW_PAYMENTS_PSMS);

        modelAndView.addObject(PSmsDto.NAME, new PSmsDto());
        final String message = messageSource.getMessage(serviceException.getErrorCodeForMessageLocalization(), null, locale);
        if (serviceException instanceof ExternalServiceException) {
            modelAndView.addObject("external_error", message);
        } else {
            modelAndView.addObject("internal_error", message);
        }

        Cookie cookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
        modelAndView.addObject("operators", paymentDetailsService.getAvailableOperators(cookie.getValue(), "PSMS"));
        modelAndView.addObject("result", FAIL);
        return modelAndView;
    }

    public void setPaymentDetailsService(PaymentDetailsService paymentDetailsService) {
        this.paymentDetailsService = paymentDetailsService;
    }

    public void setPaymentPolicyService(PaymentPolicyService paymentPolicyService) {
        this.paymentPolicyService = paymentPolicyService;
    }

    public void setMigPaymentDetailsService(MigPaymentDetailsService migPaymentDetailsService) {
        this.migPaymentDetailsService = migPaymentDetailsService;
    }

    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public void setPinMigService(PinMigService pinMigService) {
        this.pinMigService = pinMigService;
    }
}