package mobi.nowtechnologies.server.shared.web.servlet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by oar on 3/3/14.
 */
public class FileInResponseView extends AbstractView {

    private InputStream stream;

    public FileInResponseView(String contentType, InputStream stream) {
        this.stream = (stream instanceof BufferedInputStream) ? stream : new BufferedInputStream(stream);
        setContentType(contentType);
    }


    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String rangeAttribute = (String) request.getAttribute(HttpHeaders.RANGE);
        if (StringUtils.hasText(rangeAttribute)) {
            Long range = Long.valueOf(rangeAttribute);
            IOUtils.skipFully(stream, range);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        try {
            IOUtils.copy(stream, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
