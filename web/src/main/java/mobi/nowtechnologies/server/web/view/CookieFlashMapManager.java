package mobi.nowtechnologies.server.web.view;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;

public class CookieFlashMapManager extends AbstractFlashMapManager {

    private static final String FLASH_MAPS_COOKIE_ATTRIBUTE = CookieFlashMapManager.class.getName() + ".FLASH_MAPS";
    private static final Logger LOGGER = LoggerFactory.getLogger(CookieFlashMapManager.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonEncoding encoding = JsonEncoding.UTF8;

    @SuppressWarnings("unchecked")
    @Override
    protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Cookie cookieFlashMap = null;
        for (int i = 0; i < cookies.length; i++) {
            if (FLASH_MAPS_COOKIE_ATTRIBUTE.equals(cookies[i].getName())) {
                cookieFlashMap = cookies[i];
                break;
            }
        }

        if (cookieFlashMap == null) {
            return null;
        }

        try {
            JsonParser parser = this.objectMapper.getJsonFactory().createJsonParser(cookieFlashMap.getValue());
            return parser.readValueAs(List.class);
        } catch (JsonParseException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            JsonGenerator generator = this.objectMapper.getJsonFactory().createJsonGenerator(response.getOutputStream(), this.encoding);
            this.objectMapper.writeValue(generator, flashMaps);
            stream.close();

            Cookie cookieFlashMap = new Cookie(FLASH_MAPS_COOKIE_ATTRIBUTE, stream.toString());
            response.addCookie(cookieFlashMap);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}