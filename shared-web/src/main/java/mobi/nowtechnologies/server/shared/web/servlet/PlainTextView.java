package mobi.nowtechnologies.server.shared.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Created by oar on 3/3/14.
 */
public class PlainTextView extends AbstractView {

    private String text;

    public PlainTextView(String text) {
        this.text = text;
        setContentType(MediaType.TEXT_PLAIN_VALUE);
    }


    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);
    }


    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getWriter().print(text);
    }
}
