package mobi.nowtechnologies.server.transport.controller;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.springframework.util.StringUtils.hasText;

import java.io.StringReader;
import java.lang.Thread.State;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceSet;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.DrmService;
import mobi.nowtechnologies.server.service.FacebookService;
import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.WeeklyUpdateService;
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
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * EntityController
 * 
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Controller
public class EntityController extends CommonController {

	private static final String APP_VERSION = "APP_VERSION";
	private static final String COMMUNITY_NAME = "COMMUNITY_NAME";
	private static final String API_VERSION = "API_VERSION";
	private static final String USER_NAME = "USER_NAME";
	private static final String USER_TOKEN = "USER_TOKEN";
	private static final String TIMESTAMP = "TIMESTAMP";
	public static final String MODEL_NAME = Response.class.toString();

	private UserService userService;
	private DeviceService deviceService;
	private DrmService drmService;

	private WeeklyUpdateService weeklyUpdateService;
	private FacebookService facebookService;

	private Thread accountUpdateServiceThread;
	private Thread paymentRetryServiceThread;
	private Thread weeklyUpdateServiceThread;
	private PromotionService promoService;
	private DeviceUserDataService deviceUserDataService;

	public void setFacebookService(FacebookService facebookService) {
		this.facebookService = facebookService;
	}

	public void init() {
		// weeklyUpdateServiceThread = new Thread(weeklyUpdateService);
		// weeklyUpdateServiceThread.setName("weeklyUpdateServiceThread");
		// weeklyUpdateServiceThread.start();
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

	public void setDeviceUserDataService(DeviceUserDataService deviceUserDataService) {
		this.deviceUserDataService = deviceUserDataService;
	}

	public void setWeeklyUpdateService(WeeklyUpdateService weeklyUpdateService) {
		this.weeklyUpdateService = weeklyUpdateService;
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

	@RequestMapping(method = RequestMethod.POST, value = { "/ECHO", "**/ECHO" })
	public ModelAndView echo() {
		ModelAndView modelAndView = new ModelAndView(view, MODEL_NAME, new Response(new Object[] {}));
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/REGISTER_USER")
	public void registerUser(@RequestBody String body,
			HttpServletResponse response,
			HttpServletRequest request) {
		LOGGER.info("command processing started");
		if (body == null)
			throw new NullPointerException("The parameter body is null");
		Source source = new StreamSource(new StringReader(body));
		UserRegInfo userRegInfo = (UserRegInfo) jaxb2Marshaller.unmarshal(source);
		String userName = userRegInfo.getEmail();
		String communityName = userRegInfo.getCommunityName();
		try {
			LOGGER.info("command proccessing for [{}] user, [{}] community", userName, communityName);
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");

			String remoteAddr = Utils.getIpFromRequest(request);
			userRegInfo.setIpAddress(remoteAddr);
			User user = userService.registerUserWhitoutPersonalInfo(userRegInfo);
			user = userService.loginUser(user, communityName, null);
		} finally {
			LOGGER.info("command processing finished");
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = { "/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/ACC_CHECK", "*/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/ACC_CHECK" })
	public ModelAndView accountCheckForO2Client(
			HttpServletRequest httpServletRequest,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
			@RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
			@RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
			@RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
			@RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
			@RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt,
			@PathVariable("community") String community) throws Exception {

		User user = null;
		Exception ex = null;
		try {
			LOGGER.info("command proccessing started");
			
			if (iphoneToken != null)
				pushNotificationToken = iphoneToken;
			
			if (org.springframework.util.StringUtils.hasText(deviceUID))
				user = userService.checkCredentials(userName, userToken, timestamp, communityName, deviceUID);
			else
				user = userService.checkCredentials(userName, userToken, timestamp, communityName);

			logAboutSuccessfullAccountCheck();

			final AccountCheckDTO accountCheckDTO = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(),
					pushNotificationToken, deviceType, transactionReceipt);
			final Object[] objects = new Object[] { accountCheckDTO };
			proccessRememberMeToken(objects);

			ModelAndView mav =  new ModelAndView(view, MODEL_NAME, new Response(objects));
			
			if (isNotBlank(xtifyToken)) {
				user = deviceUserDataService.saveXtifyToken(xtifyToken, userName, communityName, deviceUID);
			}

			user = userService.findByNameAndCommunity(userName, community);
			
			ActivationStatus activationStatus = user.getActivationStatus();
			accountCheckDTO.setActivation(activationStatus);
			accountCheckDTO.setFullyRegistred(activationStatus == ActivationStatus.ACTIVATED);

			return mav;
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			logProfileData(deviceUID, community, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

	public static AccountCheckDTO getAccountCheckDtoFrom(ModelAndView mav) {
		Response resp = (Response) mav.
				getModelMap().
				get(MODEL_NAME);
		return (AccountCheckDTO) resp.getObject()[0];
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/ACC_CHECK", "*/ACC_CHECK" })
	public ModelAndView accountCheckWithXtifyToken(
			HttpServletRequest httpServletRequest,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
			@RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
			@RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
			@RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
			@RequestParam(required = false, value = "XTIFY_TOKEN") String xtifyToken,
			@RequestParam(required = false, value = "TRANSACTION_RECEIPT") String transactionReceipt) throws Exception {

		User user = null;
		Exception ex = null;
		try {
			LOGGER.info("command proccessing started");
			
			if (iphoneToken != null)
				pushNotificationToken = iphoneToken;
			
			if (org.springframework.util.StringUtils.hasText(deviceUID))
				user = userService.checkCredentials(userName, userToken, timestamp, communityName, deviceUID);
			else
				user = userService.checkCredentials(userName, userToken, timestamp, communityName);

			logAboutSuccessfullAccountCheck();

			final AccountCheckDTO accountCheckDTO = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(),
					pushNotificationToken, deviceType, transactionReceipt);
			final Object[] objects = new Object[] { accountCheckDTO };
			proccessRememberMeToken(objects);

			ModelAndView mav =  new ModelAndView(view, MODEL_NAME, new Response(objects));
			
			if (isNotBlank(xtifyToken)) {
				user = deviceUserDataService.saveXtifyToken(xtifyToken, userName, communityName, deviceUID);
			}

			return mav;
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			logProfileData(deviceUID, communityName, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

	private void logAboutSuccessfullAccountCheck() {
		MDC.put("ACC_CHECK", "");
		try {
			LOGGER.info("The login was successful");
		} finally {
			MDC.remove("ACC_CHECK");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = { "/SET_DRM", "**/SET_DRM" })
	public ModelAndView setDRM(HttpServletRequest httpServletRequest,
			@RequestParam(APP_VERSION) String appVersion,
			@RequestParam(COMMUNITY_NAME) String communityName,
			@RequestParam(API_VERSION) String apiVersion,
			@RequestParam(USER_NAME) String userName,
			@RequestParam(USER_TOKEN) String userToken,
			@RequestParam(TIMESTAMP) String timestamp) {
		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");

			LOGGER.info("Reguest query string: [{}]", httpServletRequest.getQueryString());
			Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
			String mediaIsrc = null;
			while (parameterNames.hasMoreElements()) {
				String parameterName = (String) parameterNames.nextElement();
				if (!(parameterName.equals(APP_VERSION) || parameterName.equals(COMMUNITY_NAME) || parameterName.equals(API_VERSION)
						|| parameterName.equals(USER_NAME) || parameterName.equals(USER_TOKEN) || parameterName.equals(TIMESTAMP))) {
					mediaIsrc = parameterName;
					break;
				}
			}

			if (null == mediaIsrc)
				throw new NullPointerException("The argument mediaIsrc is null");
			if (null == appVersion)
				throw new NullPointerException("The argument aAppVersion is null");
			if (null == apiVersion)
				throw new NullPointerException("The argument aApiVersion is null");
			if (null == userToken)
				throw new NullPointerException("The argument aUserToken is null");
			if (null == timestamp)
				throw new NullPointerException("The argument aTimestamp is null");

			byte newDrmValue = Byte.valueOf(httpServletRequest.getParameter(mediaIsrc));

			User user = userService.checkCredentials(userName, userToken,
					timestamp, communityName);

			Object[] objects = drmService.processSetDrmCommand(mediaIsrc, newDrmValue, user.getId(),
					communityName);
			proccessRememberMeToken(objects);
			return new ModelAndView(view, MODEL_NAME, new Response(
					objects));
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/BUY_TRACK", "**/BUY_TRACK" })
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
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");

			if (isrc == null)
				throw new NullPointerException("The parameter isrc is null");
			if (null == appVersion)
				throw new NullPointerException(
						"The argument appVersion is null");
			if (null == apiVersion)
				throw new NullPointerException(
						"The argument apiVersion is null");
			if (null == timestamp)
				throw new NullPointerException(
						"The argument timestamp is null");
			LOGGER.info("command after USER_NAME parameter validation on null");

			user = userService.checkCredentials(userName, userToken,
					timestamp, communityName);
			Object[] objects = drmService.processBuyTrackCommand(user, isrc,
					communityName);
			proccessRememberMeToken(objects);
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

	@RequestMapping(method = RequestMethod.POST, value = { "/SET_PASSWORD", "**/SET_PASSWORD" })
	public ModelAndView setPassword(
			HttpServletRequest httpServletRequest,
			@RequestParam("NEW_TOKEN") String newToken,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp) {
		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (null == newToken)
				throw new NullPointerException("The argument aNewToken is null");
			if (null == appVersion)
				throw new NullPointerException("The argument aAppVersion is null");
			if (null == apiVersion)
				throw new NullPointerException("The argument aApiVersion is null");
			if (null == timestamp)
				throw new NullPointerException("The argument aTimestamp is null");

			User user = userService.checkCredentials(userName, userToken, timestamp, communityName);

			Object[] objects = userService.processSetPasswordCommand(user.getId(), newToken,
					communityName);

			proccessRememberMeToken(objects);

			return new ModelAndView(view, Response.class.getSimpleName(),
					new Response(objects));
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/SET_DEVICE", "**/SET_DEVICE" })
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
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");

			if (null == deviceType)
				throw new NullPointerException("The argument deviceType is null");
			if (null == deviceUID)
				throw new NullPointerException("The argument deviceUID is null");
			if (null == appVersion)
				throw new NullPointerException("The argument appVersion is null");
			if (null == apiVersion)
				throw new NullPointerException("The argument apiVersion is null");
			if (null == timestamp)
				throw new NullPointerException("The argument timestamp is null");

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
					new Response(new DeviceSet[] { deviceSet }));
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			logProfileData(deviceUID, communityName, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/CHECK_PIN", "**/CHECK_PIN" })
	public void checkPin(@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam("PIN") String pin,
			HttpServletResponse response,
			HttpServletRequest request) {
		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (pin == null)
				throw new NullPointerException("The parameter pin is null");
			User user = userService.checkCredentials(userName, userToken, timestamp, communityName);
			userService.validateUserPin(user, pin, communityName);
		} finally {
			LOGGER.info("CHECK_PIN command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/GET_PAYMENT_POLICY", "**/GET_PAYMENT_POLICY" })
	public ModelAndView getPaymentPolicy(
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion) {
		try {
			LOGGER.info("command processing started");
			if (appVersion == null)
				throw new NullPointerException("The parameter appVersion is null");
			if (communityName == null)
				throw new NullPointerException(
						"The parameter communityName is null");
			if (apiVersion == null)
				throw new NullPointerException("The parameter apiVersion is null");

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

	@RequestMapping(method = RequestMethod.POST, value = { "/UPDATE_PHONE", "**/UPDATE_PHONE" })
	public void updatePhone(@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam("PHONE_NUMBER") String mobile,
			@RequestParam("OPERATOR") Integer operator,
			HttpServletResponse response,
			HttpServletRequest request) {

		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (appVersion == null)
				throw new NullPointerException("The parameter appVersion is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (apiVersion == null)
				throw new NullPointerException("The parameter apiVersion is null");
			if (userToken == null)
				throw new NullPointerException("The parameter userToken is null");
			if (timestamp == null)
				throw new NullPointerException("The parameter timestamp is null");
			if (mobile == null)
				throw new ServiceException("The parameter mobileNumber is null");
			if (operator == null)
				throw new NullPointerException("The parameter operatorId is null");

			User user = userService.checkCredentials(userName, userToken,
					timestamp, communityName);
			userService.updateMobile(user, mobile, operator, communityName);
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	// TODO refactor this method
	@Deprecated
	@RequestMapping(value = { "/jobsState", "/jobsstate", "/JOBSSTATE", "/JOBS_STATE" }, method = RequestMethod.GET)
	public ModelAndView getStatus(HttpServletResponse response) {

		try {
			LOGGER.info("command processing started");
			if (response == null)
				throw new NullPointerException("The parameter response is null");

			State accountUpdateServiceThreadState = accountUpdateServiceThread
					.getState();
			State migRetryServiceThreadState = paymentRetryServiceThread.getState();
			State weeklyUpdateServiceThreadState = weeklyUpdateServiceThread
					.getState();

			final String message = "The jobs state:\n"
					+ "AccountUpdateServiceThread "
					+ accountUpdateServiceThreadState + "\n"
					+ "MigRetryServiceThread " + migRetryServiceThreadState
					+ "\n" + "WeeklyUpdateServiceThread "
					+ weeklyUpdateServiceThreadState;

			LOGGER.info("JOBS_STATE command processing finished");
			return new ModelAndView(new View() {
				@Override
				public void render(Map<String, ?> arg0,
						HttpServletRequest arg1, HttpServletResponse response)
						throws Exception {
					IOUtils.copy(new StringReader(message), response
							.getOutputStream());
				}

				@Override
				public String getContentType() {
					return "text/html";
				}
			}, "", response);
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/UPDATE_PAYMENT_DETAILS", "**/UPDATE_PAYMENT_DETAILS" })
	public void updatePaymentDetails(
			@RequestParam("BODY") String body,
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			HttpServletResponse response,
			HttpServletRequest request) {

		try {
			LOGGER.info("command processing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (appVersion == null)
				throw new RuntimeException("The parameter appVersion is null");
			if (apiVersion == null)
				throw new NullPointerException(
						"The parameter apiVersion is null");
			if (userToken == null)
				throw new NullPointerException(
						"The parameter userToken is null");
			if (timestamp == null)
				throw new NullPointerException(
						"The parameter timestamp is null");
			if (body == null)
				throw new NullPointerException("The parameter body is null");

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

	@RequestMapping(method = RequestMethod.POST, value = { "/GET_PROMO_CODE", "**/GET_PROMO_CODE" })
	public ModelAndView getPromoCode(
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion) {

		try {
			LOGGER.info("command processing started");
			if (appVersion == null)
				throw new NullPointerException("The parameter appVersion is null");
			if (communityName == null)
				throw new NullPointerException(
						"The parameter communityName is null");
			if (apiVersion == null)
				throw new NullPointerException("The parameter apiVersion is null");

			List<PromoCode> codes = promoService.getPromoCodes(communityName);

			return new ModelAndView(view, Response.class.getSimpleName(),
					new Response(PromoCode.toPromoCodeDtoList(codes).toArray()));
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/FB_ACC_CHECK", "**/FB_ACC_CHECK" })
	public ModelAndView facebookAccountCheck(
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("DEVICE_UID") String deviceId,
			@RequestParam("FB_TOKEN") String facebookToken,
			@RequestParam(required = false, value = "DEVICE_TYPE", defaultValue = UserRegInfo.DeviceType.IOS) String deviceType,
			@RequestParam(required = false, value = "PUSH_NOTIFICATION_TOKEN") String pushNotificationToken,
			@RequestParam(required = false, value = "IPHONE_TOKEN") String iphoneToken,
			HttpServletResponse response,
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

			final Object[] objects = new Object[] { userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(), pushNotificationToken,
					deviceType, null) };

			proccessRememberMeToken(objects);
			return new ModelAndView(view, MODEL_NAME, new Response(objects));
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/SIGN_UP", "**/SIGN_UP" })
	public void signUp(HttpServletRequest request, HttpServletResponse response, @Valid @ModelAttribute(UserRegDetailsDto.USER_REG_DETAILS_DTO) UserRegDetailsDto userRegDetailsDto,
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

	@RequestMapping(method = RequestMethod.POST, value = { "/UPDATE_USER_FACEBOOK_DETAILS", "**/UPDATE_USER_FACEBOOK_DETAILS" })
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
					user = userService.findByNameAndCommunity(accountCheckDTO.getUserName(), userFacebookDetailsDto.getCommunityName());
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

			final Object[] objects = new Object[] { accountCheckDTO };
			proccessRememberMeToken(objects);

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
