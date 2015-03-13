package mobi.nowtechnologies.server.admin.controller.streamzine;

import mobi.nowtechnologies.server.admin.validator.CookieUtil;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class StreamzineInterceptor extends HandlerInterceptorAdapter {

    private List<String> availableCommunites;
    private CookieUtil cookieUtil;

    public void setAvailableCommunites(String[] availableCommunites) {
        this.availableCommunites = Arrays.asList(availableCommunites);
    }

    public void setCookieUtil(CookieUtil cookieUtil) {
        this.cookieUtil = cookieUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod) handler;

            if (h.getBean() instanceof StreamzineController) {
                String communityRewriteUrl = cookieUtil.get(CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
                if (!availableCommunites.contains(communityRewriteUrl)) {
                    RequestMapping annotation = extractAnnotation(h);

                    boolean isIndexPage = annotation != null && StreamzineController.INDEX_PAGE.equals(annotation.value()[0]);
                    if (!isIndexPage) {
                        throw new ResourceNotFoundException("Not allowed to view " + request.getRequestURI() + " for community url: " + communityRewriteUrl);
                    }
                }
            }
        }

        return super.preHandle(request, response, handler);
    }

    RequestMapping extractAnnotation(HandlerMethod handlerMethod) {
        return AnnotationUtils.getAnnotation(handlerMethod.getMethod(), RequestMapping.class);
    }
}
