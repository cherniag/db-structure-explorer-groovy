package mobi.nowtechnologies.server.web.asm;

import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserRegDetailsDtoValidator extends BaseValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegDetailsDtoValidator.class);

    private UserService userService;
    private UserRepository userRepository;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean customValidate(Object target, Errors errors) {
        HttpServletRequest request = RequestUtils.getHttpServletRequest();

        LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);
        UserRegDetailsDto userRegDetailsDto = (UserRegDetailsDto) target;

        // Checking whether EULA is checked
        if (!userRegDetailsDto.isTermsConfirmed()) {
            errors.rejectValue("termsConfirmed", "NotChecked.termsConfirmed", "You can't finish registration without checking EULA");
        }

        // Checking whether passwords are identical
        if (!userRegDetailsDto.getPassword().equals(userRegDetailsDto.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "NoEquals.confirmPassword", "Confirmation field should equals to password field");
        }

        String email = userRegDetailsDto.getEmail().toLowerCase();
        // Checking whether user exists in community

        String communityName = userRegDetailsDto.getCommunityName();
        if (communityName == null) {
            Cookie cookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
            communityName = cookie.getValue();
        }
        if (null != userRepository.findByUserNameAndCommunityUrl(email, communityName)) {
            errors.rejectValue("email", "AlreadyExists.email", "This email is already exists");
        }

        String remoteAddr = Utils.getIpFromRequest(request);

        // Checking whether user are in right country in order to register in community
        if (!userService.isCommunitySupportByIp(email, communityName, remoteAddr)) {
            errors.rejectValue("ipAddress", "Incorrect.ipAddress", "We don't support your country");
        }

        if (StringUtils.hasText(userRegDetailsDto.getPromotionCode())) {
            if (!userService.checkPromotionCode(userRegDetailsDto.getPromotionCode(), communityName)) {
                errors.rejectValue("promotionCode", "reject.promotionCode", "Wrong promotion code");
            }
        }

        final boolean hasErrors = errors.hasErrors();

        LOGGER.debug("Output parameter hasErrors=[{}]", hasErrors);

        return hasErrors;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        LOGGER.debug("input parameters clazz: [{}]", clazz);
        final boolean supports = UserRegDetailsDto.class.isAssignableFrom(clazz);
        LOGGER.debug("Output parameter clazz=[{}]", clazz);
        return supports;

    }

}
