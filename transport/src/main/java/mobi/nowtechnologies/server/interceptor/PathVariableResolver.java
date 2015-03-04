package mobi.nowtechnologies.server.interceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Alexsandr_Kolpakov Date: 12/17/13 Time: 3:55 PM
 */
public class PathVariableResolver {

    public static final String PATH_DELIM = "/";

    private String[] tokens;

    public PathVariableResolver() {
    }

    public PathVariableResolver(HttpServletRequest request) {
        this.tokens = request.getRequestURI().split(PATH_DELIM);
    }

    public String resolveCommunityUri(HttpServletRequest request) {
        String[] tokens = request.getRequestURI().split(PATH_DELIM);
        return tokens.length >= 3 ?
               tokens[tokens.length - 3] :
               null;
    }

    public String resolveCommunityUri() {
        return tokens.length >= 3 ?
               tokens[tokens.length - 3] :
               null;
    }

    public String resolveApiVersion() {
        return tokens.length >= 2 ?
               tokens[tokens.length - 2] :
               null;
    }

    public String resolveCommandName() {
        return tokens.length >= 1 ?
               tokens[tokens.length - 1] :
               null;
    }
}
