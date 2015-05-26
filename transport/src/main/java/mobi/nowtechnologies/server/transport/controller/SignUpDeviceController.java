package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.AppsFlyerDataService;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.validator.UserDeviceRegDetailsDtoValidator;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class SignUpDeviceController extends CommonController {

    @Resource
    private DeviceUserDataService deviceUserDataService;

    @Resource
    private AppsFlyerDataService appsFlyerDataService;

    @InitBinder(UserDeviceRegDetailsDto.NAME)
    public void initUserDeviceRegDetailsDtoBinder(WebDataBinder binder) {
        binder.setValidator(new UserDeviceRegDetailsDtoValidator(getCurrentCommunityUri(), getCurrentRemoteAddr(), userService, communityService));
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.11}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:6\\.10}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:6\\.9}/SIGN_UP_DEVICE",
                        "**/{community}/{apiVersion:6\\.8}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:6\\.7}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:6\\.6}/SIGN_UP_DEVICE"})
    public ModelAndView signUpDeviceV6_6(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, true, true, true, true);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.5}/SIGN_UP_DEVICE"})
    public ModelAndView signUpDeviceV6_5(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, true, true, true, false);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.4}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:6\\.3}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:6\\.2}/SIGN_UP_DEVICE",
                        "**/{community}/{apiVersion:6\\.1}/SIGN_UP_DEVICE"})
    public ModelAndView signUpDeviceV6_1(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, true, true, false, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.0}/SIGN_UP_DEVICE", "**/{community}/{apiVersion:5\\.[3-9]{1,3}}/SIGN_UP_DEVICE"})
    public ModelAndView signUpDeviceV5_3(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, true, false, false, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}}/SIGN_UP_DEVICE"})
    public ModelAndView signUpDevice(@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto) {
        return processSignUpDevice(userDeviceDetailsDto, false, false, false, false);
    }


    private ModelAndView processSignUpDevice(UserDeviceRegDetailsDto userDeviceDetailsDto, boolean updateUserPendingActivation, boolean updateXtifyToken, boolean withUuid,
                                             boolean updateAppsFlyerUid) {
        String community = null;
        try {
            community = getCurrentCommunityUri();
            LOGGER.info("SIGN_UP_DEVICE Started for [{}] community[{}]", userDeviceDetailsDto, community);
            User user = registerUser(userDeviceDetailsDto, community, updateUserPendingActivation);

            if (updateXtifyToken && !isEmpty(userDeviceDetailsDto.getXtifyToken())) {
                deviceUserDataService.saveXtifyToken(user, userDeviceDetailsDto.getXtifyToken());
            }

            if (updateAppsFlyerUid && !isEmpty(userDeviceDetailsDto.getAppsFlyerUid())) {
                appsFlyerDataService.saveAppsFlyerData(user, userDeviceDetailsDto.getAppsFlyerUid());
            }

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false, withUuid, false);

            return buildModelAndView(accountCheck);
        } catch (ValidationException ve) {
            LOGGER.error("SIGN_UP_DEVICE Validation error [{}] for [{}] community[{}]", ve.getMessage(), userDeviceDetailsDto, community);
            throw ve;
        } catch (RuntimeException re) {
            LOGGER.error("SIGN_UP_DEVICE error [{}] for [{}] community[{}]", re.getMessage(), userDeviceDetailsDto, community);
            throw re;
        }
    }

    private User registerUser(UserDeviceRegDetailsDto userDeviceDetailsDto, String community, boolean updateUserPendingActivation) {
        userDeviceDetailsDto.setIpAddress(getCurrentRemoteAddr());
        userDeviceDetailsDto.setCommunityUri(community);

        return userService.registerUser(userDeviceDetailsDto, false, updateUserPendingActivation);
    }
}
