package mobi.nowtechnologies.server.shared.web.spring.modifiedsince;

import mobi.nowtechnologies.server.shared.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.google.common.net.HttpHeaders.IF_MODIFIED_SINCE;
import static com.google.common.net.HttpHeaders.LAST_MODIFIED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;
import static mobi.nowtechnologies.server.shared.util.HeaderUtils.convertStringValueToDate;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by Oleg Artomov on 10/17/2014.
 */
public class IfModifiedUtils {

    public static long getIfModifiedHeaderValue(HttpServletRequest request, Long defaultValue) {
        String value = request.getHeader(IF_MODIFIED_SINCE);
        Long result = null;
        if (!isEmpty(value)) {
            Date date = convertStringValueToDate(value);
            if (date != null) {
                result = date.getTime();
            }
        }
        if (result == null) {
            result = defaultValue;
        }
        long epochMillis = Utils.getEpochMillis();
        return result > epochMillis ? defaultValue : result;
    }

    public static boolean checkNotModified(long lastModifiedTimestamp, HttpServletRequest request, HttpServletResponse response) {
        boolean notModified = false;
        if (lastModifiedTimestamp >= 0 &&
                (response == null || !response.containsHeader(LAST_MODIFIED))) {
            long ifModifiedSince = getIfModifiedHeaderValue(request, 0L);
            notModified = (ifModifiedSince >= (lastModifiedTimestamp / 1000 * 1000));
            if (response != null) {
                if (notModified && "GET".equals(request.getMethod())) {
                    response.setStatus(SC_NOT_MODIFIED);
                } else {
                    response.setDateHeader(LAST_MODIFIED, lastModifiedTimestamp);
                }
            }
        }
        return notModified;
    }
}
