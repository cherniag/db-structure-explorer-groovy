package mobi.nowtechnologies.server.security.impl.spring.handler;

import mobi.nowtechnologies.server.security.AuthenticationService;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Created by zam on 11/25/2014.
 */
public class AuthenticatingInterceptor extends HandlerInterceptorAdapter {

    private AuthenticationService<HttpServletRequest, Object> authenticationService;

    @Resource
    public void setAuthenticationService(AuthenticationService<HttpServletRequest, Object> authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        validateRequiredParam(AuthenticatedUser.USER_NAME, request);
        validateRequiredParam(AuthenticatedUser.USER_TOKEN, request);
        validateRequiredParam(AuthenticatedUser.TIMESTAMP, request);

        Object principal = authenticationService.authenticate(request);
        // add authenticated user as request attribute to simplify attribute resolving later
        request.setAttribute(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE, principal);
        // proceed in any case
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // simple clean up
        request.removeAttribute(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE);
        // proceed in any case
        super.afterCompletion(request, response, handler, ex);
    }

    private void validateRequiredParam(String name, HttpServletRequest request) throws MissingServletRequestParameterException {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            // this is to initiate HTTP 400 response back to client
            throw new MissingServletRequestParameterException(name, String.class.getSimpleName());
        }
    }
}
