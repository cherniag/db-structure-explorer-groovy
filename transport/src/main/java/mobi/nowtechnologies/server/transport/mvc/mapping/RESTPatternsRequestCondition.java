package mobi.nowtechnologies.server.transport.mvc.mapping;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.util.UrlPathHelper;

/**
 * User: Alexsandr_Kolpakov Date: 12/10/13 Time: 11:16 AM
 */
public class RESTPatternsRequestCondition extends AbstractRequestCondition<PatternsRequestCondition> {

    private final boolean useSuffixPatternMatch;
    private final boolean useTrailingSlashMatch;
    private PatternsRequestCondition patternsRequestCondition;
    private UrlPathHelper urlPathHelper;
    private PathMatcher pathMatcher;

    public RESTPatternsRequestCondition(String[] patterns, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch) {
        this.urlPathHelper = urlPathHelper;
        this.pathMatcher = pathMatcher;
        this.useSuffixPatternMatch = useSuffixPatternMatch;
        this.useTrailingSlashMatch = useTrailingSlashMatch;
        this.patternsRequestCondition = new PatternsRequestCondition(patterns, urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch);
    }

    @Override
    protected Collection<?> getContent() {
        return patternsRequestCondition.getPatterns();
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public PatternsRequestCondition combine(PatternsRequestCondition other) {
        return patternsRequestCondition.combine(other);
    }

    @Override
    public PatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        PatternsRequestCondition result = patternsRequestCondition.getMatchingCondition(request);
        if (result != null) {
            return result;
        }

        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        List<String> matches = new ArrayList<String>();
        for (String pattern : patternsRequestCondition.getPatterns()) {
            String match = getRESTMatchingPattern(pattern, lookupPath);
            if (match != null) {
                matches.add(match);
            }
        }

        Collections.sort(matches, this.pathMatcher.getPatternComparator(lookupPath));
        return matches.isEmpty() ?
               null :
               new PatternsRequestCondition(matches.toArray(new String[matches.size()]), this.urlPathHelper, this.pathMatcher, this.useSuffixPatternMatch, this.useTrailingSlashMatch);
    }

    protected String getRESTMatchingPattern(String pattern, String lookupPath) {
        if (this.useSuffixPatternMatch) {
            int lastSubpathStart = pattern.lastIndexOf('/');
            lastSubpathStart = lastSubpathStart < 0 ?
                               0 :
                               lastSubpathStart;
            boolean hasSuffix = pattern.indexOf(lastSubpathStart, '.') != -1;
            if (!hasSuffix && this.pathMatcher.match(pattern + ".*", lookupPath)) {
                return pattern + ".*";
            }
        }

        return null;
    }

    @Override
    public int compareTo(PatternsRequestCondition other, HttpServletRequest request) {
        return patternsRequestCondition.compareTo(other, request);
    }
}
