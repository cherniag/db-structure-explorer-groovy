package mobi.nowtechnologies.server.shared.web.spring;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Date;

import static com.google.common.net.HttpHeaders.IF_MODIFIED_SINCE;
import static mobi.nowtechnologies.server.shared.util.HeaderUtils.convertStringValueToDate;

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
        String value = webRequest.getHeader(IF_MODIFIED_SINCE);
        Long result = null;
        if (!StringUtils.isEmpty(value)) {
            Date date = convertStringValueToDate(value);
            if (date != null) {
                result = date.getTime();
            }
        }
        if (result == null) {
            throw new ServletRequestBindingException("Error convert  header " + IF_MODIFIED_SINCE);
        }
        return result;

    }
}
