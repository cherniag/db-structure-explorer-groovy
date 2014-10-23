package org.springframework.test.web.servlet.request;

import mobi.nowtechnologies.server.shared.util.HeaderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletContext;
import java.util.Date;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class ExtMockHttpServletRequest extends MockHttpServletRequest {
    public ExtMockHttpServletRequest(ServletContext servletContext) {
        super(servletContext);
    }


    public long getDateHeader(String name) {
        try {
            return super.getDateHeader(name);
        } catch (IllegalArgumentException e) {
            return convertStringValue(name);
        }
    }

    private long convertStringValue(String name) {
        String headerValue = getHeader(name);
        long result = -1;
        if (!StringUtils.isEmpty(headerValue)) {
            Date value = HeaderUtils.convertStringValueToDate(headerValue);
            if (value == null) {
                throw new IllegalArgumentException("Can convert string value to date");
            } else
                result = value.getTime();
        }
        return result;
    }
}
