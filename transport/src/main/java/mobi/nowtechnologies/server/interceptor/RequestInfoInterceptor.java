package mobi.nowtechnologies.server.interceptor;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class RequestInfoInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Object bean = handlerMethod.getBean();
        if (bean instanceof CommonController) {
            CommonController controller = (CommonController) bean;
            PathVariableResolver requestInfoResolver = new PathVariableResolver(request);
            String remoteAddr = Utils.getIpFromRequest(request);
            controller.setCurrentApiVersion(requestInfoResolver.resolveApiVersion());
            controller.setCurrentCommunityUri(requestInfoResolver.resolveCommunityUri());
            controller.setCurrentCommandName(requestInfoResolver.resolveCommandName());
            controller.setCurrentRemoteAddr(remoteAddr);
        }

        return super.preHandle(request, response, handler);
    }
}
