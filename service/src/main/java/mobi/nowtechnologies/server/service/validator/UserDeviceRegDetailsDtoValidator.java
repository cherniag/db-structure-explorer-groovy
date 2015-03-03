package mobi.nowtechnologies.server.service.validator;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.util.BaseValidator;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserDeviceRegDetailsDtoValidator extends BaseValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegDetailsDtoValidator.class);

    private final String communityUri;
    private final String remoteAddr;

    private CommunityService communityService;
    private UserService userService;

    public UserDeviceRegDetailsDtoValidator(String communityUri, String remoteAddr, UserService userService, CommunityService communityService) {
        this.userService = userService;
        this.communityService = communityService;
        this.communityUri = communityUri;
        this.remoteAddr = remoteAddr;
        this.communityService = communityService;
    }

    @Override
    public boolean customValidate(Object target, Errors errors) {
        LOGGER.debug("input parameters target, errors: [{}], [{}]", target, errors);
        UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) target;

        Community community = communityService.getCommunityByUrl(communityUri);

        // Checking whether user are in right country in order to register in community
        if (!userService.isCommunitySupportByIp(null, community.getName(), remoteAddr)) {
            errors.rejectValue("ipAddress", "Incorrect.ipAddress", "We don't support your country");
        }

        final boolean hasErrors = errors.hasErrors();

        LOGGER.debug("Output parameter hasErrors=[{}]", hasErrors);
        return hasErrors;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        LOGGER.debug("input parameters clazz: [{}]", clazz);
        final boolean supports = UserDeviceRegDetailsDto.class.isAssignableFrom(clazz);
        LOGGER.debug("Output parameter clazz=[{}]", clazz);
        return supports;

    }
}
