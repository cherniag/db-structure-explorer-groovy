package mobi.nowtechnologies.server.service.validator;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserRegDetailsDtoValidator extends BaseValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRegDetailsDtoValidator.class);
	
	private UserService userService;
	private HttpServletRequest request;
	private String defaultCommunityName;
	
	public UserRegDetailsDtoValidator(HttpServletRequest request, UserService userService, String defaultCommunityName) {
		this.userService = userService;
		this.request = request;
		this.defaultCommunityName=defaultCommunityName;
	}

	@Override
	public boolean customValidate(Object target, Errors errors) {
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
		if(communityName==null){			
			communityName = defaultCommunityName;
		}
		if (null != userService.findByNameAndCommunity(email, communityName)) {
			errors.rejectValue("email", "AlreadyExists.email", "This email is already exists");
		}
		
		String remoteAddr = Utils.getIpFromRequest(request);
		
		// Checking whether user are in right country in order to register in community
		if (!userService.isCommunitySupportByIp(email, communityName, remoteAddr)) {
			errors.rejectValue("ipAddress", "Incorrect.ipAddress", "We don't support your counrty");
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