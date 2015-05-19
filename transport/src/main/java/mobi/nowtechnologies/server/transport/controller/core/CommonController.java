package mobi.nowtechnologies.server.transport.controller.core;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.AccCheckService;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.ThrottlingException;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ActivationStatusException;
import mobi.nowtechnologies.server.service.exception.InvalidPhoneNumberException;
import mobi.nowtechnologies.server.service.exception.LimitPhoneNumberValidationException;
import mobi.nowtechnologies.server.service.exception.ReactivateUserException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.social.service.OAuth2ForbiddenException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.Locale;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.net.HttpHeaders.LAST_MODIFIED;
import static org.apache.commons.lang.Validate.notNull;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
public abstract class CommonController {

    public static final String MODEL_NAME = "response";
    public static final int VERSION_4 = 4;
    public static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
    public static final String VERSION_5_2 = "5.2";
    public static final String OAUTH_REALM_USERS = "OAuth realm=\"users\"";
    private static final String COMMUNITY_NAME_PARAM = "COMMUNITY_NAME";
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "serviceMessageSource")
    protected CommunityResourceBundleMessageSource messageSource;

    @Resource(name = "service.UserService")
    protected UserService userService;

    @Resource(name = "service.communityService")
    protected CommunityService communityService;

    @Resource
    protected AccCheckService accCheckService;

    @Resource
    protected ApplicationContext applicationContext;


    private ThreadLocal<String> apiVersionThreadLocal = new ThreadLocal<String>();
    private ThreadLocal<String> communityUriThreadLocal = new ThreadLocal<String>();
    private ThreadLocal<String> commandNameThreadLocal = new ThreadLocal<String>();
    private ThreadLocal<String> remoteAddrThreadLocal = new ThreadLocal<String>();

    public String getCurrentRemoteAddr() {
        return this.remoteAddrThreadLocal.get();
    }

    public void setCurrentRemoteAddr(String remoteAddr) {
        this.remoteAddrThreadLocal.set(remoteAddr);
    }

    public void setCurrentCommandName(String commandName) {
        this.commandNameThreadLocal.set(commandName);
    }

    public String getCurrentApiVersion() {
        return this.apiVersionThreadLocal.get();
    }

    public void setCurrentApiVersion(String apiVersion) {
        this.apiVersionThreadLocal.set(apiVersion);
    }

    public String getCurrentCommunityUri() {
        return this.communityUriThreadLocal.get();
    }

    public void setCurrentCommunityUri(String communityUri) {
        this.communityUriThreadLocal.set(communityUri);
    }

    protected ModelAndView buildModelAndView(Object... objs) {
        return new ModelAndView("default", MODEL_NAME, new Response(objs));
    }


    protected UserService getUserService(String communityUrl) {
        String userServiceBeanName = messageSource.getMessage(communityUrl, "service.bean.userService", null, null);

        return (UserService) applicationContext.getBean(userServiceBeanName);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception, HttpServletResponse response) {
        return sendResponse(exception, response, INTERNAL_SERVER_ERROR, true);
    }


    @ExceptionHandler(ReactivateUserException.class)
    public ModelAndView handleReactivation(ReactivateUserException exception, HttpServletResponse response) {
        return sendResponse(exception, response, HttpStatus.FORBIDDEN, false);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleException(MissingServletRequestParameterException exception, HttpServletResponse response) {
        int versionPriority = Utils.compareVersions(getCurrentApiVersion(), VERSION_5_2);
        HttpStatus status = versionPriority > 0 ? BAD_REQUEST : INTERNAL_SERVER_ERROR;

        return sendResponse(exception, response, status, true);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ModelAndView handleRequestBinding(MissingServletRequestParameterException exception, HttpServletResponse response) {
        return sendResponse(exception, response, BAD_REQUEST, true);
    }


    @ExceptionHandler({InvalidPhoneNumberException.class})
    public ModelAndView handleInvalidPhoneNumberException(InvalidPhoneNumberException exception, HttpServletResponse response) {
        exception.setLocalizedMessage("Invalid phone number format");
        return sendResponse(exception, response, OK, false);
    }

    @ExceptionHandler({LimitPhoneNumberValidationException.class})
    public ModelAndView handleLimitPhoneNumberValidationException(LimitPhoneNumberValidationException exception, HttpServletResponse response) {
        LOGGER.warn("Limit phone_number calls is exceeded for[{}] url[{}]", exception.getPhoneNumber(), exception.getUrl());
        return sendResponse(exception, response, OK, false);
    }


    @ExceptionHandler({ActivationStatusException.class})
    public ModelAndView handleException(ActivationStatusException exception, HttpServletResponse response) {
        return sendResponse(exception, response, HttpStatus.FORBIDDEN, false);
    }


    @ExceptionHandler(BindException.class)
    public ModelAndView handleMethodArgumentException(BindException argumentException, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        ObjectError objectError = Iterables.getFirst(argumentException.getBindingResult().getAllErrors(), null);
        return processException(ValidationException.getInstance(objectError.getDefaultMessage()), httpServletRequest, response);
    }

    private ModelAndView processException(ValidationException validationException, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        int versionPriority = Utils.compareVersions(getCurrentApiVersion(), VERSION_5_2);
        HttpStatus status = versionPriority > 0 ? BAD_REQUEST : INTERNAL_SERVER_ERROR;

        ServerMessage serverMessage = validationException.getServerMessage();
        String errorCodeForMessageLocalization = validationException.getErrorCodeForMessageLocalization();

        final String localizedDisplayMessage;
        final String message;
        if (serverMessage != null) {
            localizedDisplayMessage = ServerMessage.getMessage(ServerMessage.EN, serverMessage.getErrorCode(), serverMessage.getParameters());
            message = localizedDisplayMessage;
        } else if (errorCodeForMessageLocalization != null) {
            Locale locale = httpServletRequest.getLocale();
            String commnityUri = getCommunityUrl(httpServletRequest);
            localizedDisplayMessage = messageSource.getMessage(commnityUri, errorCodeForMessageLocalization, null, locale);
            message = messageSource.getMessage(commnityUri, errorCodeForMessageLocalization, null, Locale.ENGLISH);
        } else {
            localizedDisplayMessage = validationException.getLocalizedMessage();
            message = validationException.getMessage();
        }
        ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, null);
        LOGGER.warn(message);

        return sendResponse(errorMessage, status, response);
    }


    @ExceptionHandler(ValidationException.class)
    public ModelAndView handleException(ValidationException validationException, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        return processException(validationException, httpServletRequest, response);
    }

    @ExceptionHandler(UserCredentialsException.class)
    public ModelAndView handleException(UserCredentialsException exception, HttpServletResponse response) {
        ServerMessage serverMessage = exception.getServerMessage();

        String localizedDisplayMessage;
        final String message;
        final Integer errorCode;

        int versionPriority = Utils.compareVersions(getCurrentApiVersion(), VERSION_5_2);
        if (serverMessage != null) {
            errorCode = serverMessage.getErrorCode();

            localizedDisplayMessage = ServerMessage.getMessage(ServerMessage.EN, errorCode, serverMessage.getParameters());
            localizedDisplayMessage = versionPriority > 0 ? localizedDisplayMessage : "Bad user credentials";
            message = localizedDisplayMessage;
        } else {
            errorCode = null;
            localizedDisplayMessage = versionPriority > 0 ? exception.getMessage() : "Bad user credentials";
            message = localizedDisplayMessage;
        }

        ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, errorCode);
        LOGGER.info(message);

        response.setHeader(WWW_AUTHENTICATE_HEADER, OAUTH_REALM_USERS);
        return sendResponse(errorMessage, HttpStatus.UNAUTHORIZED, response);
    }

    @ExceptionHandler(ThrottlingException.class)
    public ModelAndView handleException(ThrottlingException exception, HttpServletResponse response) {
        LOGGER.info(exception.toString());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.addHeader("reason", "throttling");
        ErrorMessage errorMessage =
            getErrorMessage("Server is temporary overloaded and unavailable", "Server is temporary overloaded and unavailable. Please, try again later.", HttpStatus.SERVICE_UNAVAILABLE.value());
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
            } else {
                errorMessage = getErrorMessage(message, message, null);
            }
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
        } else {
            throw new RuntimeException("The given serviceException doesn't contain message or serverMessage", serviceException.getCause());
        }
        return sendResponse(errorMessage, INTERNAL_SERVER_ERROR, response);
    }


    @ExceptionHandler(OAuth2ForbiddenException.class)
    public ModelAndView handleExceptionFromSocialNetwork(Exception exception, HttpServletResponse response) {
        return sendResponse(exception, response, HttpStatus.FORBIDDEN, true);
    }

    private String getCommunityUrl(HttpServletRequest httpServletRequest) {
        String communityName = httpServletRequest.getParameter(COMMUNITY_NAME_PARAM);

        if (communityName != null) {
            Community community = communityService.getCommunityByName(communityName);
            return community != null ? community.getRewriteUrlParameter() : null;
        }

        return null;
    }

    protected ErrorMessage getErrorMessage(String displayMessage, String message, Integer errorCode) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDisplayMessage(displayMessage);
        errorMessage.setMessage(message);
        errorMessage.setErrorCode(errorCode);
        return errorMessage;

    }

    protected ModelAndView sendResponse(ErrorMessage errorMessage, HttpStatus status, HttpServletResponse response) {
        notNull(status, "The parameter httpStatus is null");
        notNull(errorMessage, "The parameter errorMessage is null");
        response.setStatus(status.value());

        return buildModelAndView(errorMessage);
    }

    protected ModelAndView sendResponse(Exception exception, HttpServletResponse response, HttpStatus status, boolean isCriticalException) {
        final String localizedDisplayMessage = exception.getLocalizedMessage();
        final String message = exception.getMessage();
        Integer errorCode;
        try {
            errorCode = exception instanceof ServiceException ? new Integer(((ServiceException) exception).getErrorCodeForMessageLocalization()) : null;
        } catch (NumberFormatException e) {
            errorCode = null;
        }
        ErrorMessage errorMessage = getErrorMessage(localizedDisplayMessage, message, errorCode);
        if (isCriticalException) {
            LOGGER.error(message, exception);
        } else {
            LOGGER.warn(message, exception);
        }

        return sendResponse(errorMessage, status, response);
    }


    public User checkUser(String userName, String userToken, String timestamp, String deviceUID, boolean checkReactivation, ActivationStatus... activationStatuses) {
        return userService.checkUser(getCurrentCommunityUri(), userName, userToken, timestamp, deviceUID, checkReactivation, activationStatuses);
    }

    protected boolean isMajorApiVersionNumberLessThan(int majorVersionNumber, String apiVersion) {
        try {
            return Utils.isMajorVersionNumberLessThan(majorVersionNumber, apiVersion);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Couldn't parse apiVersion [" + apiVersion + "]");
        }
    }

    // until cache not implemented
    protected void setMandatoryLastModifiedHeader(HttpServletResponse response) {
        response.setDateHeader(LAST_MODIFIED, new Date().getTime());
    }
}
