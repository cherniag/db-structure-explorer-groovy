package mobi.nowtechnologies.applicationtests.services.http;

import java.util.List;
import java.util.Map;
/**
 * Author: Gennadii Cherniaiev Date: 4/24/2015
 */
public class ResponseWrapper<T> {
    private int httpStatus;
    private Map<String, List<String>> headers;
    private T entity;

    public ResponseWrapper(int httpStatus, Map<String, List<String>> headers, T entity) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.entity = entity;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public T getEntity() {
        return entity;
    }
}
