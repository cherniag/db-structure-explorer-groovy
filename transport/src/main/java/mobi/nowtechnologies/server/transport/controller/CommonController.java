package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.error.ThrottlingException;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.*;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

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

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * EntityController
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
public abstract class CommonController extends ProfileController{
	private static final String COMMUNITY_NAME_PARAM = "COMMUNITY_NAME";
	private static final String INTERNAL_SERVER_ERROR = "internal.server.error";
	public static final String MODEL_NAME = Response.class.toString();
	public static final int VERSION_4 = 4;

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	protected View view;
	protected CommunityResourceBundleMessageSource messageSource;
    protected UserService userService;
	protected Jaxb2Marshaller jaxb2Marshaller;
	protected CommunityService communityService;
	private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;
    private UserRepository userRepository;
    protected String defaultViewName = "default";
    protected ThreadLocal<String> apiVersionThreadLocal = new ThreadLocal<String>();

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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ExceptionHandler(Exception.class)
	public ModelAndView handleException(Exception exception, HttpServletResponse response) {

		final String localizedDisplayMessage = exception.getLocalizedMessage();
		final String message = exception.getMessage();
		ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, null);
		LOGGER.error(message, exception);

		return sendResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, response);
	}
	
	@ExceptionHandler(InvalidPhoneNumberException.class)
	public ModelAndView handleException(InvalidPhoneNumberException exception, HttpServletResponse response) {

		final String localizedDisplayMessage = exception.getLocalizedMessage();
		final String message = exception.getMessage();
		final Integer errorCode = new Integer(exception.getErrorCodeForMessageLocalization());
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

	@ExceptionHandler(UserCredentialsException.class)
	public ModelAndView handleException(UserCredentialsException exception, HttpServletResponse response) {
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
	public ModelAndView handleException(ThrottlingException exception, HttpServletResponse response) {
		LOGGER.info(exception.toString());
		response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.addHeader("reason", "throttling");
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
			String communityUri = getCommunityUrl(httpServletRequest);
			String localizedMessage = messageSource.getMessage(communityUri, errorCodeForMessageLocalization, null, locale);
			message = serviceException.getLocalizedMessage();
			errorMessage = getErrorMessage(localizedMessage, message, serviceException.getErrorCode());
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
		notNull(status , "The parameter httpStatus is null");
        notNull(errorMessage , "The parameter errorMessage is null");
		response.setStatus(status.value());

        String apiVersion = apiVersionThreadLocal.get();

        if (isEmpty(apiVersion) || isMajorApiVersionNumberLessThan(VERSION_4, apiVersion) ){
            return new ModelAndView(view, Response.class.getSimpleName(), new Response(new Object[] { errorMessage }));
        }

        return new ModelAndView(defaultViewName, Response.class.getSimpleName(), new Response(new Object[] { errorMessage }));
	}

	/**
	 * Returns an auth token that is generated for web portal SSO
	 * @return rememberMe auth token
	 */
	public Object[] precessRememberMeToken(Object[] objects) {
		LOGGER.debug("input parameters objects: [{}]", objects);
		for (Object object : objects) {
			if (!(object instanceof AccountCheckDTO)) continue;
			AccountCheckDTO accountCheckDTO = (AccountCheckDTO) object;

            accountCheckDTO.rememberMeToken = getRememberMeToken(accountCheckDTO.userName, accountCheckDTO.userToken);
		}
		LOGGER.debug("Output parameter objects=[{}]", objects);
		return objects;
	}
	
	public String getRememberMeToken(String userName, String storedToken) {
		LOGGER.debug("input parameters userName, storedToken: [{}], [{}]", new String[] { userName, storedToken});
        notNull(userName , "The parameter userName is null");
		notNull(storedToken , "The parameter storedToken is null");

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken(userName, storedToken);
		LOGGER.debug("Output parameter rememberMeToken=[{}]", rememberMeToken);
		return rememberMeToken;
	}

    protected boolean isMajorApiVersionNumberLessThan(int majorVersionNumber, String apiVersion) {
        try {
            return Utils.isMajorVersionNumberLessThan(majorVersionNumber, apiVersion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Couldn't parse apiVersion [" + apiVersion + "]");
        }
    }
}
