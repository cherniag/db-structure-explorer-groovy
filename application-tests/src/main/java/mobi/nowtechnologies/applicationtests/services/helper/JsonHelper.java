package mobi.nowtechnologies.applicationtests.services.helper;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;

import org.springframework.stereotype.Component;

@Component
@Deprecated
/**
 * Please do not use it!
 */ public class JsonHelper {

    public static final String USER_PATH = "$.response.data[0].user";
    public static final String EMAIL_ACTIVATION_PATH = "$.response.data[0]";
    public static final String PHONE_NUMBER_PATH = "$.response.data[0].phoneActivation";

    private ObjectMapper objectMapper = new ObjectMapper();

    public String extractObjectJsonByPath(String rawResponseString, String path) {
        JsonPath responseUserPath = JsonPath.compile(path);
        JSONObject read = responseUserPath.read(rawResponseString);
        return read.toJSONString();
    }

    public Map<String, Object> extractObjectMapByPath(String rawResponseString, String path) {
        JsonPath responseUserPath = JsonPath.compile(path);
        return responseUserPath.<JSONObject>read(rawResponseString);
    }

    //
    // Object API
    //
    public <T> T extractObjectValueByPath(String response, String path, Class<T> type) {
        try {
            String objectJson = extractObjectJsonByPath(response, path);
            return objectMapper.readValue(objectJson, type);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
