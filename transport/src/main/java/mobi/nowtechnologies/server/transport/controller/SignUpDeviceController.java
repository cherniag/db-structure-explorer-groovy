package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.validator.UserDeviceRegDetailsDtoValidator;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class SignUpDeviceController extends CommonController {

    @Resource
    private DeviceUserDataService deviceUserDataService;

    @InitBinder(UserDeviceRegDetailsDto.NAME)
    public void initUserDeviceRegDetailsDtoBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new UserDeviceRegDetailsDtoValidator(getCurrentCommunityUri(), getCurrentRemoteAddr(), userService, communityService));
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}}/SIGN_UP_DEVICE"
    })
    public ModelAndView signUpDevice(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, false, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:5\\.[3-9]{1,3}}/SIGN_UP_DEVICE",
            "**/{community}/{apiVersion:6\\.0}/SIGN_UP_DEVICE"
    })
    public ModelAndView signUpDeviceV5_3(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, true, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:6\\.1}/SIGN_UP_DEVICE",
            "**/{community}/{apiVersion:6\\.2}/SIGN_UP_DEVICE",
            "**/{community}/{apiVersion:6\\.3}/SIGN_UP_DEVICE"
    })
    public ModelAndView signUpDeviceV6_1(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, true, true);
    }



    private ModelAndView processSignUpDevice(UserDeviceRegDetailsDto userDeviceDetailsDto, boolean updateUserPendingActivation, boolean updateXtifyToken) {
        String community = getCurrentCommunityUri();
        LOGGER.info("SIGN_UP_DEVICE Started for [{}] community[{}]", userDeviceDetailsDto, community);

        User user = null;
        Exception ex = null;
        try {
            user = registerUser(userDeviceDetailsDto, community, updateUserPendingActivation);

            if (updateXtifyToken && !isEmpty(userDeviceDetailsDto.getXtifyToken())){
                deviceUserDataService.saveXtifyToken(user, userDeviceDetailsDto.getXtifyToken());
            }

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
