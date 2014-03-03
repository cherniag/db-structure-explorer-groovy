package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.validator.UserDeviceRegDetailsDtoValidator;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class SignUpDeviceController extends CommonController {

    @InitBinder(UserDeviceRegDetailsDto.NAME)
    public void initUserDeviceRegDetailsDtoBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new UserDeviceRegDetailsDtoValidator(getCurrentCommunityUri(), getCurrentRemoteAddr(), userService, communityService));
    }

    @RequestMapping(method = RequestMethod.POST, value =
            {
                    "**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}}/SIGN_UP_DEVICE"
            })
    public ModelAndView signUpDevice(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        String community = getCurrentCommunityUri();
        LOGGER.info("SIGN_UP_DEVICE Started for [{}] community[{}]", userDeviceDetailsDto, community);

        User user = null;
        Exception ex = null;
        try {
            user = registerUser(userDeviceDetailsDto, community, false);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

            return buildModelAndView(accountCheck);
        } catch (ValidationException ve) {
            ex = ve;
            LOGGER.error("SIGN_UP_DEVICE Validation error [{}] for [{}] community[{}]", ve.getMessage(), userDeviceDetailsDto, community);
            throw ve;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("SIGN_UP_DEVICE error [{}] for [{}] community[{}]", re.getMessage(), userDeviceDetailsDto, community);
            throw re;
        } finally {
            logProfileData(null, community, userDeviceDetailsDto, null, user, ex);
            LOGGER.info("SIGN_UP_DEVICE Finished for [{}] community[{}]", userDeviceDetailsDto, community);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:5\\.[3-9]{1,3}}/SIGN_UP_DEVICE",
            "**/{community}/{apiVersion:[6-9]{1}\\.[0-9]{1,3}}/SIGN_UP_DEVICE"
    })
    public ModelAndView signUpDevicev5_3(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        String community = getCurrentCommunityUri();
        LOGGER.info("SIGN_UP_DEVICE Started for [{}] community[{}]", userDeviceDetailsDto, community);

        User user = null;
        Exception ex = null;
        try {
            user = registerUser(userDeviceDetailsDto, community, true);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

            return buildModelAndView(accountCheck);
        } catch (ValidationException ve) {
            ex = ve;
            LOGGER.error("SIGN_UP_DEVICE Validation error [{}] for [{}] community[{}]", ve.getMessage(), userDeviceDetailsDto, community);
            throw ve;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("SIGN_UP_DEVICE error [{}] for [{}] community[{}]", re.getMessage(), userDeviceDetailsDto, community);
            throw re;
        } finally {
            logProfileData(null, community, userDeviceDetailsDto, null, user, ex);
            LOGGER.info("SIGN_UP_DEVICE Finished for [{}] community[{}]", userDeviceDetailsDto, community);
        }
    }

    private User registerUser(UserDeviceRegDetailsDto userDeviceDetailsDto, String community, boolean updateUserPendingActivation) {
        userDeviceDetailsDto.setIpAddress(getCurrentRemoteAddr());
        userDeviceDetailsDto.setCommunityUri(community);

        return userService.registerUser(userDeviceDetailsDto, false, updateUserPendingActivation);
    }
}
