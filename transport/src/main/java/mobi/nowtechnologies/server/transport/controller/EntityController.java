package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.validator.UserDetailsDtoValidator;
import mobi.nowtechnologies.server.service.validator.UserFacebookDetailsDtoValidator;
import mobi.nowtechnologies.server.service.validator.UserRegDetailsDtoValidator;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.UserDetailsDto;
import mobi.nowtechnologies.server.shared.dto.UserFacebookDetailsDto;
import mobi.nowtechnologies.server.shared.dto.web.UserRegDetailsDto;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.*;

import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.util.StringUtils.hasText;

/**
 * EntityController
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * @author Maksym Chernolevskyi (maksym)
 */
@Controller
public class EntityController extends CommonController {

    private static final String APP_VERSION = "APP_VERSION";
    private static final String COMMUNITY_NAME = "COMMUNITY_NAME";
    private static final String API_VERSION = "API_VERSION";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String TIMESTAMP = "TIMESTAMP";

    private UserService userService;
    private DeviceService deviceService;
    private DrmService drmService;
    private UserRepository userRepository;

    private FacebookService facebookService;

    private PromotionService promoService;
    private CommunityResourceBundleMessageSource messageSource;

    public void setFacebookService(FacebookService facebookService) {
        this.facebookService = facebookService;
    }

    @InitBinder(UserRegDetailsDto.USER_REG_DETAILS_DTO)
    public void initUserRegDetailsDtoBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new UserRegDetailsDtoValidator(request, userService, null));
    }

    @InitBinder(UserDetailsDto.NAME)
    public void initUserDetailsDtoBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new UserDetailsDtoValidator(userService));
    }

    @InitBinder(UserFacebookDetailsDto.NAME)
    public void initUserFacebookDetailsDtoBinder(HttpServletRequest request, WebDataBinder binder) {
        binder.setValidator(new UserFacebookDetailsDtoValidator());
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void setDrmService(DrmService drmService) {
        this.drmService = drmService;
    }

    public void setMediaService(MediaService mediaService) {
    }

    public void setPromoService(PromotionService promoService) {
        this.promoService = promoService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/ECHO", "**/ECHO"})
    public ModelAndView echo() {
        ModelAndView modelAndView = new ModelAndView(defaultViewName, MODEL_NAME, new Response(new Object[]{}));
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/REGISTER_USER")
    public void registerUser(@RequestBody String body,
                             HttpServletResponse response,
                             HttpServletRequest request) {
        LOGGER.info("command processing started");
        notNull(body , "The parameter body is null");
        Source source = new StreamSource(new StringReader(body));
        UserRegInfo userRegInfo = (UserRegInfo) jaxb2Marshaller.unmarshal(source);
        String userName = userRegInfo.getEmail();
        String communityName = userRegInfo.getCommunityName();
        try {
            LOGGER.info("command processing for [{}] user, [{}] community", userName, communityName);
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");

            String remoteAddr = Utils.getIpFromRequest(request);
            userRegInfo.setIpAddress(remoteAddr);
            User user = userService.registerUserWhitoutPersonalInfo(userRegInfo);
            userService.loginUser(user, communityName, null);
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, value = {"/SET_DRM", "**/SET_DRM"})
    public ModelAndView setDRM(HttpServletRequest httpServletRequest,
                               @RequestParam(APP_VERSION) String appVersion,
                               @RequestParam(COMMUNITY_NAME) String communityName,
                               @RequestParam(API_VERSION) String apiVersion,
                               @RequestParam(USER_NAME) String userName,
                               @RequestParam(USER_TOKEN) String userToken,
                               @RequestParam(TIMESTAMP) String timestamp) {
        try {
            LOGGER.info("command processing started");
            notNull( userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");

            LOGGER.info("Request query string: [{}]", httpServletRequest.getQueryString());
            Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
            String mediaIsrc = null;
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                if (!(parameterName.equals(APP_VERSION) || parameterName.equals(COMMUNITY_NAME) || parameterName.equals(API_VERSION)
                        || parameterName.equals(USER_NAME) || parameterName.equals(USER_TOKEN) || parameterName.equals(TIMESTAMP))) {
                    mediaIsrc = parameterName;
                    break;
                }
            }

            notNull(mediaIsrc, "The argument mediaIsrc is null");
            notNull (appVersion, "The argument aAppVersion is null");
            notNull(apiVersion, "The argument aApiVersion is null");
            notNull(userToken, "The argument aUserToken is null");
            notNull(timestamp, "The argument aTimestamp is null");

            byte newDrmValue = Byte.valueOf(httpServletRequest.getParameter(mediaIsrc));

            User user = userService.checkCredentials(userName, userToken,
                    timestamp, communityName);

            Object[] objects = drmService.processSetDrmCommand(mediaIsrc, newDrmValue, user.getId(),
                    communityName);
            precessRememberMeToken(objects);
            return new ModelAndView(view, MODEL_NAME, new Response(
                    objects));
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/BUY_TRACK", "**/BUY_TRACK"})
    public ModelAndView buyTrack(@RequestParam("MEDIA_UID") String isrc,
                                 @RequestParam("APP_VERSION") String appVersion,
                                 @RequestParam("COMMUNITY_NAME") String communityName,
                                 @RequestParam("API_VERSION") String apiVersion,
                                 @RequestParam("USER_NAME") String userName,
                                 @RequestParam("USER_TOKEN") String userToken,
                                 @RequestParam("TIMESTAMP") String timestamp) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");

            notNull(isrc , "The parameter isrc is null");
            notNull(appVersion, "The argument appVersion is null");
            notNull(apiVersion, "The argument apiVersion is null");
            notNull(timestamp, "The argument timestamp is null");

            user = userService.checkCredentials(userName, userToken,
                    timestamp, communityName);
            Object[] objects = drmService.processBuyTrackCommand(user, isrc,
                    communityName);
            precessRememberMeToken(objects);
            return new ModelAndView(view, Response.class.getSimpleName(),
                    new Response(objects));
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, communityName, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/SET_PASSWORD", "**/SET_PASSWORD"})
    public ModelAndView setPassword(
            @RequestParam("NEW_TOKEN") String newToken,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) {
        try {
            LOGGER.info("command processing started");
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(newToken, "The argument aNewToken is null");
            notNull(appVersion, "The argument aAppVersion is null");
            notNull(apiVersion, "The argument aApiVersion is null");
            notNull(timestamp, "The argument aTimestamp is null");

            User user = userService.checkCredentials(userName, userToken, timestamp, communityName);

            Object[] objects = userService.processSetPasswordCommand(user.getId(), newToken,
                    communityName);

            precessRememberMeToken(objects);

            return new ModelAndView(view, Response.class.getSimpleName(),
                    new Response(objects));
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/SET_DEVICE", "**/SET_DEVICE"})
    public ModelAndView setDevice(
            @RequestParam("DEVICE_TYPE") String deviceType,
            @RequestParam("DEVICE_UID") String deviceUID,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(deviceType, "The argument deviceType is null");
            notNull(deviceUID, "The argument deviceUID is null");
            notNull(appVersion, "The argument appVersion is null");
            notNull(apiVersion, "The argument apiVersion is null");
            notNull(timestamp, "The argument timestamp is null");

            // TODO: Try to remove this code after deploying v1.2-samsung
            // BUT: only if v1.0-samsung client was not on android market
            // CL-6111 : deviceType and deviceString are not set for ANDROID
            // devices
            deviceType = (deviceType.equals(DeviceTypeDao.ANDROID.concat(",").concat(DeviceTypeDao.ANDROID))) ? DeviceTypeDao.ANDROID : deviceType;

            // TODO: Try to remove this code after deploying v1.2-samsung
            // BUT: only if v1.0-samsung client was not on android market
            // CL-6111 : deviceType and deviceString are not set for ANDROID
            // devices
            deviceType = (deviceType.equals(DeviceTypeDao.ANDROID.concat(",").concat(DeviceTypeDao.ANDROID))) ? DeviceTypeDao.ANDROID : deviceType;

            user = userService.checkCredentials(userName, userToken, timestamp, communityName);

            Map<String, Object> resultMap = deviceService.setDevice(user.getId(),
                    deviceType, deviceUID);

            DeviceSet deviceSet = (DeviceSet) resultMap.get(DeviceTypeDao.DEVICE_SET_RESULT_MAP_KEY);
            user = (User) resultMap.get(DeviceTypeDao.USER_RESULT_MAP_KEY);

            return new ModelAndView(view, Response.class.getSimpleName(),
                    new Response(new DeviceSet[]{deviceSet}));
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, communityName, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/CHECK_PIN", "**/CHECK_PIN"})
    public void checkPin(@RequestParam("APP_VERSION") String appVersion,
                         @RequestParam("COMMUNITY_NAME") String communityName,
                         @RequestParam("API_VERSION") String apiVersion,
                         @RequestParam("USER_NAME") String userName,
                         @RequestParam("USER_TOKEN") String userToken,
                         @RequestParam("TIMESTAMP") String timestamp,
                         @RequestParam("PIN") String pin) {
        try {
            LOGGER.info("command processing started");
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(pin , "The parameter pin is null");
            User user = userService.checkCredentials(userName, userToken, timestamp, communityName);
            userService.validateUserPin(user, pin, communityName);
        } finally {
            LOGGER.info("CHECK_PIN command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/GET_PAYMENT_POLICY", "**/GET_PAYMENT_POLICY"})
    public ModelAndView getPaymentPolicy(
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion) {
        try {
            LOGGER.info("command processing started");
            notNull(appVersion , "The parameter appVersion is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(apiVersion , "The parameter apiVersion is null");

            List<PaymentPolicy> paymentPolicies = userService.getPaymentPolicies(communityName);

            List<PaymentPolicyDto> dtos = new LinkedList<PaymentPolicyDto>();
            for (PaymentPolicy paymentPolicy : paymentPolicies) {
                PaymentPolicyDto dto = new PaymentPolicyDto();
                dto.setId(paymentPolicy.getId());
                dto.setOperator(paymentPolicy.getOperatorId());
                dto.setOperatorName(paymentPolicy.getOperatorName());
                dto.setPaymentType(paymentPolicy.getPaymentType());
                dto.setShortCode(paymentPolicy.getShortCode());
                dto.setSubcost(paymentPolicy.getSubcost());
                dto.setSubweeks(Integer.valueOf(paymentPolicy.getSubweeks()));
                dtos.add(dto);
            }

            return new ModelAndView(view, Response.class.getSimpleName(),
                    new Response(dtos.toArray()));
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/UPDATE_PHONE", "**/UPDATE_PHONE"})
    public void updatePhone(@RequestParam("APP_VERSION") String appVersion,
                            @RequestParam("COMMUNITY_NAME") String communityName,
                            @RequestParam("API_VERSION") String apiVersion,
                            @RequestParam("USER_NAME") String userName,
                            @RequestParam("USER_TOKEN") String userToken,
                            @RequestParam("TIMESTAMP") String timestamp,
                            @RequestParam("PHONE_NUMBER") String mobile,
                            @RequestParam("OPERATOR") Integer operator) {

        try {
            LOGGER.info("command proccessing started");
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(appVersion , "The parameter appVersion is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(apiVersion , "The parameter apiVersion is null");
            notNull(userToken , "The parameter userToken is null");
            notNull(timestamp , "The parameter timestamp is null");
            if (mobile == null)
                throw new ServiceException("The parameter mobileNumber is null");
            notNull(operator , "The parameter operatorId is null");

            User user = userService.checkCredentials(userName, userToken,
                    timestamp, communityName);
            userService.updateMobile(user, mobile, operator, communityName);
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/UPDATE_PAYMENT_DETAILS", "**/UPDATE_PAYMENT_DETAILS"})
    public void updatePaymentDetails(
            @RequestParam("BODY") String body,
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) {

        try {
            LOGGER.info("command processing started");
            notNull(userName , "The parameter userName is null");
            notNull(communityName , "The parameter communityName is null");
            if(appVersion == null)
                throw new RuntimeException("The parameter appVersion is null");
            notNull(apiVersion , "The parameter apiVersion is null");
            notNull(userToken , "The parameter userToken is null");
            notNull(timestamp , "The parameter timestamp is null");
            notNull(body , "The parameter body is null");

            Source source = new StreamSource(new StringReader(body));
            UserRegInfo userRegInfo = (UserRegInfo) jaxb2Marshaller.unmarshal(source);

            User user = userService.checkCredentials(userName, userToken,
                    timestamp, communityName);

            userRegInfo.setCommunityName(communityName);
            userRegInfo.setEmail(userName);

            userService.updatePaymentDetails(user, userRegInfo);
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/GET_PROMO_CODE", "**/GET_PROMO_CODE"})
    public ModelAndView getPromoCode(
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion) {

        try {
            LOGGER.info("command processing started");
            notNull(appVersion, "The parameter appVersion is null");
            notNull(communityName , "The parameter communityName is null");
            notNull(apiVersion , "The parameter apiVersion is null");

            List<PromoCode> codes = promoService.getPromoCodes(communityName);

            return new ModelAndView(view, Response.class.getSimpleName(),
                    new Response(PromoCode.toPromoCodeDtoList(codes).toArray()));
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/FB_ACC_CHECK", "**/FB_ACC_CHECK"})
    public ModelAndView facebookAccountCheck(
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("DEVICE_UID") String deviceId,
            @RequestParam("FB_TOKEN") String facebookToken,
            @RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
            @RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
            @RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
            HttpServletRequest request) {
        try {
            LOGGER.info("command processing started");

            // Send facebook request
            UserCredentions credentions = facebookService.getUserCredentions(communityName, facebookToken);
            final String creadentionsId = credentions.getId();
            final String credentionsEmail = credentions.getEmail();
            String userName = hasText(credentionsEmail) ? credentionsEmail : creadentionsId;

            User user = userService.findByNameAndCommunity(userName, communityName);

            if (user == null) {
                // register user if community has freeweeks promotion
                // Get promotion

                String promotionCode = userService.getDefaultPromoCode(communityName);

                // create tmp password
                String tmpPassword = Utils.getRandomString(AppConstants.TMP_PASSWORD_LENGTH);
                String localStoredToken = Utils.createStoredToken(userName, tmpPassword);

                // fill UserRegInfo
                UserRegInfo userRegInfo = new UserRegInfo();

                userRegInfo.setAppVersion(appVersion);
                userRegInfo.setCommunityName(communityName);
                userRegInfo.setEmail(userName);
                userRegInfo.setConfEmail(userName);

                userRegInfo.setStoredToken(localStoredToken);
                userRegInfo.setConfirmStoredToken(localStoredToken);
                userRegInfo.setDeviceType(deviceType);

                userRegInfo.setPromotionCode(promotionCode);

                final String firstName = credentions.getFirst_name();
                userRegInfo.setFirstName(firstName);
                userRegInfo.setLastName(credentions.getLast_name());

                String displayName = !firstName.isEmpty() ?
                        firstName : credentionsEmail;

                userRegInfo.setDisplayName(displayName);
                userRegInfo.setDeviceString(deviceId);

                userRegInfo.setIpAddress(Utils.getIpFromRequest(request));
                user = userService.registerUserWhitoutPersonalInfo(userRegInfo);
            }

            user.setFacebookId(creadentionsId);
            userService.updateUser(user);

            if (iphoneToken != null)
                pushNotificationToken = iphoneToken;

            final Object[] objects = new Object[]{userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(), pushNotificationToken,
                    deviceType, null)};

            precessRememberMeToken(objects);
            return new ModelAndView(view, MODEL_NAME, new Response(objects));
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/SIGN_UP", "**/SIGN_UP"})
    public void signUp(HttpServletRequest request, @Valid @ModelAttribute(UserRegDetailsDto.USER_REG_DETAILS_DTO) UserRegDetailsDto userRegDetailsDto,
                       BindingResult result) throws Exception {
        User user = null;
        Exception ex = null;
        LOGGER.info("command processing started");
        try {
            if (result.hasErrors()) {
                List<ObjectError> objectErrors = result.getAllErrors();

                for (ObjectError objectError : objectErrors) {
                    throw ValidationException.getInstance(objectError.getDefaultMessage());
                }
            }

            String remoteAddr = Utils.getIpFromRequest(request);

            userRegDetailsDto.setIpAddress(remoteAddr);

            user = userService.registerUser(userRegDetailsDto);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, userRegDetailsDto.getCommunityName(), null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/UPDATE_USER_FACEBOOK_DETAILS", "**/UPDATE_USER_FACEBOOK_DETAILS"})
    public ModelAndView updateUserFacebookDetails(HttpServletRequest request, @Valid @ModelAttribute(UserFacebookDetailsDto.NAME) UserFacebookDetailsDto userFacebookDetailsDto, BindingResult result) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");
            if (result.hasErrors()) {
                List<ObjectError> objectErrors = result.getAllErrors();

                for (ObjectError objectError : objectErrors) {
                    throw ValidationException.getInstance(objectError.getDefaultMessage());
                }
            }

            String remoteAddr = Utils.getIpFromRequest(request);
            userFacebookDetailsDto.setIpAddress(remoteAddr);

            AccountCheckDTO accountCheckDTO = userService.updateUserFacebookDetails(userFacebookDetailsDto);
            try {
                if (PROFILE_LOGGER.isDebugEnabled()) {
                    user = userService.findByNameAndCommunity(accountCheckDTO.userName, userFacebookDetailsDto.getCommunityName());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            final Object[] objects = new Object[]{accountCheckDTO};
            precessRememberMeToken(objects);

            return new ModelAndView(view, MODEL_NAME, new Response(objects));
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(userFacebookDetailsDto.getDeviceUID(), userFacebookDetailsDto.getCommunityName(), null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
