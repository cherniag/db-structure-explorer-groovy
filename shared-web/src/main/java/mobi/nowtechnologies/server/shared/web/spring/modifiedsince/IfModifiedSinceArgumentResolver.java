package mobi.nowtechnologies.server.shared.web.spring.modifiedsince;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class IfModifiedSinceArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(IfModifiedSinceHeader.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return IfModifiedUtils.getIfModifiedHeaderValue((javax.servlet.http.HttpServletRequest) webRequest.getNativeRequest(), calculateDefaultValue(parameter));
    }

    private Long calculateDefaultValue(MethodParameter parameter) {
        IfModifiedSinceHeader value = parameter.getParameterAnnotation(IfModifiedSinceHeader.class);
        switch (value.defaultValue()) {
            case ZERO:
                return 0L;
        }
        return 0L;
    }

}
