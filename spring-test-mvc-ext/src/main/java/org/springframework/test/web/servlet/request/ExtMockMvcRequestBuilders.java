package org.springframework.test.web.servlet.request;

import org.springframework.http.HttpMethod;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class ExtMockMvcRequestBuilders {
    public static ExtMockHttpServletRequestBuilder extGet(String urlTemplate, Object... urlVariables) {
        return new ExtMockHttpServletRequestBuilder(HttpMethod.GET, urlTemplate, urlVariables);
    }

}
