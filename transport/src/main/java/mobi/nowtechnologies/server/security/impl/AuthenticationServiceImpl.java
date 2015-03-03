package mobi.nowtechnologies.server.security.impl;

import mobi.nowtechnologies.server.interceptor.PathVariableResolver;
import mobi.nowtechnologies.server.security.AuthenticationService;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by zam on 11/26/2014.
 */
public class AuthenticationServiceImpl implements AuthenticationService<HttpServletRequest, Object> {

    private UserService userService;
    private PathVariableResolver pathVariableResolver;

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Resource
    public void setPathVariableResolver(PathVariableResolver pathVariableResolver) {
        this.pathVariableResolver = pathVariableResolver;
    }

    @Override
    public Object authenticate(HttpServletRequest request) throws Exception {
        return userService.authenticate(pathVariableResolver.resolveCommunityUri(request), request.getParameter(AuthenticatedUser.USER_NAME), request.getParameter(AuthenticatedUser.USER_TOKEN),
                                        request.getParameter(AuthenticatedUser.TIMESTAMP), request.getParameter(AuthenticatedUser.DEVICE_UID));
    }
}
