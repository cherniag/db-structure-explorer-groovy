package mobi.nowtechnologies.server.shared.web.servlet;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/24/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlainTextModalAndView extends ModelAndView{
    public PlainTextModalAndView(Object model){
        setView(new View() {
            @Override
            public void render(Map<String, ?> map, HttpServletRequest arg1, HttpServletResponse response) throws Exception {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain");
                response.getWriter().print(map.values().iterator().next());
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }
        });
        addObject(model != null ? model : "");
    }
}
