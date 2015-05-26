package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.AccountDtoValidator;
import static mobi.nowtechnologies.server.shared.Utils.getTimeOfMovingToLimitedStatus;

import javax.persistence.PersistenceException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 */
@Controller
public class AccountController extends CommonController {
    private UserRepository userRepository;
    private String specificCommunityResourcesFolderPath;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setSpecificCommunityResourcesFolderPath(String specificCommunityResourcesFolderPath) {
        this.specificCommunityResourcesFolderPath = specificCommunityResourcesFolderPath;
    }

    @InitBinder(AccountDto.ACCOUNT_DTO)
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new AccountDtoValidator(userRepository));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(simpleDateFormat, true);

        binder.registerCustomEditor(Date.class, editor);

    }

    @RequestMapping(value = "account.html", method = RequestMethod.GET)
    public ModelAndView getAccountPage(HttpServletRequest request, Locale locale) {
        logger.debug("input parameters request [{}]", request);

        int userId = getUserId();

        Cookie cookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
        String communityUrl = cookie.getValue();

        User user = userRepository.findOne(userId);
        AccountDto accountDto = toAccountDto(user);

        ModelAndView modelAndView = getAccountPageModelView(locale, accountDto, communityUrl);

        logger.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "change_account.html", method = RequestMethod.GET)
    public ModelAndView getEditableAccountPage(HttpServletRequest request, Locale locale) {
        logger.debug("input parameters request [{}]", request);

        int userId = getUserId();

        Cookie cookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
        String communityUrl = cookie.getValue();

        User user = userRepository.findOne(userId);
        AccountDto accountDto = toAccountDto(user);

        accountDto = getLocalizedAccountDto(locale, accountDto, communityUrl);

        ModelAndView modelAndView = new ModelAndView("change_account");
        modelAndView.getModelMap().put(AccountDto.ACCOUNT_DTO, accountDto);

        logger.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    @RequestMapping(value = "change_account.html", method = RequestMethod.POST)
    public ModelAndView saveAccountDetails(HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute(AccountDto.ACCOUNT_DTO) AccountDto accountDto,
                                           BindingResult bindingResult) {
        logger.debug("input parameters request [{}]", request);

        ModelAndView modelAndView;
        if (bindingResult.hasErrors()) {
            modelAndView = new ModelAndView("change_account");
        } else {
            int userId = getUserId();

            doSaveAccountDetails(accountDto, userId);

            modelAndView = new ModelAndView("redirect:account.html");
        }
        logger.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    private void doSaveAccountDetails(AccountDto accountDto, int userId) {
        logger.debug("input parameters accountDto: [{}]", accountDto);

        User user = userRepository.findOne(userId);

        String localStoredToken = Utils.createStoredToken(user.getUserName(), accountDto.getNewPassword());

        user.setToken(localStoredToken);
        user.setMobile(accountDto.getPhoneNumber());

        userRepository.save(user);
    }

    private ModelAndView getAccountPageModelView(Locale locale, AccountDto accountDto, String communityUrl) {
        logger.debug("input parameters locale, accountDto, communityUrl: [{}], [{}], [{}]", new Object[] {locale, accountDto, communityUrl});
        accountDto = getLocalizedAccountDto(locale, accountDto, communityUrl);

        ModelAndView modelAndView = new ModelAndView("account");
        modelAndView.getModelMap().put(AccountDto.ACCOUNT_DTO, accountDto);

        logger.debug("Output parameter modelAndView=[{}]", modelAndView);
        return modelAndView;
    }

    private AccountDto toAccountDto(User user) {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail(user.getUserName());
        accountDto.setPhoneNumber(user.getMobile());
        accountDto.setSubBalance(user.getSubBalance());

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        AccountDto.Subscription subscription;
        if (UserStatusType.SUBSCRIBED.name().equals(user.getStatus().getName()) && currentPaymentDetails == null) {
            subscription = AccountDto.Subscription.freeTrialSubscription;
        } else if (UserStatusType.SUBSCRIBED.name().equals(user.getStatus().getName()) && currentPaymentDetails != null && currentPaymentDetails.isActivated()) {
            subscription = AccountDto.Subscription.subscribedSubscription;
        } else if ((currentPaymentDetails != null && !currentPaymentDetails.isActivated()) || UserStatusType.LIMITED.name().equals(user.getStatus().getName())) {
            subscription = AccountDto.Subscription.unsubscribedSubscription;
        } else {
            throw new PersistenceException("Couldn't recognize the user subscription");
        }

        accountDto.setSubscription(subscription);

        accountDto.setTimeOfMovingToLimitedStatus(getTimeOfMovingToLimitedStatus(user.getNextSubPayment(), user.getSubBalance()) * 1000L);
        if (user.getPotentialPromotion() != null) {
            accountDto.setPotentialPromotion(String.valueOf(user.getPotentialPromotion().getI()));
        }
        return accountDto;
    }

    private AccountDto getLocalizedAccountDto(Locale locale, AccountDto accountDto, String communityUrl) {
        logger.debug("input parameters locale, accountDto, communityUrl: [{}], [{}], [{}]", new Object[] {locale, accountDto, communityUrl});

        final String potentialPromotion = accountDto.getPotentialPromotion();

        String potentialPromotionImgPath = null;
        if (potentialPromotion != null) {
            potentialPromotionImgPath = specificCommunityResourcesFolderPath + communityUrl + "/" + potentialPromotion + ".png";
        }

        //String password = messageSource.getMessage("account.page.accountDetails.defaultPassword", null, locale);
        //String confirmPassword = messageSource.getMessage("account.page.accountDetails.defaultConfirmPassword", null, locale);
        String phoneNumber = accountDto.getPhoneNumber();

        //accountDto.setPassword(password);
        //accountDto.setConfirmPassword(confirmPassword);
        accountDto.setPhoneNumber(phoneNumber);
        accountDto.setPotentialPromotion(potentialPromotionImgPath);

        logger.debug("Output parameter accountDto=[{}]", accountDto);
        return accountDto;
    }

}