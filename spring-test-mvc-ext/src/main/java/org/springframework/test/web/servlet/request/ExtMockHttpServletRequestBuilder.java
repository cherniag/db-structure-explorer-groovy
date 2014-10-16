package org.springframework.test.web.servlet.request;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Constructor;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class ExtMockHttpServletRequestBuilder extends MockHttpServletRequestBuilder {
    ExtMockHttpServletRequestBuilder(HttpMethod httpMethod, String urlTemplate, Object... urlVariables) {
        super(httpMethod, urlTemplate, urlVariables);
    }

    protected MockHttpServletRequest createServletRequest(ServletContext servletContext) {
        return MockHttpServletRequestBuilder.servlet3Present ? createServlet3Request(servletContext) : new ExtMockHttpServletRequest(servletContext);
    }

    private MockHttpServletRequest createServlet3Request(ServletContext servletContext) {
        try {
            String className = "org.springframework.test.web.servlet.request.Servlet3MockHttpServletRequest";
            Class<?> clazz = ClassUtils.forName(className, MockHttpServletRequestBuilder.class.getClassLoader());
            Constructor<?> constructor = clazz.getConstructor(ServletContext.class);
            return (MockHttpServletRequest) BeanUtils.instantiateClass(constructor, servletContext);
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to instantiate MockHttpServletRequest", t);
        }
    }
}
