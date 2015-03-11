package mobi.nowtechnologies.server.service;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailTemplateProcessor {

    private static final Pattern tokenPatter = Pattern.compile("%([^%]+)%");


    /**
     * Method searches for sequences framed by %-sign and replaces them with values from model map.
     *
     * @param templateString - a string with framed sequences
     * @param model          - a map with key - values
     * @return processed string
     */
    public static String processTemplateString(String templateString, Map<String, String> model) {
        StringBuilder output = new StringBuilder();
        Matcher matcher = tokenPatter.matcher(templateString);

        int cursor = 0;
        while (matcher.find()) {
            int tokenStart = matcher.start();
            int tokenEnd = matcher.end();
            int keyStart = matcher.start(1);
            int keyEnd = matcher.end(1);

            output.append(templateString.substring(cursor, tokenStart));

            String token = templateString.substring(tokenStart, tokenEnd);
            String key = templateString.substring(keyStart, keyEnd);

            if (model.containsKey(key)) {
                String value = model.get(key);
                output.append(value);
            } else {
                output.append(token);
            }

            cursor = tokenEnd;
        }
        output.append(templateString.substring(cursor));

        return output.toString();
    }
}
