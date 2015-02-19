package mobi.nowtechnologies.shared.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    public static Map<String, String> build(String props) {
        Map<String, String> attribs = new HashMap<>();
        for (String pair : props.split(";")) {
            String[] keyValue = pair.split("=");
            attribs.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return attribs;
    }
}
