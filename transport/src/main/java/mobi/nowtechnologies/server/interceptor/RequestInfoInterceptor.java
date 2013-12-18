package mobi.nowtechnologies.server.interceptor;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.transport.controller.CommonController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestInfoInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
        CommonController controller = (CommonController)handlerMethod.getBean();

        PathVariableResolver requestInfoResolver = new PathVariableResolver(request);
        String remoteAddr = Utils.getIpFromRequest(request);
        controller.setCurrentApiVersion(requestInfoResolver.resolveApiVersion());
        controller.setCurrentCommunityUri(requestInfoResolver.resolveCommunityUri());
        controller.setCurrentCommandName(requestInfoResolver.resolveCommandName());
        controller.setCurrentRemoteAddr(remoteAddr);

		return super.preHandle(request, response, handler);
    }
}
