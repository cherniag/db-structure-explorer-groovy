package mobi.nowtechnologies.server.security.impl.spring.method.annotation;

import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import javax.servlet.ServletException;

/**
 * Created by zam on 11/26/2014.
 */
public class AuthenticatedUserMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver implements InitializingBean {

    private Class<?> parameterType;

    public AuthenticatedUserMethodArgumentResolver() {
        super(null);
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(parameterType, "parameterType must not be null");
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        AuthenticatedUser annotation = parameter.getParameterAnnotation(AuthenticatedUser.class);
        return (annotation != null) ?
                new AuthenticatedUserNamedValueInfo(annotation) :
                new AuthenticatedUserNamedValueInfo();
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        return request.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new MissingServletRequestParameterException(name, parameter.getParameterType().getSimpleName());
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class) &&
                parameterType.isAssignableFrom(parameter.getParameterType());
    }

    private class AuthenticatedUserNamedValueInfo extends NamedValueInfo {

        private AuthenticatedUserNamedValueInfo() {
            this(false);
        }

        private AuthenticatedUserNamedValueInfo(boolean isRequired) {
            super(AuthenticatedUser.AUTHENTICATED_USER_REQUEST_ATTRIBUTE, isRequired, null);
        }

        private AuthenticatedUserNamedValueInfo(AuthenticatedUser annotation) {
            this(annotation.required());
        }
    }
}
