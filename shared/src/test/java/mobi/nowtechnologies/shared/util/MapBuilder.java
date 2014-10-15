package mobi.nowtechnologies.shared.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    public static Map<String, String> build(String props) {
        Map<String, String> attribs = new HashMap<String, String>();
        for (String pair : props.split(";")) {
            String[] keyValue = pair.split("=");
            attribs.put(keyValue[0], keyValue[1]);
        }
        return attribs;
    }
}
