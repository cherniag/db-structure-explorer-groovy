package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.AccountDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.AccountDtoValidator;

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
    private UserService userService;
    private UserRepository userRepository;
    private String specificCommunityResourcesFolderPath;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

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
        AccountDto accountDto = user.toAccountDto();

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
        AccountDto accountDto = user.toAccountDto();

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