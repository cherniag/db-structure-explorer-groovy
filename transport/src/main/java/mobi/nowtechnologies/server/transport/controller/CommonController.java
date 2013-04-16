package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.error.ThrottlingException;
import mobi.nowtechnologies.server.interceptor.LoggerInterceptor;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.exception.*;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * EntityController
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
public abstract class CommonController {
	private static final String COMMUNITY_NAME_PARAM = "COMMUNITY_NAME";
	private static final String INTERNAL_SERVER_ERROR = "internal.server.error";

	protected final Logger PROFILE_LOGGER = LoggerFactory.getLogger("PROFILE_LOGGER");
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	protected View view;
	protected CommunityResourceBundleMessageSource messageSource;
	protected Jaxb2Marshaller jaxb2Marshaller;
	protected CommunityService communityService;
	private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;

	public void setView(View view) {
		this.view = view;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setJaxb2Marshaller(Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
	}
	
	public void setNowTechTokenBasedRememberMeServices(NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices) {
		this.nowTechTokenBasedRememberMeServices = nowTechTokenBasedRememberMeServices;
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse response) {

		final String localizedDisplayMessage = exception.getLocalizedMessage();
		final String message = exception.getMessage();
		ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, null);
		LOGGER.error(message, exception);

		return sendResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, response);
	}
	
	@ExceptionHandler(InvalidPhoneNumberException.class)
	public ModelAndView handleException(InvalidPhoneNumberException exception, HttpServletRequest httpServletRequest, HttpServletResponse response) {

		final String localizedDisplayMessage = exception.getLocalizedMessage();
		final String message = exception.getMessage();
		final Integer errorCode = new Integer(exception.getErrorCode());
		ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, errorCode);
		
		LOGGER.error(message, exception);

		return sendResponse(errorMessage, HttpStatus.OK, response);
	}
	
	@ExceptionHandler(ValidationException.class)
	public ModelAndView handleException(ValidationException validationException, HttpServletRequest httpServletRequest, HttpServletResponse response) {
			
		ServerMessage serverMessage = validationException.getServerMessage();
		String errorCodeForMessageLocalization = validationException.getErrorCodeForMessageLocalization();
		
		final String localizedDisplayMessage;
		final String message;
		if (serverMessage != null) {
			localizedDisplayMessage = ServerMessage.getMessage(ServerMessage.EN, serverMessage.getErrorCode(), serverMessage.getParameters());
			message = localizedDisplayMessage;
		}else if (errorCodeForMessageLocalization != null) {
			Locale locale = httpServletRequest.getLocale();
			String commnityUri = getCommunityUrl(httpServletRequest);
			localizedDisplayMessage = messageSource.getMessage(commnityUri, errorCodeForMessageLocalization, null, locale);
			message = messageSource.getMessage(commnityUri, errorCodeForMessageLocalization, null, Locale.ENGLISH);
		} else{
			localizedDisplayMessage = validationException.getLocalizedMessage();
			message = validationException.getMessage();
		}
		ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, null);
		LOGGER.warn(message);

		return sendResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, response);
	}

	@ExceptionHandler(SagePayException.class)
	public ModelAndView handleException(SagePayException sagePayException, HttpServletRequest httpServletRequest, HttpServletResponse response) {
		Locale locale = httpServletRequest.getLocale();
		String commnityUri = getCommunityUrl(httpServletRequest);

		String localizedDisplayMessage = messageSource.getMessage(commnityUri, INTERNAL_SERVER_ERROR, null, locale);
		final String message = sagePayException.getMessage();

		ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, null);
		LOGGER.error(message);

		return sendResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, response);
	}

	@ExceptionHandler(UserCredentialsException.class)
	public ModelAndView handleException(UserCredentialsException exception, HttpServletRequest httpServletRequest, HttpServletResponse response) {		
		ServerMessage serverMessage = exception.getServerMessage();
		
		final String localizedDisplayMessage;
		final String message;
		final Integer errorCode;
		
		if(serverMessage!=null){
			errorCode = serverMessage.getErrorCode();

			localizedDisplayMessage = ServerMessage.getMessage(ServerMessage.EN, errorCode, serverMessage.getParameters());
			message = localizedDisplayMessage;
		}else{
			errorCode = null;
			localizedDisplayMessage=exception.getMessage();
			message=localizedDisplayMessage;
		}

		ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, errorCode);
		LOGGER.info(message);

		return sendResponse(errorMessage, HttpStatus.UNAUTHORIZED, response);
	}
	
	@ExceptionHandler(ThrottlingException.class)
	public ModelAndView handleException(ThrottlingException exception, HttpServletRequest httpServletRequest, HttpServletResponse response) {
		LOGGER.info(exception.toString());
		response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
		ErrorMessage errorMessage = getErrorMessage("Server is temporary overloaded and unavailable", "Server is temporary overloaded and unavailable. Please, try again later.", HttpStatus.SERVICE_UNAVAILABLE.value());
		return sendResponse(errorMessage, HttpStatus.SERVICE_UNAVAILABLE, response);
	}

	@ExceptionHandler(ServiceException.class)
	public ModelAndView handleException(ServiceException serviceException, HttpServletRequest httpServletRequest, HttpServletResponse response) {	
		String message = serviceException.getMessage();
		Throwable throwable = serviceException.getCause();
		ServerMessage serverMessage = serviceException.getServerMessage();
		String errorCodeForMessageLocalization = serviceException.getErrorCodeForMessageLocalization();
		
		ErrorMessage errorMessage;
		if (message != null && serverMessage == null) {
			if (throwable != null) {
				errorMessage = getErrorMessage(throwable.getLocalizedMessage(), message, null);
			} else
				errorMessage = getErrorMessage(message, message, null);
			LOGGER.error(message, serviceException);
		} else if (serverMessage != null) {
			String localizedMessage = ServerMessage.getMessage(ServerMessage.EN, serverMessage.getErrorCode(), serverMessage.getParameters());

			errorMessage = getErrorMessage(localizedMessage, localizedMessage, serviceException.getServerMessage().getErrorCode());
			LOGGER.error(message);
		} else if (errorCodeForMessageLocalization != null) {
			Locale locale = httpServletRequest.getLocale();
			String commnityUri = getCommunityUrl(httpServletRequest);
			String localizedMessage = messageSource.getMessage(commnityUri, errorCodeForMessageLocalization, null, locale);
			message = serviceException.getLocalizedMessage();
			errorMessage = getErrorMessage(localizedMessage, message, null);
			LOGGER.error(message);
		} else
			throw new RuntimeException("The given serviceException doesn't contain message or serverMessage", serviceException.getCause());
		return sendResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, response);
	}
	
	private String getCommunityUrl(HttpServletRequest httpServletRequest)
	{
		String communityName = httpServletRequest.getParameter(COMMUNITY_NAME_PARAM);
		
		if(communityName != null)
		{
			Community community = communityService.getCommunityByName(communityName);
			return community != null ? community.getRewriteUrlParameter() : null;
		}
		
		return null;
	}

	private ErrorMessage getErrorMessage(String displayMessage, String message, Integer errorCode) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setDisplayMessage(displayMessage);
		errorMessage.setMessage(message);
		errorMessage.setErrorCode(errorCode);
		return errorMessage;

	}

	private ModelAndView sendResponse(ErrorMessage errorMessage, HttpStatus status, HttpServletResponse response) {
		if (status == null)
			throw new NullPointerException("The parameter httpStatus is null");
		if (errorMessage == null)
			throw new NullPointerException("The parameter errorMessage is null");
		response.setStatus(status.value());
		return new ModelAndView(view, Response.class.getSimpleName(), new Response(new Object[] { errorMessage }));
	}

	/**
	 * Returns an auth token that is generated for web portal SSO
	 * @return rememberMe auth token
	 */
	public Object[] proccessRememberMeToken(Object[] objects) {
		LOGGER.debug("input parameters objects: [{}], [{}]", objects);
		for (Object object : objects) {
			if (!(object instanceof AccountCheckDTO)) continue;
			AccountCheckDTO accountCheckDTO = (AccountCheckDTO) object;
			
			String rememberMeToken = getRememberMeToken(accountCheckDTO.getUserName(), accountCheckDTO.getUserToken());
			accountCheckDTO.setRememberMeToken(rememberMeToken);
		}
		LOGGER.debug("Output parameter objects=[{}]", objects);
		return objects;
	}
	
	public String getRememberMeToken(String userName, String storedToken) {
		LOGGER.debug("input parameters userName, storedToken: [{}], [{}]", new String[] { userName, storedToken});
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		if (storedToken == null)
			throw new NullPointerException("The parameter storedToken is null");

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(userName, storedToken);
		LOGGER.debug("Output parameter rememberMeToken=[{}]", rememberMeToken);
		return rememberMeToken;
	}
	
	public void logProfileDate(String communityFromRequest, UserDeviceRegDetailsDto userDeviceRegDetailsDto, String PHONEFromRequest, User user, Exception exception) {
		String result = "success";
		String errorMessage = null;
		if (exception != null) {
			result = "fail";
			errorMessage = exception.getMessage();
			if (errorMessage == null) {
				if (exception instanceof ServiceException) {
					ServiceException serviceException = (ServiceException) exception;

					ServerMessage serverMessage = serviceException.getServerMessage();
					String errorCodeForMessageLocalization = serviceException.getErrorCodeForMessageLocalization();
					if (serverMessage != null) {
						String localizedMessage = ServerMessage.getMessage(ServerMessage.EN, serverMessage.getErrorCode(), serverMessage.getParameters());
						errorMessage = localizedMessage;
					} else {
						errorMessage = errorCodeForMessageLocalization;
					}

				}
			}
		}

		String newUserName = null;
		String newDeviceUID = null;
		String newDeviceModel = null;
		String newDeviceType = null;
		String newMobile = null;
		String newCommunityRewriteUri = null;
		if (user != null) {
			newUserName = user.getUserName();
			newDeviceUID = user.getDeviceUID();
			newDeviceModel = user.getDeviceModel();
			newMobile = user.getMobile();
			UserGroup userGroup = user.getUserGroup();
			if (userGroup!=null){
				final Community community = userGroup.getCommunity();
				if (community!=null){
					newCommunityRewriteUri = community.getRewriteUrlParameter();
				}
			}
			final DeviceType userDeviceType = user.getDeviceType();
			if (userDeviceType != null) {
				newDeviceType = userDeviceType.getName();
			}
		}

		Long startTimeMillis = LogUtils.getStartTimeMillis();
		Long executionTimeMillis = null;
		if (startTimeMillis != null) {
			final long epochMillis = Utils.getEpochMillis();
			executionTimeMillis = epochMillis - startTimeMillis;
		}

		String deviceModelFromRequest = null;
		String deviceTypeFromRequest = null;
		String deviceUIDFromRequest = null;
		if (userDeviceRegDetailsDto!=null){
			deviceModelFromRequest = userDeviceRegDetailsDto.getDeviceModel();
			deviceTypeFromRequest = userDeviceRegDetailsDto.getDeviceType();
			deviceUIDFromRequest = userDeviceRegDetailsDto.getDeviceUID();
			if (communityFromRequest==null){
				communityFromRequest = userDeviceRegDetailsDto.getCommunityName();
			}
		}
		
		PROFILE_LOGGER.info("communityFromRequest={}; deviceModelFromRequest={}; deviceTypeFromRequest={}; deviceUIDFromRequest={}; PHONEFromRequest={}; newUserName={}; newCommunityRewriteUri={}; newMobile={}; newDeviceUID={}; newDeviceModel={}; newDeviceType={}; result={}; executionTimeMillis={}; errorMessage={}",
				new Object[] {communityFromRequest, deviceModelFromRequest, deviceTypeFromRequest, deviceUIDFromRequest, PHONEFromRequest, newUserName, newCommunityRewriteUri, newMobile, newDeviceUID, newDeviceModel, newDeviceType, result,
						executionTimeMillis, errorMessage });
	}
	
}
