package mobi.nowtechnologies.applicationtests.services.util;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * "application-{name}; version: {ver}" -> "application-MQ; version: 1.65"
 * if
 * 1) name=MQ and
 * 2) ver=1.65
 *
 */
@Component
public class SimpleInterpolator {
    public String interpolate(String template, Map<String, Object> model) {
        String interpolated = template;

        for (Map.Entry<String, ?> entry : model.entrySet()) {
            interpolated = interpolated.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        return interpolated;
    }


}
