package mobi.nowtechnologies.applicationtests.services.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;

@Component
public class JsonHelper {
    public static final String USER_PATH = "$.response.data[0].user";
    public static final String ERROR_MESSAGE_PATH = "$.response.data[0].errorMessage";
    public static final String USER_DETAILS_PATH = "$.response.data[0].user.userDetails";
    public static final String PHONE_NUMBER_PATH = "$.response.data[0].phoneActivation";

    private ObjectMapper objectMapper = new ObjectMapper();

    //
    // String API
    //
    public String extractObjectJsonByPath(String rawResponseString, String path) {
        JsonPath responseUserPath = JsonPath.compile(path);
        JSONObject read = responseUserPath.read(rawResponseString);
        return read.toJSONString();
    }

    //
    // Object API
    //
    public <T> T extractObjectValueByPath(String response, String path, Class<T> type) {
        try {
            String objectJson = extractObjectJsonByPath(response, path);
            return objectMapper.readValue(objectJson, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T extractObjectValueByPath(HttpStatusCodeException error, String path, Class<T> type) {
        return extractObjectValueByPath(error.getResponseBodyAsString(), path, type);
    }
}
